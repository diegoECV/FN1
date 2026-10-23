package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.RevokedToken;

public interface RevokedTokenRepository extends ReactiveMongoRepository<RevokedToken, String> {
    Mono<RevokedToken> findByTokenSignature(String tokenSignature);
}
