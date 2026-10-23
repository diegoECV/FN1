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
@Table("CUSTOMS_DECLARATIONS")
public class CustomsDeclaration {
    @Id
    private Integer declarationId;
    private Integer shipmentId;
    private String damNumber;
    private Double fobValue;
    private String customsAgency;
    private LocalDateTime filingDate;
    private String status;
}
