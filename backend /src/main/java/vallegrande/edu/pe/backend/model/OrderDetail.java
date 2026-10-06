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
@Table("ORDER_DETAILS")
public class OrderDetail {
    @Id
    private Integer orderDetailId;
    private Integer orderId;
    private String productId;
    private String batchId;
    private Double quantityKg;
    private Integer quantityBoxes;
    private Double unitPrice;
    private Double totalPrice;
}
