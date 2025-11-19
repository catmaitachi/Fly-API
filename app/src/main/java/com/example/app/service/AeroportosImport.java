package com.example.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.app.model.Aeroporto;
import com.example.app.repository.AeroportoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AeroportosImport {

	private static final String CSV_PATH = "data/airports.csv";

	private final AeroportoRepository repo;

	public long importarCsv() {

		// ? Usa Resource para carregar o arquivo CSV 

		Resource r = new ClassPathResource(CSV_PATH);

		return leitor(r);

	}

	public long leitor(Resource resource) {

		// ? Verifica se o arquivo existe

		if ( resource == null || !resource.exists() ) return 0L;

		int inseridos = 0;

		// ? Lê o arquivo linha a linha

		try ( BufferedReader br = new BufferedReader( new InputStreamReader( resource.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
			
			String linha;

			while ( ( linha = br.readLine() ) != null ) {

				if ( linha.isBlank() ) continue;

				// ? Extrai os campos da linha

				String[] p = linha.split(";", -1);

				if (p.length < 9) continue; 

				// ? Ignora caso algum campo obrigatório seja inválido

				for ( int i = 1 ; i < 9 ; i++ ) if ( safe(p , i) == null ) continue;

				String nome = safe(p, 1);
				String cidade = safe(p, 2);
				String paisNome = safe(p, 3);
				String iata = safe(p, 4);
				String latStr = safe(p, 6);
				String lonStr = safe(p, 7);
				String altStr = safe(p, 8);

				String iataKey = iata.toUpperCase(Locale.ROOT);

				// ? Verifica se o aeroporto já existe no repositório

				if ( Boolean.TRUE.equals( repo.existsByIata(iataKey) ) ) continue;

				// ? Converte o nome do país para ISO2

				String paisIso = countryIso2(paisNome);

				if ( paisIso == null ) continue;

				Double lat = parseDouble(latStr);
				Double lon = parseDouble(lonStr);
				Integer alt = feetToMeters(altStr);

				if ( lat == null || lon == null || alt == null ) continue;

				// ? Cria o objeto Aeroporto e salva no repositório

				Aeroporto a = new Aeroporto();

				a.setNome(nome);
				a.setCidade(cidade);
				a.setPais(paisIso);
				a.setIata(iataKey);
				a.setLatitude(lat);
				a.setLongitude(lon);
				a.setAltitude(alt);

				repo.save(a);
				inseridos++;

			}

		} catch ( IOException e ) { throw new RuntimeException("Erro ao importar aeroportos do CSV", e); }

		return inseridos;

	}

	private static String countryIso2( String country ) {

		// ? Valida entrada

		String raw = country == null ? null : country.trim();

		if ( raw == null || raw.isEmpty() ) return null;

		String s = normalizar(raw);

		// ? Se já veio em ISO2 e for válido, retorna upper-case

		if ( s.length() == 2 ) {

			String up = raw.trim().toUpperCase(Locale.ROOT);

			if ( ISO_BY_NAME.containsValue(up) ) return up;

		}

		// ? Tenta converter ISO3 para ISO2

		if ( s.length() == 3 ) {

			String up3 = raw.trim().toUpperCase(Locale.ROOT);

			for ( Map.Entry<String,String> e : ISO3_TO_ISO2.entrySet() ) if ( e.getKey().equals(up3) ) return e.getValue();
			
		}

		// ? Converte nome normalizado para ISO2

		String iso = ISO_BY_NAME.get(s);

		if ( iso != null ) return iso;

		// ? Não encontrado

		return null;
		
	}

	// ? Normaliza string: trim, lower-case root e remove acentos

	private static String normalizar( String s ) {

		String n = Normalizer.normalize( s, Normalizer.Form.NFD );

		n = n.replaceAll("\\p{M}+", "");

		return n.toLowerCase(Locale.ROOT).trim();

	}

	// ? Cria tabelas de mapeamento para países

	private static final Map<String,String> ISO_BY_NAME = nameToIso();
	private static final Map<String,String> ISO3_TO_ISO2 = iso3ToIso2();

	private static Map<String,String> nameToIso() {

		Map<String,String> map = new HashMap<>();

		// ? De todos os países ISO conhecidos

		for ( String code2 : Locale.getISOCountries() ) {

			Locale loc = new Locale.Builder().setRegion(code2).build();

			String nameEn = normalizar( loc.getDisplayCountry(Locale.ENGLISH) );
			if ( !nameEn.isEmpty() ) map.put( nameEn, code2 );

			String nameDef = normalizar( loc.getDisplayCountry() );
			if ( !nameDef.isEmpty() ) map.putIfAbsent( nameDef, code2 );

		}

		// ! Exceções: nomes alternativos comuns que podem aparecer no CSV

		map.put( normalizar("United States"), "US" );
		map.put( normalizar("United States of America"), "US" );
		map.put( normalizar("United Kingdom"), "GB" );
		map.put( normalizar("South Korea"), "KR" );
		map.put( normalizar("North Korea"), "KP" );
		map.put( normalizar("Russia"), "RU" );
		map.put( normalizar("Czech Republic"), "CZ" ); // Czechia
		map.put( normalizar("Ivory Coast"), "CI" ); // Côte d'Ivoire
		map.put( normalizar("Cape Verde"), "CV" ); // Cabo Verde
		map.put( normalizar("Burma"), "MM" ); // Myanmar
		map.put( normalizar("Vatican City"), "VA" ); // Holy See

		return map;
	}

	// ? Mapeia códigos ISO3 para ISO2

	private static Map<String,String> iso3ToIso2() {

		Map<String,String> map = new HashMap<>();

		for ( String code2 : Locale.getISOCountries() ) {
			
			try {

				Locale loc = new Locale.Builder().setRegion(code2).build();

				String iso3 = loc.getISO3Country();

				if ( iso3 != null && !iso3.isEmpty() ) map.put( iso3.toUpperCase(Locale.ROOT), code2 );
			
			} catch ( Exception e ) {}

		}

		return map;

	}

	private static String safe( String[] array, int idex ) {

		// ? Verifica se o indice é válido

		if ( idex < 0 || idex >= array.length ) return null;

		// ? Obtém o valor e o formata ( trim ou null para String vazio )

		String s = array[idex];

		if (s == null) return null;

		s = s.trim();

		// ? Retorna a String ou null se estiver vazio

		return s.isEmpty() ? null : s;

	}

	private static Double parseDouble(String s) {

		// ? Tenta converter a String em Double, retornando null em caso de falha

		try { return s == null ? null : Double.parseDouble(s); } 
		catch (Exception e) { return null; }

	}

	private static Integer parseInt(String s) {

		// ? Tenta converter a String em Inteiro, retornando null em caso de falha

		try { return s == null ? null : Integer.parseInt(s); } 
		catch (Exception e) { return null; }

	}

	private static Integer feetToMeters(String s) {

		// ? Converte pés para metros, retornando null em caso de falha

		Integer feet = parseInt(s);

		if (feet == null) return null;

		return (int) Math.round(feet * 0.3048);
	}

}
