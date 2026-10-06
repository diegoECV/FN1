package vallegrande.edu.pe.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        String username;
        try {
            username = jwtUtil.getUsernameFromToken(authToken);
        } catch (Exception e) {
            return Mono.empty(); // token inválido o expirado
        }

        if (username == null || !jwtUtil.validateToken(authToken)) {
            return Mono.empty();
        }

        // Carga el usuario real de MongoDB para obtener su rol actual
        return userRepository.findByUsername(username)
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .map(user -> {
                    String role = user.getRole() != null ? user.getRole() : "USER";
                    List<SimpleGrantedAuthority> authorities =
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    return (Authentication) new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );
                });
    }
}
