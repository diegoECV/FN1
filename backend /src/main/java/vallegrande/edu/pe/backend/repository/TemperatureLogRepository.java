package vallegrande.edu.pe.backend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.backend.model.TemperatureLog;

public interface TemperatureLogRepository extends ReactiveMongoRepository<TemperatureLog, String> {
    Flux<TemperatureLog> findByShipmentId(Integer shipmentId);
    Flux<TemperatureLog> findByContainerNumber(String containerNumber);
}
