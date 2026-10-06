package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Certificate;
import vallegrande.edu.pe.backend.repository.CertificateRepository;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository repository;

    public Flux<Certificate> findAll() { return repository.findAll(); }
    public Mono<Certificate> findById(Integer id) { return repository.findById(id); }
    public Mono<Certificate> save(Certificate obj) { return repository.save(obj); }
    public Mono<Certificate> update(Integer id, Certificate obj) {
        obj.setCertificateId(id);
        return repository.save(obj);
    }
}
