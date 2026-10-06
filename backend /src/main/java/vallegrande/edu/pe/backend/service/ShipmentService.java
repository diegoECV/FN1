package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Shipment;
import vallegrande.edu.pe.backend.repository.ShipmentRepository;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository repository;

    public Flux<Shipment> findAll() { return repository.findAll(); }
    public Mono<Shipment> findById(Integer id) { return repository.findById(id); }
    public Mono<Shipment> save(Shipment obj) { return repository.save(obj); }
    public Mono<Shipment> update(Integer id, Shipment obj) {
        obj.setShipmentId(id);
        return repository.save(obj);
    }
}
