package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.edu.pe.backend.model.Provider;

public interface ProviderRepository extends ReactiveMongoRepository<Provider, String> {
}
