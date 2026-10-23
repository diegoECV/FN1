package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Payment;
import vallegrande.edu.pe.backend.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentRest {
    private final PaymentService service;

    public PaymentRest(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Payment> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<Payment> findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Payment> create(@RequestBody Payment obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<Payment> update(@PathVariable Integer id, @RequestBody Payment obj) { return service.update(id, obj); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) { return service.deleteById(id); }
}