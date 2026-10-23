package vallegrande.edu.pe.backend.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.model.Category;
import vallegrande.edu.pe.backend.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categorías (Maestro en MongoDB)", description = "Gestión del catálogo de categorías con auditoría, borrado lógico y restauración.")
public class CategoryRest {
    private final CategoryService service;

    public CategoryRest(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene las categorías con opción de filtrar por estado activo o eliminado.")
    public Flux<Category> findAll(
            @RequestParam(required = false, defaultValue = "-1") @Parameter(description = "Límite de registros") int limit,
            @RequestParam(required = false) @Parameter(description = "Filtrar por estado: true (activos), false (eliminados)") Boolean active) {
        Flux<Category> result;
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
    @Operation(summary = "Listar categorías activas")
    public Flux<Category> findActive() {
        return service.findActiveOnly();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar categorías eliminadas lógicamente")
    public Flux<Category> findDeleted() {
        return service.findDeletedOnly();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar categoría por ID")
    public Mono<ResponseEntity<Category>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva categoría")
    public Mono<Category> create(@RequestBody Category obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría")
    public Mono<Category> update(@PathVariable String id, @RequestBody Category obj) {
        return service.update(id, obj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría (Baja Lógica)", description = "Aplica borrado lógico cambiando isActive=false sin destruir los datos.")
    public Mono<ResponseEntity<Category>> delete(@PathVariable String id) {
        return service.softDelete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar categoría eliminada (Alta Lógica - PATCH)")
    public Mono<ResponseEntity<Category>> restorePatch(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar categoría eliminada (Alta Lógica - PUT)")
    public Mono<ResponseEntity<Category>> restorePut(@PathVariable String id) {
        return service.restore(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminación física definitiva")
    public Mono<Void> hardDelete(@PathVariable String id) {
        return service.deleteById(id);
    }
}