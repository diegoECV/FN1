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
@Document("assignment_tracking")
public class AssignmentTracking {
    
    @Id
    private String id;
    private Integer assignmentId;
    private List<CustodyEvent> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustodyEvent {
        private String transferredBy;
        private String transferredTo;
        private Instant transferTime;
        private String location;
        private String condition;
        private String status;
        private String comments;
    }
}
