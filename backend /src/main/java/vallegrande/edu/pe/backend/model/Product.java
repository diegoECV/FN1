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
    
    // Campos Básicos
    private String code;
    private String name;
    private String categoryId;
    private Boolean isActive;
    
    // Campos Agroexportación (Catálogo Comercial)
    private String variety;
    private String caliber;
    private String unitMeasure;
    private String hsCode; // Partida Arancelaria
}
