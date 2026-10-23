package vallegrande.edu.pe.backend.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Product;
import vallegrande.edu.pe.backend.repository.ProductRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository repository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    private String currentTimestamp() {
        return LocalDateTime.now().format(ISO_FORMATTER);
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Flux<Product> findActiveOnly() {
        return repository.findAll()
                .filter(p -> p.getIsActive() == null || Boolean.TRUE.equals(p.getIsActive()));
    }

    public Flux<Product> findDeletedOnly() {
        return repository.findAll()
                .filter(p -> Boolean.FALSE.equals(p.getIsActive()));
    }

    public Mono<Product> findById(String id) {
        return repository.findById(id);
    }

    public Mono<Product> save(Product obj) {
        if (obj.getId() == null || obj.getId().isBlank()) {
            obj.setId("PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (obj.getIsActive() == null) {
            obj.setIsActive(true);
        }
        String now = currentTimestamp();
        if (obj.getCreatedAt() == null || obj.getCreatedAt().isBlank()) {
            obj.setCreatedAt(now);
        }
        if (obj.getCreatedBy() == null || obj.getCreatedBy().isBlank()) {
            obj.setCreatedBy("ADMIN");
        }
        obj.setUpdatedAt(now);
        if (obj.getUpdatedBy() == null || obj.getUpdatedBy().isBlank()) {
            obj.setUpdatedBy("ADMIN");
        }
        return repository.save(obj);
    }

    public Mono<Product> update(String id, Product obj) {
        return repository.findById(id)
                .flatMap(existing -> {
                    obj.setId(id);
                    obj.setCreatedAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : currentTimestamp());
                    obj.setCreatedBy(existing.getCreatedBy() != null ? existing.getCreatedBy() : "ADMIN");
                    if (obj.getIsActive() == null) {
                        obj.setIsActive(existing.getIsActive() != null ? existing.getIsActive() : true);
                    }
                    obj.setUpdatedAt(currentTimestamp());
                    if (obj.getUpdatedBy() == null || obj.getUpdatedBy().isBlank()) {
                        obj.setUpdatedBy("ADMIN");
                    }
                    return repository.save(obj);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    obj.setId(id);
                    return save(obj);
                }));
    }

    public Mono<Product> softDelete(String id) {
        return repository.findById(id)
                .flatMap(p -> {
                    String now = currentTimestamp();
                    p.setIsActive(false);
                    p.setDeletedAt(now);
                    p.setDeletedBy("ADMIN");
                    p.setUpdatedAt(now);
                    p.setUpdatedBy("ADMIN_DELETED");
                    return repository.save(p);
                });
    }

    public Mono<Product> restore(String id) {
        return repository.findById(id)
                .flatMap(p -> {
                    String now = currentTimestamp();
                    p.setIsActive(true);
                    p.setDeletedAt(null);
                    p.setDeletedBy(null);
                    p.setUpdatedAt(now);
                    p.setUpdatedBy("ADMIN_RESTORED");
                    return repository.save(p);
                });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
