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
@Document(collection = "rate_limits")
public class RateLimit {
    @Id
    private String id;
    
    private String ipAddress;
    private String endpoint;
    private Integer requestCount;
    private Instant windowStart;
    private Instant windowEnd;
}
