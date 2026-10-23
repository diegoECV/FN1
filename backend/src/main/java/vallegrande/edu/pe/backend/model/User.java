package vallegrande.edu.pe.backend.model;
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
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password; // Almacenado como hash BCrypt
    private String name;
    private String email;
    private String phone;
    private String role;
    private String department;
    private String position;
    private String documentType;
    private String documentNumber;
    private String lastLogin;

    // ── ESTADO LÓGICO PARA SOFT DELETE ──
    @Builder.Default
    private Boolean isActive = true;

    // ── 4 CAMPOS DE AUDITORÍA OBLIGATORIOS (FECHA Y HORA) ──
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;

    // ── TRAZABILIDAD ADICIONAL DE ELIMINACIÓN ──
    private String deletedAt;
    private String deletedBy;
}
