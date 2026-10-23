package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.OrderDetail;

public interface OrderDetailRepository extends ReactiveCrudRepository<OrderDetail, Integer> {
    Flux<OrderDetail> findByOrderId(Integer orderId);
    Flux<OrderDetail> findByBatchId(String batchId);
}
