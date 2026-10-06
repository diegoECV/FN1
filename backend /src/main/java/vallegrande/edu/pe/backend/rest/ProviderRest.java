package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Provider;
import vallegrande.edu.pe.backend.service.ProviderService;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderRest {
    private final ProviderService service;

    @GetMapping
    public Flux<Provider> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<Provider> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Provider> create(@RequestBody Provider obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<Provider> update(@PathVariable String id, @RequestBody Provider obj) { return service.update(id, obj); }
}