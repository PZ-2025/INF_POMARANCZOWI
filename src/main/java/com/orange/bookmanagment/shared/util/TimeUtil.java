package com.orange.bookmanagment.shared.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String getTimeInStandardFormat(Instant instant){
        return instant.atZone(ZoneId.systemDefault()).format(formatter);
    }

    public static String getCurrentTimeWithFormat(){
        return LocalDateTime.now().format(formatter);
    }
}
