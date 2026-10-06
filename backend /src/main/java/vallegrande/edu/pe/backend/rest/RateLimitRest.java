package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.RateLimit;
import vallegrande.edu.pe.backend.service.RateLimitService;

@RestController
@RequestMapping("/api/v1/rate-limits")
@RequiredArgsConstructor
public class RateLimitRest {
    private final RateLimitService service;

    @GetMapping
    public Flux<RateLimit> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<RateLimit> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RateLimit> create(@RequestBody RateLimit obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<RateLimit> update(@PathVariable String id, @RequestBody RateLimit obj) { return service.update(id, obj); }
}