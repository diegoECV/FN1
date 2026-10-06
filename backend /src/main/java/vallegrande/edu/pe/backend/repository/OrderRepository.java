package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Integer> {
    Flux<Order> findByClientId(String clientId);
    Flux<Order> findByStatus(String status);
    Mono<Order> findByOrderCode(String orderCode);
}
