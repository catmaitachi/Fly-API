package com.example.app.service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PaisesService {

    public static String countryIso2( String country ) {

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

		// ? Atribuindo à países ISO conhecidos

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

}
