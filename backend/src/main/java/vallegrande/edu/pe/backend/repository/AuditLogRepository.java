package vallegrande.edu.pe.backend.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.AuditLog;

public interface AuditLogRepository extends ReactiveCrudRepository<AuditLog, Long> {
    Flux<AuditLog> findByUserId(String userId);
    Flux<AuditLog> findByAction(String action);
    Flux<AuditLog> findByResource(String resource);
}
