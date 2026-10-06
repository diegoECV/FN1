package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.AssignmentTracking;

public interface AssignmentTrackingRepository extends ReactiveMongoRepository<AssignmentTracking, String> {
    Mono<AssignmentTracking> findByAssignmentId(Integer assignmentId);
}
