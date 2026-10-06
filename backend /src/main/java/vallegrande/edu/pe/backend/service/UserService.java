package vallegrande.edu.pe.backend.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.User;
import vallegrande.edu.pe.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public Flux<User> findAll() { return repository.findAll(); }
    public Mono<User> findById(String id) { return repository.findById(id); }
    public Mono<User> save(User obj) { return repository.save(obj); }
    public Mono<User> update(String id, User obj) {
        obj.setId(id);
        return repository.save(obj);
    }
}
