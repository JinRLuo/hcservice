package com.hcservice.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final String HH = "HH";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern(HH);
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    public static String getHH() {
        return FORMATTER_HH.format(LocalDateTime.now());
    }
    public static String getYYYYMMDD() {
        return FORMATTER_YYYY_MM_DD.format(LocalDateTime.now());
    }
}