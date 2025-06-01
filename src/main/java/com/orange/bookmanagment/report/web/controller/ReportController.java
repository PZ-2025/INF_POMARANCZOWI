package com.orange.bookmanagment.report.web.controller;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import com.orange.bookmanagment.report.service.ReportService;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

/**
 * ReportController jest kontrolerem REST API, który obsługuje żądania związane z raportami.
 * Udostępnia endpointy do generowania różnych typów raportów PDF.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final BookExternalService bookExternalService;

    /**
     * Generuje raport inwentaryzacyjny wszystkich książek w bibliotece.
     *
     * @param authentication obiekt autentykacji użytkownika
     * @return ResponseEntity zawierający plik PDF z raportem
     */
    @GetMapping("/inventory")
    public ResponseEntity<?> generateInventoryReport(Authentication authentication) {
        try {
            // Pobierz informacje o użytkowniku z tokenu JWT
            String username = getUsernameFromAuthentication(authentication);

            // Wygeneruj raport
            String reportPath = reportService.generateInventoryReport(username);

            // Zwróć plik jako odpowiedź
            Resource resource = new UrlResource(Path.of(reportPath).toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"library_inventory_report.pdf\"")
                    .body(resource);
        } catch (Exception e) {
            // Zwróć odpowiedź z błędem
            return ResponseEntity.status(OK)
                    .body(HttpResponse.builder()
                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                            .statusCode(OK.value())
                            .httpStatus(OK)
                            .reason("Report generation failed")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Generuje raport inwentaryzacyjny książek z możliwością filtrowania.
     *
     * @param genre filtr gatunku (opcjonalny)
     * @param status filtr statusu (opcjonalny)
     * @param publisher filtr wydawcy (opcjonalny)
     * @param authentication obiekt autentykacji użytkownika
     * @return ResponseEntity zawierający plik PDF z raportem
     */
    @GetMapping("/filtered")
    public ResponseEntity<?> generateFilteredInventoryReport(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "publisher", required = false) String publisher,
            Authentication authentication) {
        try {
            // Pobierz informacje o użytkowniku z tokenu JWT
            String username = getUsernameFromAuthentication(authentication);

            // Konwertuj string statusu na enum (jeśli podany)
            BookStatus bookStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    bookStatus = BookStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(OK)
                            .body(HttpResponse.builder()
                                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                                    .statusCode(OK.value())
                                    .httpStatus(OK)
                                    .reason("Invalid status")
                                    .message("Error: Status '" + status + "' is not valid. Allowed values: " +
                                            String.join(", ",
                                                    java.util.Arrays.stream(BookStatus.values())
                                                            .map(Enum::name)
                                                            .toArray(String[]::new)))
                                    .build());
                }
            }

            // Wygeneruj raport
            String reportPath = reportService.generateFilteredInventoryReport(genre, bookStatus, publisher, username);

            // Utwórz odpowiednią nazwę pliku
            StringBuilder fileName = new StringBuilder("library_report");
            if (genre != null && !genre.isEmpty()) fileName.append("_").append(genre.toLowerCase());
            if (status != null && !status.isEmpty()) fileName.append("_").append(status.toLowerCase());
            if (publisher != null && !publisher.isEmpty()) fileName.append("_").append(publisher.toLowerCase().replace(" ", "_"));
            fileName.append(".pdf");

            // Zwróć plik jako odpowiedź
            Resource resource = new UrlResource(Path.of(reportPath).toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Zwróć odpowiedź z błędem
            return ResponseEntity.status(OK)
                    .body(HttpResponse.builder()
                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                            .statusCode(OK.value())
                            .httpStatus(OK)
                            .reason("Report generation failed")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Zwraca listę dostępnych typów raportów.
     *
     * @return ResponseEntity zawierający informacje o dostępnych raportach
     */
    @GetMapping
    public ResponseEntity<HttpResponse> getAvailableReports() {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Available reports request")
                        .message("List of available report types")
                        .data(Map.of("reports", Map.of(
                                "inventory", "/api/v1/reports/inventory",
                                "popularity", "/api/v1/reports/popularity"

                        )))
                        .build());
    }

    /**
     * Pobiera nazwę użytkownika z obiektu Authentication.
     *
     * @param authentication obiekt autentykacji
     * @return nazwa użytkownika
     */
    private String getUsernameFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Jeśli używane są tokeny JWT, pobierz nazwę użytkownika lub id użytkownika
            if (jwt.hasClaim("preferred_username")) {
                return jwt.getClaimAsString("preferred_username");
            } else if (jwt.hasClaim("user_id")) {
                return "User " + jwt.getClaim("user_id");
            }
        }
        return authentication.getName();
    }

    /**
     * Generuje raport popularności książek na podstawie wypożyczeń.
     *
     * @param genre filtr gatunku (opcjonalny)
     * @param status filtr statusu (opcjonalny)
     * @param startDateStr data początkowa okresu w formacie YYYY-MM-DD (opcjonalna)
     * @param endDateStr data końcowa okresu w formacie YYYY-MM-DD (opcjonalna)
     * @param limit maksymalna liczba książek w raporcie (opcjonalna, domyślnie 10)
     * @param authentication obiekt autentykacji użytkownika
     * @return ResponseEntity zawierający plik PDF z raportem
     */
    @GetMapping("/popularity")
    public ResponseEntity<?> generatePopularityReport(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            Authentication authentication) {
        try {
            // Pobierz informacje o użytkowniku z tokenu JWT
            String username = getUsernameFromAuthentication(authentication);

            // Konwertuj string statusu na enum (jeśli podany)
            BookStatus bookStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    bookStatus = BookStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(OK)
                            .body(HttpResponse.builder()
                                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                                    .statusCode(OK.value())
                                    .httpStatus(OK)
                                    .reason("Invalid status")
                                    .message("Error: Status '" + status + "' is not valid. Allowed values: " +
                                            String.join(", ",
                                                    java.util.Arrays.stream(BookStatus.values())
                                                            .map(Enum::name)
                                                            .toArray(String[]::new)))
                                    .build());
                }
            }

            // Konwertuj string daty na obiekt LocalDate (jeśli podany)
            LocalDate startDate = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateStr);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(OK)
                            .body(HttpResponse.builder()
                                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                                    .statusCode(OK.value())
                                    .httpStatus(OK)
                                    .reason("Invalid start date format")
                                    .message("Error: Start date should be in format YYYY-MM-DD")
                                    .build());
                }
            }

            LocalDate endDate = null;
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(OK)
                            .body(HttpResponse.builder()
                                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                                    .statusCode(OK.value())
                                    .httpStatus(OK)
                                    .reason("Invalid end date format")
                                    .message("Error: End date should be in format YYYY-MM-DD")
                                    .build());
                }
            }

            // Wygeneruj raport
            String reportPath = reportService.generatePopularityReport(genre, bookStatus, startDate, endDate, limit, username);

            // Utwórz odpowiednią nazwę pliku
            StringBuilder fileName = new StringBuilder("popularity_report");
            if (genre != null && !genre.isEmpty()) fileName.append("_").append(genre.toLowerCase());
            if (status != null && !status.isEmpty()) fileName.append("_").append(status.toLowerCase());
            if (startDateStr != null && !startDateStr.isEmpty()) fileName.append("_from_").append(startDateStr);
            if (endDateStr != null && !endDateStr.isEmpty()) fileName.append("_to_").append(endDateStr);
            fileName.append(".pdf");

            // Zwróć plik jako odpowiedź
            Resource resource = new UrlResource(Path.of(reportPath).toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Zwróć odpowiedź z błędem
            return ResponseEntity.status(OK)
                    .body(HttpResponse.builder()
                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                            .statusCode(OK.value())
                            .httpStatus(OK)
                            .reason("Report generation failed")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Generuje raport zalegających użytkowników
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> generateOverdueReport(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Authentication authentication) {
        try {
            String username = getUsernameFromAuthentication(authentication);

            LocalDate startDate = parseDate(startDateStr, "Start date");
            LocalDate endDate = parseDate(endDateStr, "End date");

            if ((startDateStr != null && !startDateStr.isEmpty() && startDate == null) ||
                    (endDateStr != null && !endDateStr.isEmpty() && endDate == null)) {
                return buildInvalidDateResponse();
            }

            String reportPath = reportService.generateOverdueReport(genre, publisher, startDate, endDate, username);

            StringBuilder fileName = new StringBuilder("overdue_report");
            if (genre != null && !genre.isEmpty()) fileName.append("_").append(genre.toLowerCase());
            if (publisher != null && !publisher.isEmpty()) fileName.append("_").append(publisher.toLowerCase().replace(" ", "_"));
            if (startDateStr != null && !startDateStr.isEmpty()) fileName.append("_from_").append(startDateStr);
            if (endDateStr != null && !endDateStr.isEmpty()) fileName.append("_to_").append(endDateStr);
            fileName.append(".pdf");

            Resource resource = new UrlResource(Path.of(reportPath).toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return buildErrorResponse("Overdue report generation failed", e.getMessage());
        }
    }


    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private ResponseEntity<?> buildErrorResponse(String reason, String message) {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason(reason)
                        .message("Error: " + message)
                        .build());
    }

    private ResponseEntity<?> buildInvalidDateResponse() {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Invalid date format")
                        .message("Error: Date should be in format YYYY-MM-DD")
                        .build());
    }



}
