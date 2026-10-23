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
import vallegrande.edu.pe.backend.model.User;
import vallegrande.edu.pe.backend.service.UserReportService;
import vallegrande.edu.pe.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios (Maestro de Seguridad y Accesos en MongoDB)", description = "CRUD de usuarios con auditoría completa, baja lógica, restauración y reportes.")
public class UserRest {

    private final UserService service;
    private final UserReportService reportService;

    public UserRest(UserService service, UserReportService reportService) {
        this.service = service;
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene los usuarios con opción de filtrar por estado activo o inactivo.")
    public Flux<User> findAll(
            @RequestParam(required = false, defaultValue = "-1") @Parameter(description = "Límite de registros") int limit,
            @RequestParam(required = false) @Parameter(description = "Filtrar por estado: true (activos), false (eliminados)") Boolean active) {
        Flux<User> result;
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
    @Operation(summary = "Listar usuarios activos")
    public Flux<User> findActive() {
        return service.findActiveOnly();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar usuarios eliminados lógicamente (papelera)")
    public Flux<User> findDeleted() {
        return service.findDeletedOnly();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID")
    public Mono<ResponseEntity<User>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo usuario con auditoría")
    public Mono<User> create(@RequestBody User obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar usuario actualizando auditoría")
    public Mono<User> update(@PathVariable String id, @RequestBody User obj) {
        return service.update(id, obj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (Baja Lógica)", description = "Aplica borrado lógico cambiando isActive=false sin destruir los datos.")
    public Mono<ResponseEntity<User>> delete(@PathVariable String id) {
        return service.softDelete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar usuario eliminado (Alta Lógica - PATCH)")
    public Mono<ResponseEntity<User>> restorePatch(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar usuario eliminado (Alta Lógica - PUT)")
    public Mono<ResponseEntity<User>> restorePut(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminación física definitiva")
    public Mono<Void> hardDelete(@PathVariable String id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Alternar estado activo/inactivo")
    public Mono<User> toggleStatus(@PathVariable String id) {
        return service.findById(id)
                .flatMap(u -> {
                    boolean current = u.getIsActive() != null && u.getIsActive();
                    u.setIsActive(!current);
                    return service.save(u);
                });
    }

    @PostMapping("/invite-worker")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> inviteWorker(@RequestBody User obj) {
        return service.invitarTrabajador(obj);
    }

    @PutMapping("/{id}/approve")
    public Mono<User> approveUser(@PathVariable String id) {
        return service.aprobarCliente(id);
    }

    // --- REPORTES Y EXPORTACIÓN ---

    @GetMapping(value = "/export/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<byte[]>> exportExcel() {
        return reportService.exportToExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx")
                        .body(bytes));
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity<byte[]>> exportPdf() {
        return reportService.exportToPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.pdf")
                        .body(bytes));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public Mono<ResponseEntity<byte[]>> exportCsv() {
        return reportService.exportToCsv()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.csv")
                        .body(bytes));
    }

    // --- CARGA MASIVA E IMPORTACIÓN ---

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importUsers(@RequestPart("file") FilePart filePart) {
        return reportService.importFile(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importUsersExcel(@RequestPart("file") FilePart filePart) {
        return reportService.importFromExcel(filePart)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponse>> importUsersCsv(@RequestPart("file") FilePart filePart) {
        return reportService.importFromCsv(filePart)
                .map(ResponseEntity::ok);
    }
}