package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.PriceList;
import vallegrande.edu.pe.backend.service.PriceListService;

@RestController
@RequestMapping("/api/v1/price-lists")
public class PriceListRest {
    private final PriceListService service;

    public PriceListRest(PriceListService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<PriceList> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<PriceList> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PriceList> create(@RequestBody PriceList obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<PriceList> update(@PathVariable String id, @RequestBody PriceList obj) { return service.update(id, obj); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.deleteById(id); }
}