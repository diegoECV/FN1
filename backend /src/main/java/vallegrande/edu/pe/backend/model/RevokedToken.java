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
@Document(collection = "revoked_tokens")
public class RevokedToken {
    @Id
    private String id;
    
    private String tokenSignature;
    private Instant revokedAt;
    private Instant expiresAt; // Para limpieza automática
    private String reason;
}
