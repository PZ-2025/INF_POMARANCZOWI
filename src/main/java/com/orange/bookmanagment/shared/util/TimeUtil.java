package com.orange.bookmanagment.shared.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Zawiera metody pomocnicze do formatowania czasu w aplikacji.
 */
public class TimeUtil {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Formatuje dany {@link Instant} do standardowego formatu (yyyy-MM-dd HH:mm:ss).
     *
     * @param instant czas do sformatowania
     * @return sformatowany ciąg znaków lub null, jeśli czas jest pusty
     */
    public static String getTimeInStandardFormat(Instant instant) {
        if (instant == null) return null; // <--- DODAJ TO
        return instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Zwraca aktualny czas w formacie domyślnym.
     *
     * @return sformatowany aktualny czas jako String
     */
    public static String getCurrentTimeWithFormat(){
        return LocalDateTime.now().format(formatter);
    }
}
