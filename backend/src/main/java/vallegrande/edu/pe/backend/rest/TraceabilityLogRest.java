package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.TraceabilityLog;
import vallegrande.edu.pe.backend.service.TraceabilityLogService;

@RestController
@RequestMapping("/api/v1/traceability-logs")
@RequiredArgsConstructor
public class TraceabilityLogRest {
    private final TraceabilityLogService service;

    @GetMapping
    public Flux<TraceabilityLog> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Mono<TraceabilityLog> findById(@PathVariable String id) { return service.findById(id); }

    @GetMapping("/batch/{batchId}")
    public Flux<TraceabilityLog> findByBatch(@PathVariable String batchId) {
        return service.findAll().filter(t -> batchId.equals(t.getBatchId()));
    }

    @GetMapping("/product/{productId}")
    public Flux<TraceabilityLog> findByProduct(@PathVariable String productId) {
        return service.findAll().filter(t -> productId.equals(t.getProductId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TraceabilityLog> create(@RequestBody TraceabilityLog obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public Mono<TraceabilityLog> update(@PathVariable String id, @RequestBody TraceabilityLog obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.findById(id).flatMap(t -> service.findAll().then()); }
}