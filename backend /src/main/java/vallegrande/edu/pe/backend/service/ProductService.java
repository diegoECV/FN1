package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Product;
import vallegrande.edu.pe.backend.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Flux<Product> findAll() { return repository.findAll(); }
    public Mono<Product> findById(String id) { return repository.findById(id); }
    public Mono<Product> save(Product obj) { return repository.save(obj); }
    public Mono<Product> update(String id, Product obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
