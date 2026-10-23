package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.TraceabilityLog;

public interface TraceabilityLogRepository extends ReactiveMongoRepository<TraceabilityLog, String> {
    Flux<TraceabilityLog> findByBatchId(String batchId);
    Flux<TraceabilityLog> findByProductId(String productId);
}
