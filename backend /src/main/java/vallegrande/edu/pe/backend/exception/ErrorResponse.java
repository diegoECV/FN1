package vallegrande.edu.pe.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;                    // endpoint que causó el error
    private LocalDateTime timestamp;
    private Map<String, String> details;    // para errores de validación campo a campo
}
