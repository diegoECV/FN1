package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.AuditLog;
import vallegrande.edu.pe.backend.repository.AuditLogRepository;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repository;

    public Flux<AuditLog> findAll() { return repository.findAll(); }
    public Mono<AuditLog> findById(Long id) { return repository.findById(id); }
    public Mono<AuditLog> save(AuditLog obj) { return repository.save(obj); }
    public Mono<AuditLog> update(Long id, AuditLog obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
