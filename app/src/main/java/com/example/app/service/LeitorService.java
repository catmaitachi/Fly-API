package com.example.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.app.model.Aeroporto;
import com.example.app.repository.AeroportoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeitorService {

	private static final String CSV_PATH = "data/airports.csv";

	private final AeroportoRepository repo;

	// ? Usa EventListener para iniciar a importação do CSV ao iniciar a aplicação

	@EventListener(ApplicationReadyEvent.class)
	private void inicializador() {

		try { importarCsv(); } 
        catch (Exception e) { throw new RuntimeException(e); }

	}

	// ? Usa Resource para carregar o arquivo CSV 

	private long importarCsv() {

		Resource r = new ClassPathResource(CSV_PATH);

		return leitor(r);

	}

	private long leitor(Resource resource) {

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

				String paisIso = PaisesService.countryIso2(paisNome);

				if ( paisIso == null ) continue;

				Double lat = ConversorService.parseDouble(latStr);
				Double lon = ConversorService.parseDouble(lonStr);
				Integer alt = ConversorService.feetToMeters(altStr);

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

}
