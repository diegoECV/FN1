package vallegrande.edu.pe.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("SHIPMENTS")
public class Shipment {
    @Id
    private Integer shipmentId;
    private Integer orderId;
    private String blNumber;
    private String bookingNumber;
    private String freightForwarder;
    private String vesselName;
    private String containerNumber;
    private String departurePort;
    private String arrivalPort;
    private LocalDateTime departureDate;
    private LocalDateTime estimatedArrival;
    private Double temperatureSetpoint;
    private String status;
}
