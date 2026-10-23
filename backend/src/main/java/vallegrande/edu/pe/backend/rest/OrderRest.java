package vallegrande.edu.pe.backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.dto.OrderDto;
import vallegrande.edu.pe.backend.service.OrderReportService;
import vallegrande.edu.pe.backend.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRest {

    private final OrderService orderService;
    private final OrderReportService reportService;

    public OrderRest(OrderService orderService, OrderReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @GetMapping
    public Flux<OrderDto> findAll(@RequestParam(required = false, defaultValue = "-1") int limit) {
        return limit > 0 ? orderService.findAll().take(limit) : serviceFindAll();
    }

    private Flux<OrderDto> serviceFindAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<OrderDto> findById(@PathVariable Integer id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDto> create(@RequestBody OrderDto orderDto) {
        return orderService.save(orderDto);
    }

    @PutMapping("/{id}")
    public Mono<OrderDto> update(@PathVariable Integer id, @RequestBody OrderDto orderDto) {
        return orderService.update(id, orderDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) {
        return orderService.deleteById(id);
    }

    // --- REPORTES Y EXPORTACIÓN ---

    @GetMapping(value = "/export/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<byte[]>> exportExcel() {
        return reportService.exportToExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedidos.xlsx")
                        .body(bytes));
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity<byte[]>> exportPdf() {
        return reportService.exportToPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedidos.pdf")
                        .body(bytes));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public Mono<ResponseEntity<byte[]>> exportCsv() {
        return reportService.exportToCsv()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedidos.csv")
                        .body(bytes));
    }

    // --- CARGA MASIVA E IMPORTACIÓN ---

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importOrders(@RequestPart("file") FilePart filePart) {
        return reportService.importFile(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importOrdersExcel(@RequestPart("file") FilePart filePart) {
        return reportService.importFromExcel(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importOrdersCsv(@RequestPart("file") FilePart filePart) {
        return reportService.importFromCsv(filePart)
                .map(ResponseEntity::ok);
    }
}