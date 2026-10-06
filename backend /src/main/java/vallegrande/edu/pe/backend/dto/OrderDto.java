package vallegrande.edu.pe.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vallegrande.edu.pe.backend.model.OrderTracking.TrackingEvent;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Integer orderId;
    private String clientId;
    private String orderCode;
    private LocalDate orderDate;
    private String incoterm;
    private String status;
    private String statusDescription;
    private String currency;              // campo faltante
    private String paymentTerms;          // campo faltante
    private String destinationPort;       // campo faltante
    private LocalDate estimatedShippingDate; // campo faltante
    private List<TrackingEvent> trackingEvents;
}
