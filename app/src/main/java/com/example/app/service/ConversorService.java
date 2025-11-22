package com.example.app.service;

import org.springframework.stereotype.Service;

@Service
public class ConversorService {
    
    public static Double parseDouble(String s) {

		// ? Tenta converter a String em Double, retornando null em caso de falha

		try { return s == null ? null : Double.parseDouble(s); } 
		catch (Exception e) { return null; }

	}

	public static Integer parseInt(String s) {

		// ? Tenta converter a String em Inteiro, retornando null em caso de falha

		try { return s == null ? null : Integer.parseInt(s); } 
		catch (Exception e) { return null; }

	}

	public static Integer feetToMeters(String s) {

		// ? Converte pés para metros, retornando null em caso de falha

		Integer feet = parseInt(s);

		if (feet == null) return null;

		return (int) Math.round(feet * 0.3048);
	}

}   
