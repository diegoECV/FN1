package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Invoice;
import vallegrande.edu.pe.backend.repository.InvoiceRepository;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository repository;

    public Flux<Invoice> findAll() { return repository.findAll(); }
    public Mono<Invoice> findById(Integer id) { return repository.findById(id); }
    public Mono<Invoice> save(Invoice obj) { return repository.save(obj); }
    public Mono<Invoice> update(Integer id, Invoice obj) {
        obj.setInvoiceId(id);
        return repository.save(obj);
    }
}
