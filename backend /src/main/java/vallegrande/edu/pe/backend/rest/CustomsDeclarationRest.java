package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.CustomsDeclaration;
import vallegrande.edu.pe.backend.service.CustomsDeclarationService;

@RestController
@RequestMapping("/api/v1/customs-declarations")
@RequiredArgsConstructor
public class CustomsDeclarationRest {
    private final CustomsDeclarationService service;

    @GetMapping
    public Flux<CustomsDeclaration> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<CustomsDeclaration> findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomsDeclaration> create(@RequestBody CustomsDeclaration obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<CustomsDeclaration> update(@PathVariable Integer id, @RequestBody CustomsDeclaration obj) { return service.update(id, obj); }
}