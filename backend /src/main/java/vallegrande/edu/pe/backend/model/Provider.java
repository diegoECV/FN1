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
@Document(collection = "providers")
public class Provider {
    @Id
    private String id;
    
    private String documentType;
    private String documentNumber;
    private String companyName;
    private String contactName;
    private String country;
    private String email;
    private String phone;
    private String address;
    
    // Campos Específicos para Agricultores/Proveedores Peruanos
    private String ubigeo;
    private String bankAccountCci;
    private String certificationStatus;
}
