package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.User;
import vallegrande.edu.pe.backend.repository.UserRepository;

/**
 * Servicio de gestión de usuarios.
 * Incluye CRUD completo + lógica de aprobación/invitación con notificación por correo.
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository repository;
    private final MailService mailService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public UserService(UserRepository repository, MailService mailService) {
        this.repository = repository;
        this.mailService = mailService;
    }

    private static final java.time.format.DateTimeFormatter ISO_FORMATTER = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String currentTimestamp() {
        return java.time.LocalDateTime.now().format(ISO_FORMATTER);
    }

    // ===================== CRUD BÁSICO Y AUDITORÍA =====================

    public Flux<User> findAll() { return repository.findAll(); }

    public Flux<User> findActiveOnly() {
        return repository.findAll()
                .filter(u -> u.getIsActive() == null || Boolean.TRUE.equals(u.getIsActive()));
    }

    public Flux<User> findDeletedOnly() {
        return repository.findAll()
                .filter(u -> Boolean.FALSE.equals(u.getIsActive()));
    }

    public Mono<User> findById(String id) { return repository.findById(id); }

    public Mono<User> save(User obj) {
        if (obj.getId() == null || obj.getId().isBlank()) {
            obj.setId("U-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (obj.getIsActive() == null) {
            obj.setIsActive(true);
        }
        String now = currentTimestamp();
        if (obj.getCreatedAt() == null || obj.getCreatedAt().isBlank()) {
            obj.setCreatedAt(now);
        }
        if (obj.getCreatedBy() == null || obj.getCreatedBy().isBlank()) {
            obj.setCreatedBy("ADMIN");
        }
        obj.setUpdatedAt(now);
        if (obj.getUpdatedBy() == null || obj.getUpdatedBy().isBlank()) {
            obj.setUpdatedBy("ADMIN");
        }
        return repository.save(obj);
    }

    public Mono<User> update(String id, User obj) {
        return repository.findById(id)
                .flatMap(existing -> {
                    obj.setId(id);
                    obj.setCreatedAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : currentTimestamp());
                    obj.setCreatedBy(existing.getCreatedBy() != null ? existing.getCreatedBy() : "ADMIN");
                    if (obj.getIsActive() == null) {
                        obj.setIsActive(existing.getIsActive() != null ? existing.getIsActive() : true);
                    }
                    obj.setUpdatedAt(currentTimestamp());
                    if (obj.getUpdatedBy() == null || obj.getUpdatedBy().isBlank()) {
                        obj.setUpdatedBy("ADMIN");
                    }
                    return repository.save(obj);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    obj.setId(id);
                    return save(obj);
                }));
    }

    public Mono<User> softDelete(String id) {
        return repository.findById(id)
                .flatMap(u -> {
                    String now = currentTimestamp();
                    u.setIsActive(false);
                    u.setDeletedAt(now);
                    u.setDeletedBy("ADMIN");
                    u.setUpdatedAt(now);
                    u.setUpdatedBy("ADMIN_DELETED");
                    return repository.save(u);
                });
    }

    public Mono<User> restore(String id) {
        return repository.findById(id)
                .flatMap(u -> {
                    String now = currentTimestamp();
                    u.setIsActive(true);
                    u.setDeletedAt(null);
                    u.setDeletedBy(null);
                    u.setUpdatedAt(now);
                    u.setUpdatedBy("ADMIN_RESTORED");
                    return repository.save(u);
                });
    }

    public Mono<Void> delete(String id) { return repository.deleteById(id); }

    // ===================== LÓGICA DE APROBACIÓN =====================

    /**
     * Aprueba la cuenta de un Cliente (cambia status PENDING → ACTIVE)
     * y le envía un correo de confirmación automáticamente.
     */
    public Mono<User> aprobarCliente(String userId) {
        return repository.findById(userId)
            .flatMap(user -> {
                user.setIsActive(true);
                return repository.save(user)
                    .flatMap(userGuardado -> {
                        String urlLogin = frontendUrl + "/login";
                        return mailService.sendAprobacionCuenta(
                            userGuardado.getEmail(),
                            userGuardado.getName(),
                            userGuardado.getRole(),
                            urlLogin
                        ).thenReturn(userGuardado);
                    });
            })
            .doOnSuccess(u -> log.info("[UserService] Cliente aprobado y correo enviado: {}", u.getEmail()))
            .doOnError(e -> log.error("[UserService] Error al aprobar cliente: {}", e.getMessage()));
    }

    /**
     * Crea la cuenta de un Trabajador (invitado por el Admin)
     * y le envía un correo con el enlace de activación.
     */
    public Mono<User> invitarTrabajador(User nuevoTrabajador) {
        nuevoTrabajador.setIsActive(false); // Inactivo hasta que active su cuenta
        nuevoTrabajador.setRole("WORKER");

        return repository.save(nuevoTrabajador)
            .flatMap(trabajador -> {
                // Token simple: id del usuario en base64 (en producción usar UUID + expiración)
                String token = java.util.Base64.getEncoder()
                    .encodeToString(trabajador.getId().getBytes());
                String urlActivacion = frontendUrl + "/activar-cuenta?token=" + token;

                return mailService.sendInvitacionTrabajador(
                    trabajador.getEmail(),
                    trabajador.getName(),
                    trabajador.getDepartment() != null ? trabajador.getDepartment() : "Operaciones",
                    urlActivacion
                ).thenReturn(trabajador);
            })
            .doOnSuccess(t -> log.info("[UserService] Trabajador invitado y correo enviado: {}", t.getEmail()))
            .doOnError(e -> log.error("[UserService] Error al invitar trabajador: {}", e.getMessage()));
    }

    /**
     * Activa la cuenta de un trabajador usando su ID (decodificado del token).
     */
    public Mono<User> activarCuentaTrabajador(String userId, String nuevaPassword) {
        return repository.findById(userId)
            .flatMap(user -> {
                // En producción, la password debe pasar por BCryptPasswordEncoder
                user.setPassword(nuevaPassword);
                user.setIsActive(true);
                return repository.save(user);
            })
            .doOnSuccess(u -> log.info("[UserService] Cuenta activada exitosamente: {}", u.getEmail()));
    }

    /**
     * Retorna todos los usuarios con cuenta pendiente de aprobación.
     */
    public Flux<User> findPendientes() {
        return repository.findAll()
            .filter(user -> Boolean.FALSE.equals(user.getIsActive()));
    }
}
