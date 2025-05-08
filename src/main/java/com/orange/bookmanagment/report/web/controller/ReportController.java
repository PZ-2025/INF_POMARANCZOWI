package com.orange.bookmanagment.report.web.controller;

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
import java.util.Map;

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

//    /**
//     * Generuje raport książek o określonym statusie.
//     *
//     * @param status status książek do uwzględnienia w raporcie
//     * @param authentication obiekt autentykacji użytkownika
//     * @return ResponseEntity zawierający plik PDF z raportem
//     */
//    @GetMapping("/status/{status}")
//    public ResponseEntity<?> generateStatusReport(
//            @PathVariable("status") String status,
//            Authentication authentication) {
//        try {
//            // Konwertuj string statusu na enum
//            BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
//
//            // Pobierz informacje o użytkowniku z tokenu JWT
//            String username = getUsernameFromAuthentication(authentication);
//
//            // Wygeneruj raport
//            String reportPath = reportService.generateStatusReport(bookStatus, username);
//
//            // Zwróć plik jako odpowiedź
//            Resource resource = new UrlResource(Path.of(reportPath).toUri());
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=\"library_" + status.toLowerCase() + "_report.pdf\"")
//                    .body(resource);
//        } catch (IllegalArgumentException e) {
//            // Zwróć błąd dla nieprawidłowego statusu
//            return ResponseEntity.status(OK)
//                    .body(HttpResponse.builder()
//                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
//                            .statusCode(OK.value())
//                            .httpStatus(OK)
//                            .reason("Invalid status")
//                            .message("Error: Status '" + status + "' is not valid. Allowed values: " +
//                                    String.join(", ",
//                                            java.util.Arrays.stream(BookStatus.values())
//                                                    .map(Enum::name)
//                                                    .toArray(String[]::new)))
//                            .build());
//        } catch (Exception e) {
//            // Zwróć odpowiedź z błędem
//            return ResponseEntity.status(OK)
//                    .body(HttpResponse.builder()
//                            .timeStamp(TimeUtil.getCurrentTimeWithFormat())
//                            .statusCode(OK.value())
//                            .httpStatus(OK)
//                            .reason("Report generation failed")
//                            .message("Error: " + e.getMessage())
//                            .build());
//        }
//    }

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
                                "byStatus", "/api/v1/reports/status/{status}"
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
}