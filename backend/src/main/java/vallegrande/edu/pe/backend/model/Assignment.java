package vallegrande.edu.pe.backend.model;
import java.time.LocalDate;

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
@Table("ASSIGNMENTS")
public class Assignment {
    @Id
    private Integer assignmentId;
    private Integer orderId;
    private String assignmentType;
    private String assignedTo;
    private String assignedBy;
    private String priority;
    private String status;
    private String notes;
    private LocalDate assignmentDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
}
