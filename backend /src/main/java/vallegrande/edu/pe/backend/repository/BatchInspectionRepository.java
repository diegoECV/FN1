package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.BatchInspection;

public interface BatchInspectionRepository extends ReactiveMongoRepository<BatchInspection, String> {
    Mono<BatchInspection> findByBatchId(Integer batchId);
}
