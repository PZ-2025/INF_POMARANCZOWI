package com.orange.bookmanagment.report.service;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.loan.api.LoanExternalService;
import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.report.model.BookPopularityData;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import com.orange.bookmanagment.report.config.ReportConfig;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.pdf.builder.data.LibraryPdfTableItem;
import com.orange.pdf.service.LibraryPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final LoanExternalService loanExternalService;
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
        };
    }

    /**
     * Generuje raport popularności książek na podstawie historii wypożyczeń
     *
     * @param genre filtr gatunku książki (opcjonalny)
     * @param status filtr statusu książki (opcjonalny)
     * @param startDate data początkowa okresu (opcjonalna)
     * @param endDate data końcowa okresu (opcjonalna)
     * @param limit liczba książek w rankingu (opcjonalna, domyślnie 10)
     * @param generatedBy nazwa użytkownika generującego raport
     * @return ścieżka do wygenerowanego pliku raportu
     */
    public String generatePopularityReport(String genre, BookStatus status, LocalDate startDate,
                                           LocalDate endDate, Integer limit, String generatedBy) {
        // Inicjalizacja serwisu PDF
        LibraryPdfService pdfService = new LibraryPdfService();

        // Upewnij się, że katalog raportów istnieje
        ensureReportDirectoryExists();

        // Pobierz wszystkie wypożyczenia
        List<LoanExternalDto> allLoans = loanExternalService.getAllLoans();

        // Filtrowanie wypożyczeń według daty (jeśli podano)
        List<LoanExternalDto> filteredLoans = allLoans;
        if (startDate != null || endDate != null) {
            filteredLoans = allLoans.stream()
                    .filter(loan -> startDate == null ||
                            loan.borrowedAt().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(startDate.minusDays(1)) ||
                            loan.borrowedAt().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(startDate))
                    .filter(loan -> endDate == null ||
                            loan.borrowedAt().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(endDate.plusDays(1)) ||
                            loan.borrowedAt().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(endDate))
                    .collect(Collectors.toList());
        }

        // Zlicz wypożyczenia dla każdej książki
        Map<Long, Integer> bookLoanCounts = new HashMap<>();
        for (LoanExternalDto loan : filteredLoans) {
            bookLoanCounts.merge(loan.bookId(), 1, Integer::sum);
        }

        // Pobierz informacje o książkach
        Map<Long, BookExternalDto> booksMap = new HashMap<>();
        for (Long bookId : bookLoanCounts.keySet()) {
            try {
                BookExternalDto book = bookExternalService.getBookForExternal(bookId);
                if (book != null) {
                    booksMap.put(bookId, book);
                }
            } catch (Exception e) {
                // Książka mogła zostać usunięta, pomijamy ją
                System.err.println("Nie znaleziono książki o ID: " + bookId);
            }
        }

        // Filtruj książki według gatunku i statusu (jeśli podano)
        List<Map.Entry<Long, Integer>> filteredEntries = bookLoanCounts.entrySet().stream()
                .filter(entry -> {
                    BookExternalDto book = booksMap.get(entry.getKey());
                    if (book == null) return false;

                    boolean genreMatch = genre == null || genre.isEmpty() ||
                            (book.genre() != null && book.genre().equalsIgnoreCase(genre));

                    boolean statusMatch = status == null || book.status() == status;

                    return genreMatch && statusMatch;
                })
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Ogranicz liczbę książek (jeśli podano limit)
        if (limit != null && limit > 0 && limit < filteredEntries.size()) {
            filteredEntries = filteredEntries.subList(0, limit);
        }

        // Przygotuj dane do raportu
        List<LibraryPdfTableItem> pdfItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : filteredEntries) {
            BookExternalDto book = booksMap.get(entry.getKey());
            if (book != null) {
                String authorsString = book.authors().stream()
                        .map(author -> author.firstName() + " " + author.lastName())
                        .collect(Collectors.joining(", "));

                // Liczba wypożyczeń dodana jako gatunek (zostanie pokazana w tej kolumnie)
                String loanCountDisplay = String.valueOf(entry.getValue());

                pdfItems.add(new LibraryPdfTableItem(
                        String.valueOf(book.id()),
                        book.title(),
                        authorsString,
                        book.publisher() != null ? book.publisher().name() : "",
                        translateStatus(book.status()),
                        book.genre() + " (" + loanCountDisplay + " wypożyczeń)",  // Dodajemy liczbę wypożyczeń do gatunku
                        ""  // Pusty opis, możemy tu dodać więcej informacji w przyszłości
                ));
            }
        }

        // Zlicz gatunki z książek które mają wypożyczenia
        Map<String, Integer> genreLoanCounts = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : bookLoanCounts.entrySet()) {
            BookExternalDto book = booksMap.get(entry.getKey());
            if (book != null && book.genre() != null && !book.genre().isEmpty()) {
                genreLoanCounts.merge(book.genre(), entry.getValue(), Integer::sum);
            }
        }

        // Sortuj gatunki według liczby wypożyczeń
        List<Map.Entry<String, Integer>> sortedGenres = genreLoanCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Przygotuj mapy dla raportu
        Map<String, Integer> statusCounts = new HashMap<>();
        Map<String, Integer> genreCounts = new HashMap<>();

        // Dla każdego gatunku, podaj liczbę wypożyczeń (nie książek)
        for (Map.Entry<String, Integer> entry : sortedGenres) {
            genreCounts.put(entry.getKey() + " (" + entry.getValue() + " wypożyczeń)", 1);
        }

        // Dla statusów, możemy użyć liczby książek
        for (LibraryPdfTableItem item : pdfItems) {
            statusCounts.merge(item.getStatus(), 1, Integer::sum);
        }

        // Tworzy tytuł raportu
        StringBuilder reportTitle = new StringBuilder("System Zarządzania Księgozbiorem - Raport popularności książek");
        if (genre != null && !genre.isEmpty()) {
            reportTitle.append(" - Gatunek: ").append(genre);
        }
        if (status != null) {
            reportTitle.append(" - Status: ").append(translateStatus(status));
        }
        if (startDate != null) {
            reportTitle.append(" - Od: ").append(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (endDate != null) {
            reportTitle.append(" - Do: ").append(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Utwórz nazwę pliku z datą i czasem
        String fileName = "popularity_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".pdf";
        String outputPath = Paths.get(reportConfig.getReportDirectory(), fileName).toString();

        // Wygeneruj raport
        pdfService.generatePopularityReport(
                "Biblioteka Miejska",
                reportTitle.toString(),
                "ul. Akademicka 16",
                "44-100 Gliwice",
                "POP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001",
                startDate, endDate,
                pdfItems,
                genreCounts,  // Przekazujemy gatunki z liczbą wypożyczeń
                statusCounts, // Przekazujemy statusy
                outputPath,
                generatedBy
        );

        return outputPath;
    }

}