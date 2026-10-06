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
    private Boolean isActive;
    private String lastLogin;
    private String createdAt;
}
