package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.TemperatureLog;
import vallegrande.edu.pe.backend.service.TemperatureLogService;

@RestController
@RequestMapping("/api/v1/temperature-logs")
@RequiredArgsConstructor
public class TemperatureLogRest {
    private final TemperatureLogService service;

    @GetMapping
    public Flux<TemperatureLog> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Mono<TemperatureLog> findById(@PathVariable String id) { return service.findById(id); }

    @GetMapping("/shipment/{shipmentId}")
    public Flux<TemperatureLog> findByShipment(@PathVariable Integer shipmentId) {
        return service.findAll().filter(t -> shipmentId.equals(t.getShipmentId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TemperatureLog> create(@RequestBody TemperatureLog obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public Mono<TemperatureLog> update(@PathVariable String id, @RequestBody TemperatureLog obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.findById(id).flatMap(t -> service.findAll().then()); }
}