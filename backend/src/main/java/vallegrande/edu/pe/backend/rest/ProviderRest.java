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
import vallegrande.edu.pe.backend.model.Provider;
import vallegrande.edu.pe.backend.service.ProviderReportService;
import vallegrande.edu.pe.backend.service.ProviderService;

@RestController
@RequestMapping("/api/v1/providers")
@Tag(name = "Proveedores (Maestro CRUD en MongoDB)", description = "CRUD completo del maestro de proveedores agropecuarios en MongoDB: 14+ campos funcionales, 4 campos de auditoría con fecha y hora, borrado lógico, restauración, exportación e importación masiva.")
public class ProviderRest {

    private final ProviderService service;
    private final ProviderReportService reportService;

    public ProviderRest(ProviderService service, ProviderReportService reportService) {
        this.service = service;
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "Listar proveedores", description = "Obtiene los proveedores del maestro en MongoDB. Permite filtrar opcionalmente por estado activo/inactivo.")
    public Flux<Provider> findAll(
            @RequestParam(required = false, defaultValue = "-1") @Parameter(description = "Límite de registros a retornar") int limit,
            @RequestParam(required = false) @Parameter(description = "Filtrar por estado: true (activos), false (eliminados lógicamente), omitir (todos)") Boolean active) {
        Flux<Provider> result;
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
    @Operation(summary = "Listar proveedores activos", description = "Obtiene exclusivamente los proveedores que no han sido eliminados lógicamente.")
    public Flux<Provider> findActive() {
        return service.findActiveOnly();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar proveedores eliminados lógicamente", description = "Obtiene los proveedores eliminados disponibles para ser restaurados.")
    public Flux<Provider> findDeleted() {
        return service.findDeletedOnly();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar proveedor por ID", description = "Recupera un proveedor específico por su identificador único de MongoDB.")
    public Mono<ResponseEntity<Provider>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nuevo proveedor en MongoDB", description = "Registra un proveedor agropecuario con más de 12 campos funcionales e inicializa automáticamente los 4 campos de auditoría (createdAt, createdBy, updatedAt, updatedBy) con fecha y hora.")
    public Mono<Provider> create(@RequestBody Provider obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar proveedor en MongoDB", description = "Modifica los datos del proveedor preservando los datos de creación y actualizando automáticamente los campos de auditoría (updatedAt, updatedBy) con la fecha y hora actual.")
    public Mono<Provider> update(@PathVariable String id, @RequestBody Provider obj) {
        return service.update(id, obj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor (Baja Lógica)", description = "Aplica borrado lógico cambiando isActive=false y registrando fecha y hora de eliminación en auditoría sin destruir los datos.")
    public Mono<ResponseEntity<Provider>> delete(@PathVariable String id) {
        return service.softDelete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar proveedor eliminado (Alta Lógica - PATCH)", description = "Restaura un proveedor previamente eliminado (isActive=true) actualizando la fecha y hora en los campos de auditoría.")
    public Mono<ResponseEntity<Provider>> restorePatch(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar proveedor eliminado (Alta Lógica - PUT)", description = "Alias PUT para restaurar proveedores desde clientes HTTP que no admitan PATCH.")
    public Mono<ResponseEntity<Provider>> restorePut(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminación física definitiva", description = "Elimina permanentemente el proveedor de la base de datos MongoDB.")
    public Mono<Void> hardDelete(@PathVariable String id) {
        return service.deleteById(id);
    }

    // --- REPORTES Y EXPORTACIÓN (MÍN. 10 REGISTROS) ---

    @GetMapping(value = "/export/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Exportar proveedores a Excel (.xlsx)", description = "Genera y descarga un archivo Excel con los datos funcionales y de auditoría de los proveedores (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportExcel() {
        return reportService.exportToExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proveedores.xlsx")
                        .body(bytes));
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar proveedores a PDF", description = "Genera y descarga un reporte formal en formato PDF con la lista de proveedores (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportPdf() {
        return reportService.exportToPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proveedores.pdf")
                        .body(bytes));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    @Operation(summary = "Exportar proveedores a CSV", description = "Genera y descarga un archivo CSV estructurado con los proveedores (mín. 10 registros).")
    public Mono<ResponseEntity<byte[]>> exportCsv() {
        return reportService.exportToCsv()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proveedores.csv")
                        .body(bytes));
    }

    // --- CARGA MASIVA E IMPORTACIÓN (MÍN. 10 REGISTROS) ---

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar proveedores desde archivo (Excel o CSV)", description = "Carga masiva de mínimo 10 proveedores asignando automáticamente campos de auditoría.")
    public Mono<ResponseEntity<ImportResponse>> importProviders(@RequestPart("file") FilePart filePart) {
        return reportService.importFile(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar proveedores desde Excel (.xlsx)", description = "Carga masiva desde archivo Excel validando e insertando registros en MongoDB.")
    public Mono<ResponseEntity<ImportResponse>> importProvidersExcel(@RequestPart("file") FilePart filePart) {
        return reportService.importFromExcel(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar proveedores desde CSV", description = "Carga masiva desde archivo CSV validando e insertando registros en MongoDB.")
    public Mono<ResponseEntity<ImportResponse>> importProvidersCsv(@RequestPart("file") FilePart filePart) {
        return reportService.importFromCsv(filePart)
                .map(ResponseEntity::ok);
    }
}