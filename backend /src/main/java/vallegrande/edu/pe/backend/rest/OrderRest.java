package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.OrderDto;
import vallegrande.edu.pe.backend.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderRest {
    private final OrderService orderService;

    @GetMapping
    public Flux<OrderDto> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) {
        return limit > 0 ? orderService.findAll().take(limit) : orderService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<OrderDto> findById(@PathVariable Integer id) {
        return orderService.findById(id);
    }

    @PostMapping
    public Mono<OrderDto> create(@RequestBody OrderDto orderDto) {
        return orderService.save(orderDto);
    }

    @PutMapping("/{id}")
    public Mono<OrderDto> update(@PathVariable Integer id, @RequestBody OrderDto orderDto) {
        return orderService.update(id, orderDto);
    }
}