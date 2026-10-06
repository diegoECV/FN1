package vallegrande.edu.pe.backend.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("SHIPMENT_ITEMS")
public class ShipmentItem {
    @Id
    private Integer itemId;
    private Integer shipmentId;
    private String productId;
    private Integer quantityPallets;
    private Integer quantityBoxes;
    private Double netWeightKg;
    private Double grossWeightKg;
}
