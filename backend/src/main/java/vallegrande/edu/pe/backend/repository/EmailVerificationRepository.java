package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.EmailVerification;

public interface EmailVerificationRepository extends ReactiveMongoRepository<EmailVerification, String> {
    Mono<EmailVerification> findByEmail(String email);
    Mono<EmailVerification> findByVerificationCode(String verificationCode);
}
