package vallegrande.edu.pe.backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.Assignment;

public interface AssignmentRepository extends ReactiveCrudRepository<Assignment, Integer> {
    Flux<Assignment> findByOrderId(Integer orderId);
    Flux<Assignment> findByStatus(String status);
    Flux<Assignment> findByAssignedTo(String assignedTo);
}
