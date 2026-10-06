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
@Table("INVOICES")
public class Invoice {
    @Id
    private Integer invoiceId;
    private Integer orderId;
    private String invoiceNumber;
    private String documentType;
    private Double exchangeRate;
    private String sunatCdrStatus;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private String currency;
    private Double subtotal;
    private Double taxes;
    private Double total;
    private String status;
}
