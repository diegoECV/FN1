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
@Document(collection = "clients")
public class Client {
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
    private String preferredIncoterm;
    private String destinationPort;
    private String paymentTerms;
    private String preferredCurrency;
    private Double creditLimit;
    private String language;
}
