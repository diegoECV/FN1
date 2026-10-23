package vallegrande.edu.pe.backend.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Client;
import vallegrande.edu.pe.backend.repository.ClientRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ClientService {
    private final ClientRepository repository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    private String currentTimestamp() {
        return LocalDateTime.now().format(ISO_FORMATTER);
    }

    public Flux<Client> findAll() {
        return repository.findAll();
    }

    public Flux<Client> findActiveOnly() {
        return repository.findAll()
                .filter(c -> c.getIsActive() == null || Boolean.TRUE.equals(c.getIsActive()));
    }

    public Flux<Client> findDeletedOnly() {
        return repository.findAll()
                .filter(c -> Boolean.FALSE.equals(c.getIsActive()));
    }

    public Mono<Client> findById(String id) {
        return repository.findById(id);
    }

    public Mono<Client> save(Client obj) {
        if (obj.getId() == null || obj.getId().isBlank()) {
            obj.setId("CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
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

    public Mono<Client> update(String id, Client obj) {
        return repository.findById(id)
                .flatMap(existing -> {
                    obj.setId(id);
                    // Preservar auditoría de creación original
                    obj.setCreatedAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : currentTimestamp());
                    obj.setCreatedBy(existing.getCreatedBy() != null ? existing.getCreatedBy() : "ADMIN");
                    if (obj.getIsActive() == null) {
                        obj.setIsActive(existing.getIsActive() != null ? existing.getIsActive() : true);
                    }
                    // Actualizar auditoría de modificación con fecha y hora actual
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

    /**
     * Eliminación lógica (Soft Delete) requerida por la rúbrica TSE
     */
    public Mono<Client> softDelete(String id) {
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

    /**
     * Restauración de registro eliminado lógicamente requerida por la rúbrica TSE
     */
    public Mono<Client> restore(String id) {
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
