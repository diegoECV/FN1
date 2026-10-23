package vallegrande.edu.pe.backend.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.edu.pe.backend.model.PriceList;

public interface PriceListRepository extends ReactiveMongoRepository<PriceList, String> {
}
