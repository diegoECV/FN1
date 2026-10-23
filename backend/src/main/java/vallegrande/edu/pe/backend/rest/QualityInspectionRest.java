package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.QualityInspection;
import vallegrande.edu.pe.backend.service.QualityInspectionService;

@RestController
@RequestMapping("/api/v1/quality-inspections")
public class QualityInspectionRest {
    private final QualityInspectionService service;

    public QualityInspectionRest(QualityInspectionService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<QualityInspection> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<QualityInspection> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<QualityInspection> create(@RequestBody QualityInspection obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<QualityInspection> update(@PathVariable String id, @RequestBody QualityInspection obj) { return service.update(id, obj); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) { return service.deleteById(id); }
}