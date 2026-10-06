package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.InventoryBatch;

public interface InventoryBatchRepository extends ReactiveMongoRepository<InventoryBatch, String> {
    Mono<InventoryBatch> findByBatchId(Integer batchId);    // enlace con Batch SQL
    Flux<InventoryBatch> findByProductId(String productId);
    Flux<InventoryBatch> findByProviderId(String providerId);
}
