package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.EmailVerification;
import vallegrande.edu.pe.backend.repository.EmailVerificationRepository;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationRepository repository;

    public Flux<EmailVerification> findAll() { return repository.findAll(); }
    public Mono<EmailVerification> findById(String id) { return repository.findById(id); }
    public Mono<EmailVerification> save(EmailVerification obj) { return repository.save(obj); }
    public Mono<EmailVerification> update(String id, EmailVerification obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
