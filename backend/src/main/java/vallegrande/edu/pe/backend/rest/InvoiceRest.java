package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Invoice;
import vallegrande.edu.pe.backend.service.InvoiceService;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceRest {
    private final InvoiceService service;

    public InvoiceRest(InvoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Invoice> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<Invoice> findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Invoice> create(@RequestBody Invoice obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<Invoice> update(@PathVariable Integer id, @RequestBody Invoice obj) { return service.update(id, obj); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) { return service.deleteById(id); }
}