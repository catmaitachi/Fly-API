package com.example.app.service;

/* 

! Este código foi escrito por um modelo de linguagem AI, e apenas revisado por mim.
! Ele pode conter erros sutis ou não seguir as melhores práticas de programação.

O código abaixo é um serviço usado para importar os dados dos aeroportos a partir do CSV automaticamente ao iniciar a aplicação.

*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.example.app.model.Aeroporto;
import com.example.app.repository.AeroportoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por importar aeroportos a partir de um arquivo CSV
 * localizado em {@code src/main/resources/data/airports.csv} (no classpath).
 *
 * Regras principais:
 * - Só insere se o IATA (3 letras) ainda não existir no banco (idempotente).
 * - Faz parsing de latitude, longitude e altitude.
 * - Converte o nome do país para código ISO2 (com alguns ajustes/overrides).
 * - Retorna a quantidade de novos registros inseridos.
 */
@Service
@RequiredArgsConstructor
public class AeroportosImport {

	private static final Logger log = LoggerFactory.getLogger(AeroportosImport.class);
	private static final String CLASSPATH_CSV = "classpath:data/airports.csv";

	private final AeroportoRepository repository;
	private final ResourceLoader resourceLoader;

	/**
	 * Lê o CSV de aeroportos do classpath e insere registros que ainda não existam
	 * (chave: IATA). Ideal para ser chamado no startup.
	 *
	 * @return quantidade de aeroportos adicionados (novos)
	 */
	public long importarDoClasspath() {
		Resource resource = resourceLoader.getResource(CLASSPATH_CSV);
		return importar(resource);
	}

	/**
	 * Executa a importação a partir de um {@link Resource} qualquer.
	 * Linhas inválidas ou com dados essenciais ausentes são ignoradas.
	 * A operação é idempotente (verifica existência por IATA antes de salvar).
	 *
	 * @param resource arquivo/stream CSV
	 * @return quantidade de aeroportos inseridos
	 */
	public long importar(Resource resource) {
		if (resource == null || !resource.exists()) {
			log.warn("Recurso CSV não encontrado: {}", resource);
			return 0L;
		}

		int processados = 0;
		int invalidos = 0;
		int duplicados = 0;
		int inseridos = 0;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			String linha;
			while ((linha = br.readLine()) != null) {
				processados++;
				if (linha.isBlank()) continue;

				// CSV utiliza ';' como separador e o parâmetro -1 mantém campos vazios
				// (não descarta colunas faltantes ao final da linha)
				String[] p = linha.split(";", -1);
				if (p.length < 9) { // precisa ao menos até altitude
					invalidos++;
					continue;
				}

				String nome = safe(p, 1);
				String cidade = safe(p, 2);
				String paisNome = safe(p, 3);
				String iata = safe(p, 4);
				String latStr = safe(p, 6);
				String lonStr = safe(p, 7);
				String altStr = safe(p, 8);

				// IATA precisa ser 3 letras; a fonte usa \N para ausente
				if (iata == null || iata.equalsIgnoreCase("\\N") || iata.length() != 3) {
					invalidos++;
					continue;
				}

				String iataKey = iata.toUpperCase(Locale.ROOT);
				// Checa idempotência consultando existência por IATA
				if (Boolean.TRUE.equals(repository.existsByIata(iataKey))) {
					duplicados++;
					continue;
				}

				Double latitude = parseDouble(latStr);
				Double longitude = parseDouble(lonStr);
				Integer altitude = parseInt(altStr);
				// Não insere se qualquer coordenada/altitude faltar ou for inválida
				if (latitude == null || longitude == null || altitude == null) {
					invalidos++;
					continue;
				}

				String iso2 = toIso2CountryCode(paisNome);
				if (iso2 == null) iso2 = "XX"; // fallback para manter 2 caracteres exigidos pela coluna

				Aeroporto a = new Aeroporto();
				a.setNome(nome);
				a.setCidade(cidade);
				a.setPais(iso2);
				a.setIata(iataKey);
				a.setLatitude(latitude);
				a.setLongitude(longitude);
				a.setAltitude(altitude);

				repository.save(a);
				inseridos++;
			}
		} catch (IOException e) {
			log.error("Erro lendo CSV de aeroportos", e);
		}

