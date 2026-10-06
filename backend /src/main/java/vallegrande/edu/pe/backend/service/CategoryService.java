package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Category;
import vallegrande.edu.pe.backend.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    public Flux<Category> findAll() { return repository.findAll(); }
    public Mono<Category> findById(String id) { return repository.findById(id); }
    public Mono<Category> save(Category obj) { return repository.save(obj); }
    public Mono<Category> update(String id, Category obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
