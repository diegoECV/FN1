package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.edu.pe.backend.model.Provider;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProviderRepository extends ReactiveMongoRepository<Provider, String> {
    Flux<Provider> findByIsActive(Boolean isActive);
    Mono<Provider> findByDocumentNumber(String documentNumber);
}
