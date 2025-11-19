package com.example.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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

		Resource resource = new ClassPathResource(CSV_PATH);

		return leitor(resource);

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

				String nome = safe(p, 1);
				String cidade = safe(p, 2);
				String iata = safe(p, 4);
				String latStr = safe(p, 6);
				String lonStr = safe(p, 7);
				String altStr = safe(p, 8);

				// ? Ignora IATAs inválidos

				if ( iata == null || iata.equalsIgnoreCase("\\N") || iata.length() != 3 ) continue;

				String iataKey = iata.toUpperCase(Locale.ROOT);

				// ? Verifica se o aeroporto já existe no repositório

				if ( Boolean.TRUE.equals( repo.existsByIata(iataKey) ) ) continue;

				Double latitude = parseDouble(latStr);
				Double longitude = parseDouble(lonStr);
				Integer altitude = feetToMeters(altStr);

				if ( latitude == null || longitude == null || altitude == null ) continue;

				// ? Cria o objeto Aeroporto e salva no repositório

				Aeroporto a = new Aeroporto();

				a.setNome(nome);
				a.setCidade(cidade);
				a.setPais("XX"); 
				a.setIata(iataKey);
				a.setLatitude(latitude);
				a.setLongitude(longitude);
				a.setAltitude(altitude);

				repo.save(a);
				inseridos++;

			}

		} catch ( IOException e ) { throw new RuntimeException("Erro ao importar aeroportos do CSV", e); }

		return inseridos;

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
