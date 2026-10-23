package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.ShipmentItem;

public interface ShipmentItemRepository extends ReactiveCrudRepository<ShipmentItem, Integer> {
    Flux<ShipmentItem> findByShipmentId(Integer shipmentId);
}
