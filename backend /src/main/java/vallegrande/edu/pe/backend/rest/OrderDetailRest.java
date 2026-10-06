package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.OrderDetail;
import vallegrande.edu.pe.backend.service.OrderDetailService;

@RestController
@RequestMapping("/api/v1/order-details")
@RequiredArgsConstructor
public class OrderDetailRest {
    private final OrderDetailService service;

    @GetMapping
    public Flux<OrderDetail> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<OrderDetail> findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDetail> create(@RequestBody OrderDetail obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<OrderDetail> update(@PathVariable Integer id, @RequestBody OrderDetail obj) { return service.update(id, obj); }
}