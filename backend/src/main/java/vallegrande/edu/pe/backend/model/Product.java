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
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    // ── 14 CAMPOS FUNCIONALES DE NEGOCIO AGROEXPORTADOR ──
    private String code;
    private String name;
    private String scientificName;
    private String categoryId;
    private String variety;
    private String caliber;
    private String unitMeasure;
    private String hsCode; // Partida Arancelaria
    private String storageTemperature;
    private Integer shelfLifeDays;
    private String packagingType;
    private String originRegion;
    private String qualityGrade;
    private String organicCertification;

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
