package vallegrande.edu.pe.backend.model;

import java.time.LocalDateTime;

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
@Table("PAYMENTS")
public class Payment {
    @Id
    private Integer paymentId;
    
    private Integer orderId;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String referenceCode;
    private String notes;
}
