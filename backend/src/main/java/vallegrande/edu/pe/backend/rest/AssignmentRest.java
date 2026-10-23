package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.AssignmentDto;
import vallegrande.edu.pe.backend.service.AssignmentService;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentRest {
    private final AssignmentService assignmentService;

    @GetMapping
    public Flux<AssignmentDto> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) {
        return limit > 0 ? assignmentService.findAll().take(limit) : assignmentService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<AssignmentDto> findById(@PathVariable Integer id) {
        return assignmentService.findById(id);
    }

    @PostMapping
    public Mono<AssignmentDto> create(@RequestBody AssignmentDto dto) {
        return assignmentService.save(dto);
    }

    @PutMapping("/{id}")
    public Mono<AssignmentDto> update(@PathVariable Integer id, @RequestBody AssignmentDto dto) {
        return assignmentService.update(id, dto);
    }
}