package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.RevokedToken;
import vallegrande.edu.pe.backend.service.RevokedTokenService;

@RestController
@RequestMapping("/api/v1/revoked-tokens")
@RequiredArgsConstructor
public class RevokedTokenRest {
    private final RevokedTokenService service;

    @GetMapping
    public Flux<RevokedToken> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Mono<RevokedToken> findById(@PathVariable String id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RevokedToken> create(@RequestBody RevokedToken obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public Mono<RevokedToken> update(@PathVariable String id, @RequestBody RevokedToken obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.findById(id).flatMap(t -> service.findAll().then()); }
}