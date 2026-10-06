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
@Table("AUDIT_LOGS")
public class AuditLog {
    @Id
    private Long id;
    private String userId;
    private String username;
    private String action;
    private String resource;
    private String resourceId;
    private LocalDateTime timestamp;
    private String changesBefore;
    private String changesAfter;
    private String ipAddress;
}
