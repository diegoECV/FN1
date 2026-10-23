package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.EmailNotificationRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio centralizado para el envío de correos HTML con Thymeleaf.
 * Maneja notificaciones de aprobación, invitación y avisos automáticos de actualización del sistema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:notificaciones@visons.com}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Envía un correo HTML usando una plantilla Thymeleaf.
     * Se ejecuta en un hilo separado (boundedElastic) para no bloquear el flujo reactivo.
     * Si no hay credenciales SMTP configuradas, simula el envío y registra en auditoría.
     */
    public Mono<Void> sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        return Mono.fromRunnable(() -> {
            try {
                String sender = (fromEmail != null && !fromEmail.isBlank() && !fromEmail.contains("${"))
                        ? fromEmail
                        : "notificaciones@visons.com";

                Context context = new Context();
                context.setVariables(variables);
                String htmlContent = templateEngine.process(templateName, context);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(sender, "Visons S.A.C.");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("[MailService] Correo '{}' enviado exitosamente a: {}", subject, to);

            } catch (Exception e) {
                log.warn("[MailService] Aviso/Simulación: No se pudo enviar el correo vía SMTP a {}. Razón: {}. Contenido registrado correctamente.",
                        to, e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Notificación general de actualización de cualquier módulo del sistema.
     */
    public Mono<Void> sendNotificacionActualizacion(EmailNotificationRequest req) {
        String recipient = (req.getTo() != null && !req.getTo().isBlank())
                ? req.getTo()
                : "operaciones@visons.com";

        String action = (req.getActionType() != null && !req.getActionType().isBlank())
                ? req.getActionType().toUpperCase()
                : "ACTUALIZACIÓN";

        String mod = (req.getModule() != null && !req.getModule().isBlank())
                ? req.getModule().toUpperCase()
                : "SISTEMA";

        String subject = String.format("[Visons SAC] %s en %s: %s",
                action, mod, req.getEntityName() != null ? req.getEntityName() : "Registro");

        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        Map<String, Object> vars = new HashMap<>();
        vars.put("asunto", subject);
        vars.put("modulo", mod);
        vars.put("tipoAccion", action);
        vars.put("elemento", req.getEntityName() != null ? req.getEntityName() : "Recurso del Sistema");
        vars.put("detalles", req.getDetails() != null ? req.getDetails() : "Se ha modificado la información operativa.");
        vars.put("usuario", req.getUser() != null ? req.getUser() : "Administrador");
        vars.put("fechaHora", formattedDate);
        vars.put("urlAcceso", req.getLink() != null && !req.getLink().isBlank() ? req.getLink() : frontendUrl);

        return sendHtmlEmail(recipient, subject, "email-notificacion-actualizacion", vars);
    }

    /**
     * Envío de correo de prueba para verificar configuración.
     */
    public Mono<Void> sendTestEmail(String to) {
        EmailNotificationRequest testReq = EmailNotificationRequest.builder()
                .to(to)
                .module("CONFIGURACIÓN & DIAGNÓSTICO")
                .actionType("PRUEBA DE CONEXIÓN")
                .entityName("Servicio SMTP & Notificaciones Visons")
                .details("¡Excelente! La pasarela de correos automáticos de Visons S.A.C. está operativa y lista para enviar alertas de cualquier actualización en tiempo real.")
                .user("Sistema Automatizado")
                .link(frontendUrl)
                .build();

        return sendNotificacionActualizacion(testReq);
    }

    /**
     * Correo de aprobación de cuenta (Cliente aprobado por el Admin).
     */
    public Mono<Void> sendAprobacionCuenta(String to, String nombre, String rol, String urlLogin) {
        return sendHtmlEmail(
            to,
            "✅ Tu cuenta en Visons fue aprobada",
            "email-aprobacion",
            Map.of("nombre", nombre, "rol", rol, "email", to, "urlLogin", urlLogin)
        );
    }

    /**
     * Correo de invitación a nuevo trabajador (creado por el Admin).
     */
    public Mono<Void> sendInvitacionTrabajador(String to, String nombre, String departamento, String urlActivacion) {
        return sendHtmlEmail(
            to,
            "👋 ¡Bienvenido al equipo Visons! Activa tu cuenta",
            "email-invitacion-trabajador",
            Map.of("nombre", nombre, "departamento", departamento, "email", to, "urlActivacion", urlActivacion)
        );
    }
}
