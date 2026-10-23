package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.Batch;

public interface BatchRepository extends ReactiveCrudRepository<Batch, Integer> {
    Flux<Batch> findByProductId(String productId);
    Flux<Batch> findByProviderId(String providerId);
}
