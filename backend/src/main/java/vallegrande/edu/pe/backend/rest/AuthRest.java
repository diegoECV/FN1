package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.AuthRequest;
import vallegrande.edu.pe.backend.dto.AuthResponse;
import vallegrande.edu.pe.backend.model.AuditLog;
import vallegrande.edu.pe.backend.repository.UserRepository;
import vallegrande.edu.pe.backend.security.JwtUtil;
import vallegrande.edu.pe.backend.service.AuditLogService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRest {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return userRepository.findByUsername(request.getUsername())
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .flatMap(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    AuthResponse response = AuthResponse.builder()
                            .token(token)
                            .username(user.getUsername())
                            .role(user.getRole())
                            .expiresIn(36000L) // 10 horas en segundos
                            .build();
                    AuditLog log = AuditLog.builder()
                            .userId(user.getId() != null ? user.getId() : "U-001")
                            .username(user.getName() != null ? user.getName() : user.getUsername())
                            .action("LOGIN")
                            .resource("System")
                            .resourceId(null)
                            .timestamp(LocalDateTime.now())
                            .changesBefore(null)
                            .changesAfter("{\"role\":\"" + user.getRole() + "\",\"channel\":\"WEB_PORTAL\"}")
                            .ipAddress("127.0.0.1")
                            .build();
                    return auditLogService.save(log)
                            .onErrorResume(e -> Mono.empty())
                            .thenReturn(ResponseEntity.ok(response));
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
