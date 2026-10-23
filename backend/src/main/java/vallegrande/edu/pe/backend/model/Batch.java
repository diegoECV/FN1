package vallegrande.edu.pe.backend.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("BATCHES")
public class Batch {
    @Id
    private Integer batchId;
    @Column("product_code")
    private String productId;    // → Product (MongoDB)
    private String providerId;
    private String fieldName;
    private LocalDate harvestDate;
    private LocalDate packingDate;
    private LocalDate productionDate;
    private LocalDate expirationDate;
}
