package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Provider;
import vallegrande.edu.pe.backend.repository.ProviderRepository;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository repository;

    public Flux<Provider> findAll() { return repository.findAll(); }
    public Mono<Provider> findById(String id) { return repository.findById(id); }
    public Mono<Provider> save(Provider obj) { return repository.save(obj); }
    public Mono<Provider> update(String id, Provider obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
