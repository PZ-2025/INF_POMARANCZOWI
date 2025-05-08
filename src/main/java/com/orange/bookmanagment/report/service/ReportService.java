package com.orange.bookmanagment.report.service;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.report.config.ReportConfig;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.pdf.builder.data.LibraryPdfTableItem;
import com.orange.pdf.service.LibraryPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacja serwisu do generowania raportów PDF
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookExternalService bookExternalService;
    private final ReportConfig reportConfig;

    public String generateInventoryReport(String generatedBy) {
        // Inicjalizacja serwisu PDF
        LibraryPdfService pdfService = new LibraryPdfService();

        // Upewnij się, że katalog raportów istnieje
        ensureReportDirectoryExists();

        // Pobierz wszystkie książki z serwisu zewnętrznego
        List<BookExternalDto> books = bookExternalService.getAllBooks();

        // Konwertuj książki na format używany przez bibliotekę PDF
        List<LibraryPdfTableItem> pdfItems = convertBooksToPdfItems(books);

        // Zlicz książki według statusów
        Map<String, Integer> statusCounts = countBooksByStatus(books);

        // Utwórz nazwę pliku z datą i czasem
        String fileName = "library_inventory_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".pdf";
        String outputPath = Paths.get(reportConfig.getReportDirectory(), fileName).toString();

        // Wygeneruj raport
        pdfService.generateInventoryReport(
                pdfItems,
                statusCounts,
                outputPath,
                generatedBy
        );

        return outputPath;
    }

    public String generateFilteredInventoryReport(String genre, BookStatus status, String publisher, String generatedBy) {
        // Inicjalizacja serwisu PDF
        LibraryPdfService pdfService = new LibraryPdfService();

        // Upewnij się, że katalog raportów istnieje
        ensureReportDirectoryExists();

        // Pobierz wszystkie książki
        List<BookExternalDto> allBooks = bookExternalService.getAllBooks();

        // Filtruj książki według podanych parametrów
        List<BookExternalDto> filteredBooks = allBooks.stream()
                .filter(book -> (genre == null || genre.isEmpty() || book.genre().equalsIgnoreCase(genre)))
                .filter(book -> (status == null || book.status() == status))
                .filter(book -> (publisher == null || publisher.isEmpty() ||
                        (book.publisher() != null && book.publisher().name().equalsIgnoreCase(publisher))))
                .collect(Collectors.toList());

        // Konwertuj książki na format używany przez bibliotekę PDF
        List<LibraryPdfTableItem> pdfItems = convertBooksToPdfItems(filteredBooks);

        // Przygotuj podsumowania statusów, gatunków i wydawców
        Map<String, Integer> statusCounts = countBooksByStatus(filteredBooks);
        Map<String, Integer> genreCounts = countBooksByGenre(filteredBooks);
        Map<String, Integer> publisherCounts = countBooksByPublisher(filteredBooks);

        // Zastosowane filtry do tytułu raportu
        StringBuilder reportTitle = new StringBuilder("Raport biblioteczny");
        if (genre != null && !genre.isEmpty()) {
            reportTitle.append(" - Gatunek: ").append(genre);
        }
        if (status != null) {
            reportTitle.append(" - Status: ").append(translateStatus(status));
        }
        if (publisher != null && !publisher.isEmpty()) {
            reportTitle.append(" - Wydawca: ").append(publisher);
        }

        // Utwórz nazwę pliku z datą i czasem
        String fileName = "library_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".pdf";
        String outputPath = Paths.get(reportConfig.getReportDirectory(), fileName).toString();

        // Wygeneruj raport
        pdfService.generateInventoryReport(
                "Biblioteka Miejska",
                reportTitle.toString(),
                "ul. Akademicka 16",
                "44-100 Gliwice",
                "RPT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001",
                java.time.LocalDate.now(),
                pdfItems,
                statusCounts,
                genreCounts,
                publisherCounts,
                outputPath,
                generatedBy
        );

        return outputPath;
    }

    // Pomocnicze metody do liczenia książek według różnych parametrów
    private Map<String, Integer> countBooksByGenre(List<BookExternalDto> books) {
        Map<String, Integer> counts = new HashMap<>();
        for (BookExternalDto book : books) {
            String genre = book.genre() != null ? book.genre() : "Nieznany";
            counts.put(genre, counts.getOrDefault(genre, 0) + 1);
        }
        return counts;
    }

    private Map<String, Integer> countBooksByPublisher(List<BookExternalDto> books) {
        Map<String, Integer> counts = new HashMap<>();
        for (BookExternalDto book : books) {
            String publisher = (book.publisher() != null && book.publisher().name() != null) ?
                    book.publisher().name() : "Nieznany";
            counts.put(publisher, counts.getOrDefault(publisher, 0) + 1);
        }
        return counts;
    }

