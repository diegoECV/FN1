package vallegrande.edu.pe.backend.model;
import java.time.Instant;

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
@Document(collection = "traceability_logs")
public class TraceabilityLog {
    @Id
    private String id;
    
    private String batchId;
    private String productId;
    
    private String action; // Ej. HARVESTED, PACKED, SHIPPED
    private String performedBy;
    private Instant timestamp;
    
    private String location;
    private String additionalInfo; // Notas o JSON adicional
}
