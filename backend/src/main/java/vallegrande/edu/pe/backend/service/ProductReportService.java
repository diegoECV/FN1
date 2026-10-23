package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.Product;
import vallegrande.edu.pe.backend.repository.ProductRepository;
import vallegrande.edu.pe.backend.util.ReportExportUtil;
import vallegrande.edu.pe.backend.util.ReportImportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReportService {

    private final ProductRepository productRepository;

    private static final String[] HEADERS = {
        "ID", "Código", "Nombre", "Categoría", "Variedad", "Calibre", "Unidad", "Partida Arancelaria (HS)", "Temperatura", "Vida Útil", "Empaque", "Región", "Calidad", "Certificación", "Estado", "Fecha Creación", "Creado Por", "Fecha Modificación", "Modificado Por"
    };

    private List<String> mapProductToRow(Product p) {
        return List.of(
            p.getId() != null ? p.getId() : "",
            p.getCode() != null ? p.getCode() : "",
            p.getName() != null ? p.getName() : "",
            p.getCategoryId() != null ? p.getCategoryId() : "",
            p.getVariety() != null ? p.getVariety() : "",
            p.getCaliber() != null ? p.getCaliber() : "",
            p.getUnitMeasure() != null ? p.getUnitMeasure() : "",
            p.getHsCode() != null ? p.getHsCode() : "",
            p.getStorageTemperature() != null ? p.getStorageTemperature() : "",
            p.getShelfLifeDays() != null ? String.valueOf(p.getShelfLifeDays()) : "",
            p.getPackagingType() != null ? p.getPackagingType() : "",
            p.getOriginRegion() != null ? p.getOriginRegion() : "",
            p.getQualityGrade() != null ? p.getQualityGrade() : "",
            p.getOrganicCertification() != null ? p.getOrganicCertification() : "",
            (p.getIsActive() == null || Boolean.TRUE.equals(p.getIsActive())) ? "ACTIVO" : "INACTIVO",
            p.getCreatedAt() != null ? p.getCreatedAt() : "",
            p.getCreatedBy() != null ? p.getCreatedBy() : "",
            p.getUpdatedAt() != null ? p.getUpdatedAt() : "",
            p.getUpdatedBy() != null ? p.getUpdatedBy() : ""
        );
    }

    private Product mapRowToProduct(List<String> r) {
        String id = r.size() > 0 && !r.get(0).isBlank() ? r.get(0) : "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String code = r.size() > 1 ? r.get(1) : "";
        String name = r.size() > 2 ? r.get(2) : "";
        String categoryId = r.size() > 3 && !r.get(3).isBlank() ? r.get(3) : "CAT-01";
        String variety = r.size() > 4 ? r.get(4) : "";
        String caliber = r.size() > 5 ? r.get(5) : "";
        String unitMeasure = r.size() > 6 && !r.get(6).isBlank() ? r.get(6) : "KG";
        String hsCode = r.size() > 7 ? r.get(7) : "";
        String temp = r.size() > 8 ? r.get(8) : "2°C - 4°C";
        Integer shelfLife = 30;
        if (r.size() > 9 && !r.get(9).isBlank()) {
            try {
                shelfLife = Integer.parseInt(r.get(9).replaceAll("\\D", ""));
            } catch (Exception ignored) {}
        }
        String packaging = r.size() > 10 ? r.get(10) : "Caja de Exportación 5Kg";
        String region = r.size() > 11 ? r.get(11) : "Ica";
        String grade = r.size() > 12 ? r.get(12) : "CAT_EXTRA";
        String cert = r.size() > 13 ? r.get(13) : "GLOBAL_GAP";
        String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return Product.builder()
                .id(id)
                .code(code)
                .name(name)
                .categoryId(categoryId)
                .variety(variety)
                .caliber(caliber)
                .unitMeasure(unitMeasure)
                .hsCode(hsCode)
                .storageTemperature(temp)
                .shelfLifeDays(shelfLife)
                .packagingType(packaging)
                .originRegion(region)
                .qualityGrade(grade)
                .organicCertification(cert)
                .isActive(true)
                .createdAt(now)
                .createdBy("IMPORT_FILE")
                .updatedAt(now)
                .updatedBy("IMPORT_FILE")
                .build();
    }

    // --- EXPORTACIÓN ---

    public Mono<byte[]> exportToExcel() {
        return productRepository.findAll()
            .map(this::mapProductToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateExcel("Productos", HEADERS, rows));
    }

    public Mono<byte[]> exportToPdf() {
        return productRepository.findAll()
            .map(this::mapProductToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generatePdf("Catálogo de Productos de Exportación", HEADERS, rows));
    }

    public Mono<byte[]> exportToCsv() {
        return productRepository.findAll()
            .map(this::mapProductToRow)
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
                        throw new RuntimeException("Error al parsear Excel: " + e.getMessage(), e);
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
                        throw new RuntimeException("Error al parsear CSV: " + e.getMessage(), e);
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
        List<Product> products = rows.stream()
                .map(this::mapRowToProduct)
                .collect(Collectors.toList());

        return productRepository.saveAll(products)
                .collectList()
                .map(saved -> ImportResponse.builder()
                        .success(true)
                        .message("Se importaron " + saved.size() + " productos exitosamente.")
                        .totalProcessed(rows.size())
                        .totalImported(saved.size())
                        .errors(new ArrayList<>())
                        .build());
    }
}