//    public String generateStatusReport(BookStatus status, String generatedBy) {
//        // Inicjalizacja serwisu PDF
//        LibraryPdfService pdfService = new LibraryPdfService();
//
//        // Upewnij się, że katalog raportów istnieje
//        ensureReportDirectoryExists();
//
//        // Pobierz książki o określonym statusie z serwisu zewnętrznego
//        List<BookExternalDto> books = bookExternalService.getBooksByStatus(status);
//
//        // Konwertuj książki na format używany przez bibliotekę PDF
//        List<LibraryPdfTableItem> pdfItems = convertBooksToPdfItems(books);
//
//        // Utwórz mapę liczebności (w tym przypadku tylko jeden status)
//        Map<String, Integer> statusCounts = Map.of(
//                translateStatus(status), books.size()
//        );
//
//        // Utwórz nazwę pliku z datą i czasem
//        String statusName = status.toString().toLowerCase();
//        String fileName = "library_" + statusName + "_" +
//                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
//                ".pdf";
//        String outputPath = Paths.get(reportConfig.getReportDirectory(), fileName).toString();
//
//        // Wygeneruj raport
//        pdfService.generateInventoryReport(
//                "Biblioteka Miejska",
//                "Raport książek - " + translateStatus(status),
//                "ul. Akademicka 16",
//                "44-100 Gliwice",
//                "RPT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001",
//                java.time.LocalDate.now(),
//                pdfItems,
//                statusCounts,
//                outputPath,
//                generatedBy
//        );
//
//        return outputPath;
//    }

    /**
     * Upewnia się, że katalog raportów istnieje
     */
    private void ensureReportDirectoryExists() {
        try {
            Path directoryPath = Paths.get(reportConfig.getReportDirectory());
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Nie można utworzyć katalogu raportów: " + e.getMessage());
        }
    }

    /**
     * Konwertuje listę książek z modelu zewnętrznego API do modelu używanego przez bibliotekę PDF
     */
    private List<LibraryPdfTableItem> convertBooksToPdfItems(List<BookExternalDto> books) {
        return books.stream()
                .map(this::convertBookToPdfItem)
                .collect(Collectors.toList());
    }

    /**
     * Konwertuje książkę z modelu zewnętrznego API do modelu używanego przez bibliotekę PDF
     */
    private LibraryPdfTableItem convertBookToPdfItem(BookExternalDto book) {
        return new LibraryPdfTableItem(
                String.valueOf(book.id()),
                book.title(),
                book.authors().stream()
                        .map(author -> author.firstName() + " " + author.lastName())
                        .collect(Collectors.joining(", ")),
                book.publisher() != null ? book.publisher().name() : "",
                translateStatus(book.status()),
                book.genre()
        );
    }

    /**
     * Zlicza książki według statusów
     */
    private Map<String, Integer> countBooksByStatus(List<BookExternalDto> books) {
        Map<String, Integer> statusCounts = new HashMap<>();

        for (BookExternalDto book : books) {
            String status = translateStatus(book.status());
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        return statusCounts;
    }

    /**
     * Tłumaczy status z modelu aplikacji na tekst w języku polskim
     */
    private String translateStatus(BookStatus status) {
        if (status == null) {
            return "Nieznany";
        }

        return switch (status) {
            case AVAILABLE -> "Dostępna";
            case BORROWED -> "Wypożyczona";
            case RESERVED -> "Zarezerwowana";
            case LOST -> "Zagubiona";
//            case DAMAGED -> "Uszkodzona";
//            case ARCHIVED -> "Zarchiwizowana";
        };
    }
}