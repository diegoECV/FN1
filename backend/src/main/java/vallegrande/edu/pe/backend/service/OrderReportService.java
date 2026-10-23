package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.Order;
import vallegrande.edu.pe.backend.repository.OrderRepository;
import vallegrande.edu.pe.backend.util.ReportExportUtil;
import vallegrande.edu.pe.backend.util.ReportImportUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReportService {

    private final OrderRepository orderRepository;

    private static final String[] HEADERS = {
        "ID Pedido", "Código Orden", "ID Cliente", "Fecha Pedido", "Incoterm", "Estado", "Moneda", "Condición Pago", "Puerto Destino", "Fecha Estimada Envío"
    };

    private List<String> mapOrderToRow(Order o) {
        return List.of(
            o.getOrderId() != null ? String.valueOf(o.getOrderId()) : "",
            o.getOrderCode() != null ? o.getOrderCode() : "",
            o.getClientId() != null ? o.getClientId() : "",
            o.getOrderDate() != null ? o.getOrderDate().toString() : "",
            o.getIncoterm() != null ? o.getIncoterm() : "",
            o.getStatus() != null ? o.getStatus() : "",
            o.getCurrency() != null ? o.getCurrency() : "",
            o.getPaymentTerms() != null ? o.getPaymentTerms() : "",
            o.getDestinationPort() != null ? o.getDestinationPort() : "",
            o.getEstimatedShippingDate() != null ? o.getEstimatedShippingDate().toString() : ""
        );
    }

    private Order mapRowToOrder(List<String> r) {
        Integer orderId = null;
        if (r.size() > 0 && !r.get(0).isBlank()) {
            try { orderId = Integer.parseInt(r.get(0)); } catch (Exception ignored) {}
        }
        String orderCode = r.size() > 1 && !r.get(1).isBlank() ? r.get(1) : "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String clientId = r.size() > 2 && !r.get(2).isBlank() ? r.get(2) : "CLI-001";
        LocalDate orderDate = LocalDate.now();
        if (r.size() > 3 && !r.get(3).isBlank()) {
            try { orderDate = LocalDate.parse(r.get(3)); } catch (Exception ignored) {}
        }
        String incoterm = r.size() > 4 && !r.get(4).isBlank() ? r.get(4) : "FOB";
        String status = r.size() > 5 && !r.get(5).isBlank() ? r.get(5) : "PENDING";
        String currency = r.size() > 6 && !r.get(6).isBlank() ? r.get(6) : "USD";
        String paymentTerms = r.size() > 7 && !r.get(7).isBlank() ? r.get(7) : "30 dias";
        String destinationPort = r.size() > 8 && !r.get(8).isBlank() ? r.get(8) : "Rotterdam";
        LocalDate estimatedShippingDate = orderDate.plusDays(15);
        if (r.size() > 9 && !r.get(9).isBlank()) {
            try { estimatedShippingDate = LocalDate.parse(r.get(9)); } catch (Exception ignored) {}
        }

        return Order.builder()
                .orderId(orderId)
                .orderCode(orderCode)
                .clientId(clientId)
                .orderDate(orderDate)
                .incoterm(incoterm)
                .status(status)
                .currency(currency)
                .paymentTerms(paymentTerms)
                .destinationPort(destinationPort)
                .estimatedShippingDate(estimatedShippingDate)
                .build();
    }

    // --- EXPORTACIÓN ---

    public Mono<byte[]> exportToExcel() {
        return orderRepository.findAll()
            .map(this::mapOrderToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateExcel("Pedidos", HEADERS, rows));
    }

    public Mono<byte[]> exportToPdf() {
        return orderRepository.findAll()
            .map(this::mapOrderToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generatePdf("Reporte de Pedidos de Exportación", HEADERS, rows));
    }

    public Mono<byte[]> exportToCsv() {
        return orderRepository.findAll()
            .map(this::mapOrderToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateCsv(HEADERS, rows));
    }

    // --- IMPORTACIÓN ---

    private Mono<byte[]> extractBytes(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });
    }

    public Mono<ImportResponse> importFromExcel(FilePart filePart) {
        return extractBytes(filePart)
                .publishOn(Schedulers.boundedElastic())
                .map(bytes -> {
                    try {
                        return ReportImportUtil.parseExcel(bytes);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al parsear Excel de pedidos: " + e.getMessage(), e);
                    }
                })
                .flatMap(this::saveImportedRows);
    }

    public Mono<ImportResponse> importFromCsv(FilePart filePart) {
        return extractBytes(filePart)
                .publishOn(Schedulers.boundedElastic())
                .map(bytes -> {
                    try {
                        return ReportImportUtil.parseCsv(bytes);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al parsear CSV de pedidos: " + e.getMessage(), e);
                    }
                })
                .flatMap(this::saveImportedRows);
    }

    public Mono<ImportResponse> importFile(FilePart filePart) {
        String filename = filePart.filename().toLowerCase();
        if (filename.endsWith(".csv")) {
            return importFromCsv(filePart);
        } else {
            return importFromExcel(filePart);
        }
    }

    private Mono<ImportResponse> saveImportedRows(List<List<String>> rows) {
        List<Order> orders = rows.stream()
                .map(this::mapRowToOrder)
                .collect(Collectors.toList());

        return orderRepository.saveAll(orders)
                .collectList()
                .map(saved -> ImportResponse.builder()
                        .success(true)
                        .message("Se importaron " + saved.size() + " pedidos exitosamente.")
                        .totalProcessed(rows.size())
                        .totalImported(saved.size())
                        .errors(new ArrayList<>())
                        .build());
    }
}
