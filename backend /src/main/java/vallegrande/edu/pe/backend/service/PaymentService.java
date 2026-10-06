package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Payment;
import vallegrande.edu.pe.backend.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;

    public Flux<Payment> findAll() { return repository.findAll(); }
    public Mono<Payment> findById(Integer id) { return repository.findById(id); }
    public Mono<Payment> save(Payment obj) { return repository.save(obj); }
    public Mono<Payment> update(Integer id, Payment obj) {
        obj.setPaymentId(id);
        return repository.save(obj);
    }
}
