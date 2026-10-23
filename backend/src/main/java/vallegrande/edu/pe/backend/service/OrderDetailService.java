package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.OrderDetail;
import vallegrande.edu.pe.backend.repository.OrderDetailRepository;

@Service
public class OrderDetailService {
    private final OrderDetailRepository repository;

    public OrderDetailService(OrderDetailRepository repository) {
        this.repository = repository;
    }

    public Flux<OrderDetail> findAll() { return repository.findAll(); }
    public Mono<OrderDetail> findById(Integer id) { return repository.findById(id); }
    public Mono<OrderDetail> save(OrderDetail obj) { return repository.save(obj); }
    public Mono<OrderDetail> update(Integer id, OrderDetail obj) {
        obj.setOrderDetailId(id);
        return repository.save(obj);
    }
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
