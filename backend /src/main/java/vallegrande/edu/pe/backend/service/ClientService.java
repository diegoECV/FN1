package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Client;
import vallegrande.edu.pe.backend.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repository;

    public Flux<Client> findAll() { return repository.findAll(); }
    public Mono<Client> findById(String id) { return repository.findById(id); }
    public Mono<Client> save(Client obj) { return repository.save(obj); }
    public Mono<Client> update(String id, Client obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
