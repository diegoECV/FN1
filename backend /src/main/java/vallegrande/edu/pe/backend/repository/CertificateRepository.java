package vallegrande.edu.pe.backend.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.Certificate;

public interface CertificateRepository extends ReactiveCrudRepository<Certificate, Integer> {
    Flux<Certificate> findByShipmentId(Integer shipmentId);
    Flux<Certificate> findByBatchId(String batchId);
    Flux<Certificate> findByType(String type);
}
