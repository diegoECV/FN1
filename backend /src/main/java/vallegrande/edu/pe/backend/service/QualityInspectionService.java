package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.QualityInspection;
import vallegrande.edu.pe.backend.repository.QualityInspectionRepository;

@Service
@RequiredArgsConstructor
public class QualityInspectionService {
    private final QualityInspectionRepository repository;

    public Flux<QualityInspection> findAll() { return repository.findAll(); }
    public Mono<QualityInspection> findById(String id) { return repository.findById(id); }
    public Mono<QualityInspection> save(QualityInspection obj) { return repository.save(obj); }
    public Mono<QualityInspection> update(String id, QualityInspection obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
