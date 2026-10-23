package vallegrande.edu.pe.backend.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades de fecha y hora para Visons S.A.C.
 * Zona horaria base: América/Lima (UTC-5)
 */
public class DateUtils {

    public static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtils() {}

    // --- Formateo ---

    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    public static String formatDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_FORMATTER) : null;
    }

    public static String formatDisplayDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME_FORMATTER) : null;
    }

    // --- Conversiones ---

    /** Convierte un Instant (MongoDB) a LocalDateTime en zona Lima */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, LIMA_ZONE) : null;
    }

    /** Convierte un LocalDateTime de Lima a Instant (para guardar en MongoDB) */
    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(LIMA_ZONE).toInstant() : null;
    }

    /** Convierte un LocalDate a Instant al inicio del día (00:00) en Lima */
    public static Instant toInstantStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay(LIMA_ZONE).toInstant() : null;
    }

    // --- Utilidades de negocio ---

    /** Verifica si una fecha de expiración ya venció */
    public static boolean isExpired(LocalDate expirationDate) {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /** Verifica si un Instant ya venció */
    public static boolean isExpired(Instant expiresAt) {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    /** Retorna la fecha de hoy en zona Lima */
    public static LocalDate today() {
        return LocalDate.now(LIMA_ZONE);
    }

    /** Retorna el momento actual en zona Lima */
    public static LocalDateTime now() {
        return LocalDateTime.now(LIMA_ZONE);
    }

    /** Retorna el Instant actual */
    public static Instant nowInstant() {
        return Instant.now();
    }
}
