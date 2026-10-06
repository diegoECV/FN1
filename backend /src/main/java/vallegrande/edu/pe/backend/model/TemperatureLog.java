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
@Document(collection = "temperature_logs")
public class TemperatureLog {
    @Id
    private String id;
    
    private Integer shipmentId;
    private String containerNumber;
    
    private Instant timestamp;
    private Double temperatureCelsius;
    private Double humidityPercentage;
    
    private String location; // Coordenadas o ciudad
    private Boolean alertTriggered; // True si rompió la cadena de frío
}
