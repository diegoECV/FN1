package vallegrande.edu.pe.backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 404 Not Found ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFound(
            ResourceNotFoundException ex, ServerHttpRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request)));
    }

    // --- 400 Bad Request (body inválido / tipo incorrecto) ---
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadInput(
            ServerWebInputException ex, ServerHttpRequest request) {

        String msg = "Cuerpo de la solicitud inválido o campo con tipo incorrecto";
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, msg, request)));
    }

    // --- 401 Unauthorized — Token JWT expirado ---
    @ExceptionHandler(ExpiredJwtException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExpiredJwt(
            ExpiredJwtException ex, ServerHttpRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "El token JWT ha expirado", request)));
    }

    // --- 401 Unauthorized — Token JWT inválido o malformado ---
    @ExceptionHandler(JwtException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidJwt(
            JwtException ex, ServerHttpRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "Token JWT inválido", request)));
    }

    // --- Errores HTTP específicos (403, 405, etc.) lanzados manualmente ---
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatus(
            ResponseStatusException ex, ServerHttpRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return Mono.just(ResponseEntity.status(status)
                .body(buildError(status, ex.getReason() != null ? ex.getReason() : ex.getMessage(), request)));
    }

    // --- IllegalArgumentException (validaciones de negocio) ---
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(
            IllegalArgumentException ex, ServerHttpRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request)));
    }

    // --- 500 Catch-all genérico ---
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(
            Exception ex, ServerHttpRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocurrió un error interno en el servidor", request)));
    }

    // --- Helper ---
    private ErrorResponse buildError(HttpStatus status, String message, ServerHttpRequest request) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getPath().value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
