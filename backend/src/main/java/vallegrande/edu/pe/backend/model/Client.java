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
    // ── 14 CAMPOS FUNCIONALES DE NEGOCIO ──
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
