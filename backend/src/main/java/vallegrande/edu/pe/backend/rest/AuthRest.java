package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.AuthRequest;
import vallegrande.edu.pe.backend.dto.AuthResponse;
import vallegrande.edu.pe.backend.repository.UserRepository;
import vallegrande.edu.pe.backend.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRest {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return userRepository.findByUsername(request.getUsername())
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    AuthResponse response = AuthResponse.builder()
                            .token(token)
                            .username(user.getUsername())
                            .role(user.getRole())
                            .expiresIn(36000L) // 10 horas en segundos
                            .build();
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
