package vallegrande.edu.pe.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("batch_inspections")
public class BatchInspection {
    @Id
    private String id;
    private Integer batchId;
    private List<InspectionEvent> inspections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionEvent {
        private String inspector;
        private Instant inspectionTime;
        private String qualityStatus;
        private String notes;
    }
}
