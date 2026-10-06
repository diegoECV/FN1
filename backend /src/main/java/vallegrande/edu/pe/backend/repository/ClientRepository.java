package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.edu.pe.backend.model.Client;

public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
}
