package vallegrande.edu.pe.backend.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.Product;
import vallegrande.edu.pe.backend.service.ProductReportService;
import vallegrande.edu.pe.backend.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Productos (Maestro CRUD en MongoDB)", description = "CRUD completo del maestro de productos agroexportadores en MongoDB: 14+ campos funcionales, 4 campos de auditoría con fecha y hora, borrado lógico, restauración, exportación e importación masiva.")
public class ProductRest {

    private final ProductService service;
    private final ProductReportService reportService;

    public ProductRest(ProductService service, ProductReportService reportService) {
        this.service = service;
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene los productos del maestro en MongoDB. Permite filtrar opcionalmente por estado activo/inactivo.")
    public Flux<Product> findAll(
            @RequestParam(required = false, defaultValue = "-1") @Parameter(description = "Límite de registros a retornar") int limit,
            @RequestParam(required = false) @Parameter(description = "Filtrar por estado: true (activos), false (eliminados lógicamente), omitir (todos)") Boolean active) {
        Flux<Product> result;
        if (Boolean.TRUE.equals(active)) {
            result = service.findActiveOnly();
        } else if (Boolean.FALSE.equals(active)) {
            result = service.findDeletedOnly();
        } else {
            result = service.findAll();
        }
        return limit > 0 ? result.take(limit) : result;
    }

    @GetMapping("/active")
    @Operation(summary = "Listar productos activos", description = "Obtiene exclusivamente los productos que no han sido eliminados lógicamente.")
    public Flux<Product> findActive() {
        return service.findActiveOnly();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar productos eliminados lógicamente", description = "Obtiene los productos eliminados disponibles para ser restaurados.")
    public Flux<Product> findDeleted() {
        return service.findDeletedOnly();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar producto por ID", description = "Recupera un producto específico por su identificador único de MongoDB.")
    public Mono<ResponseEntity<Product>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nuevo producto en MongoDB", description = "Registra un producto agroexportador con más de 12 campos funcionales e inicializa automáticamente los 4 campos de auditoría (createdAt, createdBy, updatedAt, updatedBy) con fecha y hora.")
    public Mono<Product> create(@RequestBody Product obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar producto en MongoDB", description = "Modifica los datos del producto preservando los datos de creación y actualizando automáticamente los campos de auditoría (updatedAt, updatedBy) con la fecha y hora actual.")
    public Mono<Product> update(@PathVariable String id, @RequestBody Product obj) {
        return service.update(id, obj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto (Baja Lógica)", description = "Aplica borrado lógico cambiando isActive=false y registrando fecha y hora de eliminación en auditoría sin destruir los datos.")
    public Mono<ResponseEntity<Product>> delete(@PathVariable String id) {
        return service.softDelete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar producto eliminado (Alta Lógica - PATCH)", description = "Restaura un producto previamente eliminado (isActive=true) actualizando la fecha y hora en los campos de auditoría.")
    public Mono<ResponseEntity<Product>> restorePatch(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar producto eliminado (Alta Lógica - PUT)", description = "Alias PUT para restaurar productos desde clientes HTTP que no admitan PATCH.")
    public Mono<ResponseEntity<Product>> restorePut(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminación física definitiva", description = "Elimina permanentemente el producto de la base de datos MongoDB.")
    public Mono<Void> hardDelete(@PathVariable String id) {
        return service.deleteById(id);
    }

    // --- REPORTES Y EXPORTACIÓN (MÍN. 10 REGISTROS) ---

    @GetMapping(value = "/export/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Exportar productos a Excel (.xlsx)", description = "Genera y descarga un archivo Excel con los datos funcionales y de auditoría de los productos (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportExcel() {
        return reportService.exportToExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.xlsx")
                        .body(bytes));
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar productos a PDF", description = "Genera y descarga un reporte formal en formato PDF con el catálogo de productos (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportPdf() {
        return reportService.exportToPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.pdf")
                        .body(bytes));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    @Operation(summary = "Exportar productos a CSV", description = "Genera y descarga un archivo CSV estructurado con los productos (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportCsv() {
        return reportService.exportToCsv()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.csv")
                        .body(bytes));
    }

    // --- CARGA MASIVA E IMPORTACIÓN (MÍN. 10 REGISTROS) ---

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar productos desde archivo (Excel o CSV)", description = "Carga masiva de mínimo 10 productos asignando automáticamente campos de auditoría.")
    public Mono<ResponseEntity<ImportResponse>> importProducts(@RequestPart("file") FilePart filePart) {
        return reportService.importFile(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar productos desde Excel (.xlsx)", description = "Carga masiva desde archivo Excel validando e insertando registros en MongoDB.")
    public Mono<ResponseEntity<ImportResponse>> importProductsExcel(@RequestPart("file") FilePart filePart) {
        return reportService.importFromExcel(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar productos desde CSV", description = "Carga masiva desde archivo CSV validando e insertando registros en MongoDB.")
    public Mono<ResponseEntity<ImportResponse>> importProductsCsv(@RequestPart("file") FilePart filePart) {
        return reportService.importFromCsv(filePart)
                .map(ResponseEntity::ok);
    }
}