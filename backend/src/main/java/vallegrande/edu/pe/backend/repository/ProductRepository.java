package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findByCategoryId(String categoryId);
    Flux<Product> findByIsActive(Boolean isActive);
    Mono<Product> findByCode(String code);
}
