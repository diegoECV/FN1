package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Product;
import vallegrande.edu.pe.backend.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductRest {
    private final ProductService service;

    @GetMapping
    public Flux<Product> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<Product> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@RequestBody Product obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<Product> update(@PathVariable String id, @RequestBody Product obj) { return service.update(id, obj); }
}