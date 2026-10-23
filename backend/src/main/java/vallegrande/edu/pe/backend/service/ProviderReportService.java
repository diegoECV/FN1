package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.Provider;
import vallegrande.edu.pe.backend.repository.ProviderRepository;
import vallegrande.edu.pe.backend.util.ReportExportUtil;
import vallegrande.edu.pe.backend.util.ReportImportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderReportService {

    private final ProviderRepository providerRepository;

    private static final String[] HEADERS = {
        "ID", "Tipo Doc.", "Nro. Documento", "Razón Social", "Contacto", "País", "Email", "Teléfono", "Dirección", "Departamento", "Provincia", "Distrito", "Ubigeo", "Cultivo Principal", "CCI", "Certificación", "Estado", "Fecha Creación", "Creado Por", "Fecha Modificación", "Modificado Por"
    };

    private List<String> mapProviderToRow(Provider p) {
        return List.of(
            p.getId() != null ? p.getId() : "",
            p.getDocumentType() != null ? p.getDocumentType() : "",
            p.getDocumentNumber() != null ? p.getDocumentNumber() : "",
            p.getCompanyName() != null ? p.getCompanyName() : "",
            p.getContactName() != null ? p.getContactName() : "",
            p.getCountry() != null ? p.getCountry() : "",
            p.getEmail() != null ? p.getEmail() : "",
            p.getPhone() != null ? p.getPhone() : "",
            p.getAddress() != null ? p.getAddress() : "",
            p.getDepartment() != null ? p.getDepartment() : "",
            p.getProvince() != null ? p.getProvince() : "",
            p.getDistrict() != null ? p.getDistrict() : "",
            p.getUbigeo() != null ? p.getUbigeo() : "",
            p.getMainCrop() != null ? p.getMainCrop() : "",
            p.getBankAccountCci() != null ? p.getBankAccountCci() : "",
            p.getCertificationStatus() != null ? p.getCertificationStatus() : "",
            (p.getIsActive() == null || Boolean.TRUE.equals(p.getIsActive())) ? "ACTIVO" : "INACTIVO",
            p.getCreatedAt() != null ? p.getCreatedAt() : "",
            p.getCreatedBy() != null ? p.getCreatedBy() : "",
            p.getUpdatedAt() != null ? p.getUpdatedAt() : "",
            p.getUpdatedBy() != null ? p.getUpdatedBy() : ""
        );
    }

    private Provider mapRowToProvider(List<String> r) {
        String id = r.size() > 0 && !r.get(0).isBlank() ? r.get(0) : "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String docType = r.size() > 1 && !r.get(1).isBlank() ? r.get(1) : "RUC";
        String docNum = r.size() > 2 ? r.get(2) : "";
        String company = r.size() > 3 ? r.get(3) : "";
        String contact = r.size() > 4 ? r.get(4) : "";
        String country = r.size() > 5 && !r.get(5).isBlank() ? r.get(5) : "Perú";
        String email = r.size() > 6 ? r.get(6) : "";
        String phone = r.size() > 7 ? r.get(7) : "";
        String address = r.size() > 8 && !r.get(8).isBlank() ? r.get(8) : "Valle Agrícola Principal";
        String dept = r.size() > 9 && !r.get(9).isBlank() ? r.get(9) : "Ica";
        String prov = r.size() > 10 && !r.get(10).isBlank() ? r.get(10) : "Ica";
        String dist = r.size() > 11 && !r.get(11).isBlank() ? r.get(11) : "Salas";
        String ubigeo = r.size() > 12 && !r.get(12).isBlank() ? r.get(12) : "110108";
        String crop = r.size() > 13 && !r.get(13).isBlank() ? r.get(13) : "Uva Red Globe";
        String cci = r.size() > 14 && !r.get(14).isBlank() ? r.get(14) : "002-191-001234567890-52";
        String cert = r.size() > 15 && !r.get(15).isBlank() ? r.get(15) : "GLOBAL_GAP_CERTIFIED";
        String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return Provider.builder()
                .id(id)
                .documentType(docType)
                .documentNumber(docNum)
                .companyName(company)
                .contactName(contact)
                .country(country)
                .email(email)
                .phone(phone)
                .address(address)
                .department(dept)
                .province(prov)
                .district(dist)
                .ubigeo(ubigeo)
                .mainCrop(crop)
                .bankAccountCci(cci)
                .certificationStatus(cert)
                .isActive(true)
                .createdAt(now)
                .createdBy("IMPORT_FILE")
                .updatedAt(now)
                .updatedBy("IMPORT_FILE")
                .build();
    }

    // --- EXPORTACIÓN ---

    public Mono<byte[]> exportToExcel() {
        return providerRepository.findAll()
            .map(this::mapProviderToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateExcel("Proveedores", HEADERS, rows));
    }

    public Mono<byte[]> exportToPdf() {
        return providerRepository.findAll()
            .map(this::mapProviderToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generatePdf("Padrón de Proveedores de Campo", HEADERS, rows));
    }

    public Mono<byte[]> exportToCsv() {
        return providerRepository.findAll()
            .map(this::mapProviderToRow)
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
                        throw new RuntimeException("Error al parsear Excel de proveedores: " + e.getMessage(), e);
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
                        throw new RuntimeException("Error al parsear CSV de proveedores: " + e.getMessage(), e);
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
        List<Provider> providers = rows.stream()
                .map(this::mapRowToProvider)
                .collect(Collectors.toList());

        return providerRepository.saveAll(providers)
                .collectList()
                .map(saved -> ImportResponse.builder()
                        .success(true)
                        .message("Se importaron " + saved.size() + " proveedores exitosamente.")
                        .totalProcessed(rows.size())
                        .totalImported(saved.size())
                        .errors(new ArrayList<>())
                        .build());
    }
}
