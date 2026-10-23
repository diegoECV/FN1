package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.InventoryBatch;
import vallegrande.edu.pe.backend.service.InventoryBatchService;

@RestController
@RequestMapping("/api/v1/inventory-batches")
public class InventoryBatchRest {
    private final InventoryBatchService service;

    public InventoryBatchRest(InventoryBatchService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<InventoryBatch> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<InventoryBatch> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InventoryBatch> create(@RequestBody InventoryBatch obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<InventoryBatch> update(@PathVariable String id, @RequestBody InventoryBatch obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.deleteById(id); }
}