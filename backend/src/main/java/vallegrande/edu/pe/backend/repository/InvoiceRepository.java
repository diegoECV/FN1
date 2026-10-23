package vallegrande.edu.pe.backend.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Invoice;

public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Integer> {
    Flux<Invoice> findByOrderId(Integer orderId);
    Mono<Invoice> findByInvoiceNumber(String invoiceNumber);
    Flux<Invoice> findByStatus(String status);
}
