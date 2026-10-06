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
@Table("CERTIFICATES")
public class Certificate {
    @Id
    private Integer certificateId;
    private Integer shipmentId;
    private String batchId;
    private String type;
    private String issuedBy;
    private String certificateNumber;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String fileUrl;
}
