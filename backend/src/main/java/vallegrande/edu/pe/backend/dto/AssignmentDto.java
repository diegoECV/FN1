package vallegrande.edu.pe.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vallegrande.edu.pe.backend.model.AssignmentTracking.CustodyEvent;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDto {
    private Integer assignmentId;
    private Integer orderId;
    private String assignmentType;   // campo faltante
    private String assignedTo;
    private String assignedBy;       // campo faltante
    private String priority;         // campo faltante
    private String status;
    private String notes;            // campo faltante
    private LocalDate assignmentDate;
    private LocalDate dueDate;       // campo faltante
    private LocalDate completionDate; // campo faltante
    private List<CustodyEvent> custodyEvents;
}
