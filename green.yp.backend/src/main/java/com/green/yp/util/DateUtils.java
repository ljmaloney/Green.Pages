package com.green.yp.util;

import org.apache.commons.lang3.StringUtils;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtils {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter ISO_OFFSET_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Converts a string to the specified date type
     * @param dateStr The date string to convert
     * @param targetClass The target date class (LocalDate.class, LocalDateTime.class, etc.)
     * @throws IllegalArgumentException if the string is null/empty or target class is unsupported
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static <T> T parseDate(String dateStr, Class<T> targetClass) {
        if (StringUtils.isBlank(dateStr)) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        String trimmedDate = dateStr.trim();
        Object result;

        try {
            result = switch (targetClass.getName()) {
                case "java.time.LocalDate" -> LocalDate.parse(trimmedDate, ISO_DATE);
                case "java.time.LocalDateTime" -> LocalDateTime.parse(trimmedDate, ISO_DATE_TIME);
                case "java.time.OffsetDateTime" -> OffsetDateTime.parse(trimmedDate, ISO_OFFSET_DATE_TIME);
                case "java.util.Date" -> convertSimpleDate(trimmedDate);
                default -> throw new IllegalArgumentException("Unsupported date type: " + targetClass.getName());
            };
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    String.format("Failed to parse '%s' to %s", dateStr, targetClass.getSimpleName()),
                    e.getParsedString(),
                    e.getErrorIndex()
            );
        }
        return targetClass.cast(result);
    }

    private static Date convertSimpleDate(String trimmedDate) {
        try {
            LocalDate localDate = LocalDate.parse(trimmedDate, ISO_DATE);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            LocalDateTime ldt = LocalDateTime.parse(trimmedDate, ISO_DATE_TIME);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * Safely converts a string to the specified date type, returns null if invalid
     * @param dateStr The date string to convert
     * @param targetClass The target date class (LocalDate.class, LocalDateTime.class, etc.)
     */
    public static <T> T parseDateSafe(String dateStr, Class<T> targetClass) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return parseDate(dateStr, targetClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validates if the string is a valid date for the specified type
     * @param dateStr The date string to validate
     * @param targetClass The target date class (LocalDate.class, LocalDateTime.class, etc.)
     */
    public static boolean isValidDate(String dateStr, Class<?> targetClass) {
        return parseDateSafe(dateStr, targetClass) != null;
    }
}