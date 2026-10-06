package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.TemperatureLog;
import vallegrande.edu.pe.backend.repository.TemperatureLogRepository;

@Service
@RequiredArgsConstructor
public class TemperatureLogService {
    private final TemperatureLogRepository repository;

    public Flux<TemperatureLog> findAll() { return repository.findAll(); }
    public Mono<TemperatureLog> findById(String id) { return repository.findById(id); }
    public Mono<TemperatureLog> save(TemperatureLog obj) { return repository.save(obj); }
    public Mono<TemperatureLog> update(String id, TemperatureLog obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
