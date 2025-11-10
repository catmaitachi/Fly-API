package com.example.app.service;

/* 

! Este código foi escrito por um modelo de linguagem AI, e apenas revisado por mim.
! Ele pode conter erros sutis ou não seguir as melhores práticas de programação.

O código abaixo é responsável por iniciar a importação dos dados dos aeroportos a partir do CSV automaticamente ao iniciar a aplicação.

*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.app.repository.AeroportoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Dispara a importação após a aplicação estar pronta (ApplicationReadyEvent),
 * garantindo que o schema JPA já foi aplicado e o datasource está disponível.
 */
@Component
@RequiredArgsConstructor
public class AeroportosDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(AeroportosDataInitializer.class);

    private final AeroportosImport importService;
    private final AeroportoRepository repository;

    @Value("${app.import.airports.enabled:true}")
    private boolean enabled;

    private volatile boolean ran = false; // evita execução duplicada em alguns ambientes
    private volatile boolean fallbackTried = false;
    private volatile long lastAdded = -1L;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (ran) {
            log.debug("onApplicationReady chamado novamente; ignorando.");
            return;
        }
        ran = true;

        log.info("[AeroportosDataInitializer] Propriedade app.import.airports.enabled={} (env APP_IMPORT_AIRPORTS_ENABLED)", enabled);
        if (!enabled) {
            log.warn("Importação de aeroportos está DESABILITADA – nenhuma ação será executada.");
            return;
        }

        try {
            log.info("Iniciando importação inicial de aeroportos (evento ApplicationReady)...");
            lastAdded = importService.importarDoClasspath();
            long total = repository.count();
            log.info("Importação concluída: adicionados={}, total_na_tabela={}.", lastAdded, total);
            if (total == 0) {
                log.warn("Tabela 'aeroporto' continua vazia após importação inicial – será tentada uma segunda tentativa (fallback) em ~10s.");
            }
        } catch (Exception e) {
            log.error("Falha durante importação inicial de aeroportos", e);
        }
    }

    /**
     * Fallback: tenta novamente após 10s se a tabela permanecer vazia (por exemplo, em casos de race condition de schema em alguns ambientes).
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void fallbackIfNeeded() {
        if (fallbackTried) return;
        if (!enabled) return; // já desabilitado
        long total = repository.count();
        if (total > 0) {
            log.debug("Fallback não necessário: tabela já possui {} registros.", total);
            fallbackTried = true;
            return;
        }
        log.warn("Executando fallback de importação de aeroportos (tabela ainda vazia). Último added={}.", lastAdded);
        try {
            long added = importService.importarDoClasspath();
            long novoTotal = repository.count();
            log.info("Fallback concluído: adicionados={}, total_na_tabela={}.", added, novoTotal);
        } catch (Exception e) {
            log.error("Erro no fallback de importação de aeroportos", e);
        } finally {
            fallbackTried = true; // evita repetir indefinidamente
        }
    }
}
