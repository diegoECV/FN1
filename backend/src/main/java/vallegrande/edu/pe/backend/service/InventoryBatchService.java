package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.InventoryBatch;
import vallegrande.edu.pe.backend.repository.InventoryBatchRepository;

@Service
public class InventoryBatchService {
    private final InventoryBatchRepository repository;

    public InventoryBatchService(InventoryBatchRepository repository) {
        this.repository = repository;
    }

    public Flux<InventoryBatch> findAll() { return repository.findAll(); }
    public Mono<InventoryBatch> findById(String id) { return repository.findById(id); }
    public Mono<InventoryBatch> save(InventoryBatch obj) { return repository.save(obj); }
    public Mono<InventoryBatch> update(String id, InventoryBatch obj) {
        obj.setId(id);
        return repository.save(obj);
    }
    public Mono<Void> deleteById(String id) { return repository.deleteById(id); }
}
