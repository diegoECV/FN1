package vallegrande.edu.pe.backend.util;

/**
 * Constantes globales de la aplicación Visons S.A.C.
 */
public class AppConstants {

    // --- Paginación ---
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final int MAX_PAGE_SIZE = 100;

    // --- Mensajes de respuesta ---
    public static final String SUCCESS_MESSAGE = "Operación exitosa";
    public static final String NOT_FOUND_MESSAGE = "Recurso no encontrado";
    public static final String UNAUTHORIZED_MESSAGE = "No autorizado";
    public static final String VALIDATION_ERROR_MESSAGE = "Error de validación";

    // --- Roles ---
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_VIEWER = "VIEWER";

    // --- Estados de Orden ---
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_IN_TRANSIT = "IN_TRANSIT";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    // --- Estados de Envío ---
    public static final String SHIPMENT_STATUS_SCHEDULED = "SCHEDULED";
    public static final String SHIPMENT_STATUS_LOADING = "LOADING";
    public static final String SHIPMENT_STATUS_AT_SEA = "AT_SEA";
    public static final String SHIPMENT_STATUS_ARRIVED = "ARRIVED";
    public static final String SHIPMENT_STATUS_CUSTOMS = "CUSTOMS_CLEARANCE";
    public static final String SHIPMENT_STATUS_DELIVERED = "DELIVERED";

    // --- Resultados de Inspección ---
    public static final String INSPECTION_PASSED = "PASSED";
    public static final String INSPECTION_REJECTED = "REJECTED";
    public static final String INSPECTION_CONDITIONAL = "CONDITIONALLY_ACCEPTED";

    // --- Acciones de Trazabilidad ---
    public static final String TRACE_ACTION_HARVESTED = "HARVESTED";
    public static final String TRACE_ACTION_PACKED = "PACKED";
    public static final String TRACE_ACTION_LOADED = "LOADED";
    public static final String TRACE_ACTION_SHIPPED = "SHIPPED";
    public static final String TRACE_ACTION_ARRIVED = "ARRIVED";
    public static final String TRACE_ACTION_CUSTOMS = "CUSTOMS_CLEARED";
    public static final String TRACE_ACTION_DELIVERED = "DELIVERED";

    // --- JWT ---
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";

    // --- API ---
    public static final String API_BASE_PATH = "/api/v1";

    private AppConstants() {
        // Prevenir instanciación
    }
}
