package vallegrande.edu.pe.backend.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Category;
import vallegrande.edu.pe.backend.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    private String currentTimestamp() {
        return LocalDateTime.now().format(ISO_FORMATTER);
    }

    public Flux<Category> findAll() {
        return repository.findAll();
    }

    public Flux<Category> findActiveOnly() {
        return repository.findAll()
                .filter(c -> c.getIsActive() == null || Boolean.TRUE.equals(c.getIsActive()));
    }

    public Flux<Category> findDeletedOnly() {
        return repository.findAll()
                .filter(c -> Boolean.FALSE.equals(c.getIsActive()));
    }

    public Mono<Category> findById(String id) {
        return repository.findById(id);
    }

    public Mono<Category> save(Category obj) {
        if (obj.getId() == null || obj.getId().isBlank()) {
            obj.setId("CAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
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

    public Mono<Category> update(String id, Category obj) {
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

    public Mono<Category> softDelete(String id) {
        return repository.findById(id)
                .flatMap(c -> {
                    String now = currentTimestamp();
                    c.setIsActive(false);
                    c.setDeletedAt(now);
                    c.setDeletedBy("ADMIN");
                    c.setUpdatedAt(now);
                    c.setUpdatedBy("ADMIN_DELETED");
                    return repository.save(c);
                });
    }

    public Mono<Category> restore(String id) {
        return repository.findById(id)
                .flatMap(c -> {
                    String now = currentTimestamp();
                    c.setIsActive(true);
                    c.setDeletedAt(null);
                    c.setDeletedBy(null);
                    c.setUpdatedAt(now);
                    c.setUpdatedBy("ADMIN_RESTORED");
                    return repository.save(c);
                });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
