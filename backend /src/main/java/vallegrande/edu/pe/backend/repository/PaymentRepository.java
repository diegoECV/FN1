package vallegrande.edu.pe.backend.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Payment;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, Integer> {
    Flux<Payment> findByOrderId(Integer orderId);
    Mono<Payment> findByReferenceCode(String referenceCode);
}
