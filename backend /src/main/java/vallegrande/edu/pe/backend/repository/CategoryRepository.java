package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.edu.pe.backend.model.Category;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}
