package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.CustomsDeclaration;
import vallegrande.edu.pe.backend.repository.CustomsDeclarationRepository;

@Service
@RequiredArgsConstructor
public class CustomsDeclarationService {
    private final CustomsDeclarationRepository repository;

    public Flux<CustomsDeclaration> findAll() { return repository.findAll(); }
    public Mono<CustomsDeclaration> findById(Integer id) { return repository.findById(id); }
    public Mono<CustomsDeclaration> save(CustomsDeclaration obj) { return repository.save(obj); }
    public Mono<CustomsDeclaration> update(Integer id, CustomsDeclaration obj) {
        obj.setDeclarationId(id);
        return repository.save(obj);
    }
}
