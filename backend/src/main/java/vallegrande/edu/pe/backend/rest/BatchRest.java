package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.BatchDto;
import vallegrande.edu.pe.backend.service.BatchService;

@RestController
@RequestMapping("/api/v1/batches")
public class BatchRest {
    private final BatchService batchService;

    public BatchRest(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    public Flux<BatchDto> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) {
        return limit > 0 ? batchService.findAll().take(limit) : batchService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BatchDto> findById(@PathVariable Integer id) {
        return batchService.findById(id);
    }

    @PostMapping
    public Mono<BatchDto> create(@RequestBody BatchDto dto) {
        return batchService.save(dto);
    }

    @PutMapping("/{id}")
    public Mono<BatchDto> update(@PathVariable Integer id, @RequestBody BatchDto dto) {
        return batchService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) {
        return batchService.deleteById(id);
    }
}