package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.OrderTracking;

public interface OrderTrackingRepository extends ReactiveMongoRepository<OrderTracking, String> {
    Mono<OrderTracking> findByOrderId(Integer orderId);
}
