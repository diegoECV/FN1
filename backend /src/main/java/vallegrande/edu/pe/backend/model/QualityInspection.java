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
@Document(collection = "quality_inspections")
public class QualityInspection {
    @Id
    private String id;
    
    private String batchId;
    private String inspectorId;
    private Instant inspectionDate;
    
    // Resultados
    private String result; // Ej. PASSED, REJECTED, CONDITIONALLY_ACCEPTED
    private String notes;
    private List<String> evidenceUrls; // Fotos o documentos
}
