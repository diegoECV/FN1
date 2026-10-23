package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.PriceList;
import vallegrande.edu.pe.backend.repository.PriceListRepository;

@Service
public class PriceListService {
    private final PriceListRepository repository;

    public PriceListService(PriceListRepository repository) {
        this.repository = repository;
    }

    public Flux<PriceList> findAll() { return repository.findAll(); }
    public Mono<PriceList> findById(String id) { return repository.findById(id); }
    public Mono<PriceList> save(PriceList obj) { return repository.save(obj); }
    public Mono<PriceList> update(String id, PriceList obj) {
        obj.setId(id);
        return repository.save(obj);
    }
    public Mono<Void> deleteById(String id) { return repository.deleteById(id); }
}
