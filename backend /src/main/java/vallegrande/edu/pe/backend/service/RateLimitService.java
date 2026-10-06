package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.RateLimit;
import vallegrande.edu.pe.backend.repository.RateLimitRepository;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final RateLimitRepository repository;

    public Flux<RateLimit> findAll() { return repository.findAll(); }
    public Mono<RateLimit> findById(String id) { return repository.findById(id); }
    public Mono<RateLimit> save(RateLimit obj) { return repository.save(obj); }
    public Mono<RateLimit> update(String id, RateLimit obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
