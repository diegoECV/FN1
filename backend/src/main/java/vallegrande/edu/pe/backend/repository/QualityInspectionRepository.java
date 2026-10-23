package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.QualityInspection;

public interface QualityInspectionRepository extends ReactiveMongoRepository<QualityInspection, String> {
    Flux<QualityInspection> findByBatchId(String batchId);
    Flux<QualityInspection> findByInspectorId(String inspectorId);
}
