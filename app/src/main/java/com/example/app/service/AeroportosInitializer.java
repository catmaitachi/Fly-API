package com.example.app.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AeroportosInitializer {

    private final AeroportosImport importService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        // ? Usa EventListener para iniciar a importação do CSV ao iniciar a aplicação

        try { importService.importarCsv(); } 
        catch (Exception e) { throw new RuntimeException(e); }

    }
}
