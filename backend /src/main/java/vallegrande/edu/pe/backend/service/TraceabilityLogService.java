package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.TraceabilityLog;
import vallegrande.edu.pe.backend.repository.TraceabilityLogRepository;

@Service
@RequiredArgsConstructor
public class TraceabilityLogService {
    private final TraceabilityLogRepository repository;

    public Flux<TraceabilityLog> findAll() { return repository.findAll(); }
    public Mono<TraceabilityLog> findById(String id) { return repository.findById(id); }
    public Mono<TraceabilityLog> save(TraceabilityLog obj) { return repository.save(obj); }
    public Mono<TraceabilityLog> update(String id, TraceabilityLog obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
