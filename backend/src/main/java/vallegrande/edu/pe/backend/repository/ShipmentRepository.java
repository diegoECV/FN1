package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Shipment;

public interface ShipmentRepository extends ReactiveCrudRepository<Shipment, Integer> {
    Flux<Shipment> findByOrderId(Integer orderId);
    Mono<Shipment> findByBlNumber(String blNumber);
}