		log.info("Importação de aeroportos: processados={}, inseridos={}, duplicados={}, inválidos={}",
				processados, inseridos, duplicados, invalidos);
		return inseridos;
	}

	/**
	 * Retorna o valor do índice do array, já trimado; se estiver vazio retorna null.
	 */
	private static String safe(String[] arr, int idx) {
		if (idx < 0 || idx >= arr.length) return null;
		String s = arr[idx];
		if (s == null) return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	/**
	 * Faz parsing para Double, retornando null em caso de erro.
	 */
	private static Double parseDouble(String s) {
		try {
			return s == null ? null : Double.parseDouble(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Faz parsing para Integer, retornando null em caso de erro.
	 */
	private static Integer parseInt(String s) {
		try {
			return s == null ? null : Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}

	// --- Mapeamento País -> ISO2 ---
	private static final Map<String, String> NORMALIZED_COUNTRY_TO_ISO = buildCountryIndex();
	private static final Map<String, String> OVERRIDES = buildOverrides();

	/**
	 * Constrói um índice Nome-do-País(normalizado) -> ISO2 usando Locale.
	 */
	private static Map<String, String> buildCountryIndex() {
		Map<String, String> map = new HashMap<>();
		for (String code : Locale.getISOCountries()) {
			Locale loc = new Locale.Builder().setRegion(code).build();
			String name = loc.getDisplayCountry(Locale.ENGLISH);
			map.put(normalize(name), code);
		}
		return map;
	}

	/**
	 * Overrides para nomes comuns/alternativos que não casam 1:1 com Locale.
	 */
	private static Map<String, String> buildOverrides() {
		Map<String, String> m = new HashMap<>();
		m.put(normalize("Cote d'Ivoire"), "CI"); // Côte d’Ivoire
		m.put(normalize("Ivory Coast"), "CI");
		m.put(normalize("Congo (Brazzaville)"), "CG"); // Republic of the Congo
		m.put(normalize("Congo"), "CG");
		m.put(normalize("Congo (Kinshasa)"), "CD"); // DR Congo
		m.put(normalize("Democratic Republic of the Congo"), "CD");
		m.put(normalize("Greenland"), "GL");
		m.put(normalize("United Kingdom"), "GB");
		m.put(normalize("United States"), "US");
		m.put(normalize("Czech Republic"), "CZ");
		m.put(normalize("Vatican City"), "VA");
		return m;
	}

	/**
	 * Converte o nome do país para o código ISO2 (duas letras), quando possível.
	 */
	private static String toIso2CountryCode(String countryName) {
		if (countryName == null || countryName.isBlank()) return null;
		String n = normalize(countryName);
		if (OVERRIDES.containsKey(n)) return OVERRIDES.get(n);
		return NORMALIZED_COUNTRY_TO_ISO.get(n);
	}

	/**
	 * Normaliza strings para facilitar comparações:
	 * - remove acentos/diacríticos
	 * - lowercase
	 * - padroniza apóstrofos e remove pontuação comum
	 * - comprime espaços
	 */
	private static String normalize(String input) {
		String s = input == null ? "" : input;
		s = Normalizer.normalize(s, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		s = s.toLowerCase(Locale.ROOT);
		s = s.replace("’", "'")
			 .replace("`", "'")
			 .replace("´", "'")
			 .replace("-", " ")
			 .replace("_", " ")
			 .replace("(", " ")
			 .replace(")", " ")
			 .replace(",", " ")
			 .replace(".", " ");
		s = s.trim().replaceAll("\\s+", " ");
		return s;
	}
}
