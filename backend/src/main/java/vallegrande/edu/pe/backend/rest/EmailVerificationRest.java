package vallegrande.edu.pe.backend.rest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.EmailVerification;
import vallegrande.edu.pe.backend.service.EmailVerificationService;

@RestController
@RequestMapping("/api/v1/email-verifications")
@RequiredArgsConstructor
public class EmailVerificationRest {
    private final EmailVerificationService service;

    @GetMapping
    public Flux<EmailVerification> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) { return limit > 0 ? service.findAll().take(limit) : service.findAll(); }
    @GetMapping("/{id}")
    public Mono<EmailVerification> findById(@PathVariable String id) { return service.findById(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmailVerification> create(@RequestBody EmailVerification obj) { return service.save(obj); }
    @PutMapping("/{id}")
    public Mono<EmailVerification> update(@PathVariable String id, @RequestBody EmailVerification obj) { return service.update(id, obj); }
}