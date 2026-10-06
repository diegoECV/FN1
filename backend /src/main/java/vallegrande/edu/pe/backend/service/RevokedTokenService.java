package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.RevokedToken;
import vallegrande.edu.pe.backend.repository.RevokedTokenRepository;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {
    private final RevokedTokenRepository repository;

    public Flux<RevokedToken> findAll() { return repository.findAll(); }
    public Mono<RevokedToken> findById(String id) { return repository.findById(id); }
    public Mono<RevokedToken> save(RevokedToken obj) { return repository.save(obj); }
    public Mono<RevokedToken> update(String id, RevokedToken obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
