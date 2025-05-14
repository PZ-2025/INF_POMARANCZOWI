package com.orange.bookmanagment.shared.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Reprezentuje ustandaryzowaną strukturę odpowiedzi HTTP w aplikacji.
 * <p>
 * Zawiera metadane takie jak czas odpowiedzi, status, kod oraz dane zwrotne.
 */
@Data
@Builder
public class HttpResponse {
    protected String timeStamp;
    protected HttpStatus httpStatus;
    protected int statusCode;
    protected String reason;
    protected String message;
    private Map<?,?> data;
}
