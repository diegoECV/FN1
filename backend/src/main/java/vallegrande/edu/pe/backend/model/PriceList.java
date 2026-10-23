package vallegrande.edu.pe.backend.model;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "price_lists")
public class PriceList {
    @Id
    private String id;
    
    private String name;
    private Instant validFrom;
    private Instant validUntil;
    private String currency;
    private List<PriceItem> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceItem {
        private String productId;
        private Double price;
        private Integer minQuantity;
    }
}
