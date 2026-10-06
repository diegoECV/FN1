package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Certificate;
import vallegrande.edu.pe.backend.service.CertificateService;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateRest {
    private final CertificateService service;

    @GetMapping
    public Flux<Certificate> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<Certificate> findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Certificate> create(@RequestBody Certificate obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<Certificate> update(@PathVariable Integer id, @RequestBody Certificate obj) { return service.update(id, obj); }
}