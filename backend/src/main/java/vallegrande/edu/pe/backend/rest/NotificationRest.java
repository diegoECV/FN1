package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.EmailNotificationRequest;
import vallegrande.edu.pe.backend.service.MailService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationRest {

    private final MailService mailService;

    @Value("${spring.mail.username:notificaciones@visons.com}")
    private String fromEmail;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> sendNotification(@RequestBody EmailNotificationRequest request) {
        log.info("[NotificationRest] Recibida solicitud de notificación por correo: módulo={}, acción={}, entidad={}",
                request.getModule(), request.getActionType(), request.getEntityName());

        Map<String, Object> successMap = new HashMap<>();
        successMap.put("success", true);
        successMap.put("message", "Notificación por correo despachada exitosamente");
        successMap.put("module", request.getModule() != null ? request.getModule() : "GENERAL");
        successMap.put("actionType", request.getActionType() != null ? request.getActionType() : "ACTUALIZACION");
        successMap.put("recipient", request.getTo() != null ? request.getTo() : "operaciones@visons.com");

        return mailService.sendNotificacionActualizacion(request)
                .thenReturn(successMap)
                .onErrorResume(e -> {
                    log.error("[NotificationRest] Error procesando notificación: {}", e.getMessage());
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("success", false);
                    errorMap.put("message", "Error al enviar correo: " + e.getMessage());
                    return Mono.just(errorMap);
                });
    }

    @PostMapping("/test-email")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> sendTestEmail(@RequestBody Map<String, String> body) {
        String to = body.getOrDefault("to", "admin@visons.com");
        log.info("[NotificationRest] Enviando correo de prueba a: {}", to);

        Map<String, Object> successMap = new HashMap<>();
        successMap.put("success", true);
        successMap.put("message", "Correo de prueba enviado con éxito a " + to);
        successMap.put("recipient", to);

        return mailService.sendTestEmail(to)
                .thenReturn(successMap)
                .onErrorResume(e -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("success", false);
                    errorMap.put("message", "Error enviando correo de prueba: " + e.getMessage());
                    return Mono.just(errorMap);
                });
    }

    @GetMapping("/config")
    public Mono<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", true);
        config.put("fromEmail", (fromEmail != null && !fromEmail.isBlank() && !fromEmail.contains("${")) ? fromEmail : "notificaciones@visons.com");
        config.put("provider", "SMTP TLS (Visons Notification Gateway)");
        config.put("status", "ACTIVE");
        return Mono.just(config);
    }
}
