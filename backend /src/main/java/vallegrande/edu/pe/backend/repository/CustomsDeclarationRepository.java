package vallegrande.edu.pe.backend.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.CustomsDeclaration;

public interface CustomsDeclarationRepository extends ReactiveCrudRepository<CustomsDeclaration, Integer> {
    Flux<CustomsDeclaration> findByShipmentId(Integer shipmentId);
    Flux<CustomsDeclaration> findByStatus(String status);
}
