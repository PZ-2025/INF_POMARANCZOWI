package com.orange.bookmanagment.report.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Konfiguracja dla modułu raportów
 */
@Configuration
public class ReportConfig {

    @Value("${app.report.directory:${user.home}/bookmanagement/reports}")
    private String reportDirectory;

    /**
     * Inicjalizuje katalog raportów
     *
     * @return ścieżka do katalogu raportów
     * @throws IOException w przypadku błędu tworzenia katalogu
     */
    @Bean
    public Path reportDirectoryPath() throws IOException {
        Path path = Paths.get(reportDirectory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    /**
     * Zwraca skonfigurowaną ścieżkę do katalogu raportów
     *
     * @return ścieżka do katalogu raportów jako String
     */
    public String getReportDirectory() {
        return reportDirectory;
    }
}