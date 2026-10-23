package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.ShipmentItem;
import vallegrande.edu.pe.backend.repository.ShipmentItemRepository;

@Service
@RequiredArgsConstructor
public class ShipmentItemService {
    private final ShipmentItemRepository repository;

    public Flux<ShipmentItem> findAll() { return repository.findAll(); }
    public Mono<ShipmentItem> findById(Integer id) { return repository.findById(id); }
    public Mono<ShipmentItem> save(ShipmentItem obj) { return repository.save(obj); }
    public Mono<ShipmentItem> update(Integer id, ShipmentItem obj) {
        obj.setItemId(id);
        return repository.save(obj);
    }
}
