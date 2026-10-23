package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.AuditLog;
import vallegrande.edu.pe.backend.service.AuditLogService;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogRest {
    private final AuditLogService service;

    public AuditLogRest(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<AuditLog> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Mono<AuditLog> findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuditLog> create(@RequestBody AuditLog obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public Mono<AuditLog> update(@PathVariable Long id, @RequestBody AuditLog obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable Long id) { return service.deleteById(id); }
}