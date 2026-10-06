package vallegrande.edu.pe.backend.model;

import java.time.LocalDate;

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
@Document(collection = "inventory_batches")
public class InventoryBatch {
    @Id
    private String id;
    private Integer batchId;     // → Batch (SQL Server)
    private String productId;    // → Product (MongoDB)
    private String providerId;   // → Provider (MongoDB)
    private String fieldName;
    private LocalDate harvestDate;
    private LocalDate packingDate;
    private LocalDate expirationDate;
    private Double quantity;
}
