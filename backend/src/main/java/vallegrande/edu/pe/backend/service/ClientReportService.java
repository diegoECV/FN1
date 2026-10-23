package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.Client;
import vallegrande.edu.pe.backend.repository.ClientRepository;
import vallegrande.edu.pe.backend.util.ReportExportUtil;
import vallegrande.edu.pe.backend.util.ReportImportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientReportService {

    private final ClientRepository clientRepository;

    private static final String[] HEADERS = {
        "ID", "Tipo Doc.", "Nro. Documento", "Empresa", "Contacto", "País", "Email", "Teléfono", "Incoterm", "Moneda", "Estado", "Fecha Creación", "Creado Por", "Fecha Modificación", "Modificado Por"
    };

    private List<String> mapClientToRow(Client c) {
        return List.of(
            c.getId() != null ? c.getId() : "",
            c.getDocumentType() != null ? c.getDocumentType() : "",
            c.getDocumentNumber() != null ? c.getDocumentNumber() : "",
            c.getCompanyName() != null ? c.getCompanyName() : "",
            c.getContactName() != null ? c.getContactName() : "",
            c.getCountry() != null ? c.getCountry() : "",
            c.getEmail() != null ? c.getEmail() : "",
            c.getPhone() != null ? c.getPhone() : "",
            c.getPreferredIncoterm() != null ? c.getPreferredIncoterm() : "",
            c.getPreferredCurrency() != null ? c.getPreferredCurrency() : "",
            (c.getIsActive() == null || Boolean.TRUE.equals(c.getIsActive())) ? "ACTIVO" : "INACTIVO",
            c.getCreatedAt() != null ? c.getCreatedAt() : "",
            c.getCreatedBy() != null ? c.getCreatedBy() : "",
            c.getUpdatedAt() != null ? c.getUpdatedAt() : "",
            c.getUpdatedBy() != null ? c.getUpdatedBy() : ""
        );
    }

    private Client mapRowToClient(List<String> r) {
        String id = r.size() > 0 && !r.get(0).isBlank() ? r.get(0) : "CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String docType = r.size() > 1 && !r.get(1).isBlank() ? r.get(1) : "TAX_ID";
        String docNum = r.size() > 2 ? r.get(2) : "";
        String company = r.size() > 3 ? r.get(3) : "";
        String contact = r.size() > 4 ? r.get(4) : "";
        String country = r.size() > 5 ? r.get(5) : "";
        String email = r.size() > 6 ? r.get(6) : "";
        String phone = r.size() > 7 ? r.get(7) : "";
        String incoterm = r.size() > 8 && !r.get(8).isBlank() ? r.get(8) : "FOB";
        String currency = r.size() > 9 && !r.get(9).isBlank() ? r.get(9) : "USD";
        String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return Client.builder()
                .id(id)
                .documentType(docType)
                .documentNumber(docNum)
                .companyName(company)
                .contactName(contact)
                .country(country)
                .email(email)
                .phone(phone)
                .address("Oficina Principal")
                .preferredIncoterm(incoterm)
                .destinationPort("Rotterdam")
                .paymentTerms("30 dias")
                .preferredCurrency(currency)
                .creditLimit(100000.0)
                .language("es")
                .isActive(true)
                .createdAt(now)
                .createdBy("IMPORT_FILE")
                .updatedAt(now)
                .updatedBy("IMPORT_FILE")
                .build();
    }

    // --- EXPORTACIÓN ---

    public Mono<byte[]> exportToExcel() {
        return clientRepository.findAll()
            .map(this::mapClientToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateExcel("Clientes", HEADERS, rows));
    }

    public Mono<byte[]> exportToPdf() {
        return clientRepository.findAll()
            .map(this::mapClientToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generatePdf("Reporte General de Clientes", HEADERS, rows));
    }

    public Mono<byte[]> exportToCsv() {
        return clientRepository.findAll()
            .map(this::mapClientToRow)
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
                        throw new RuntimeException("Error al parsear Excel de clientes: " + e.getMessage(), e);
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
                        throw new RuntimeException("Error al parsear CSV de clientes: " + e.getMessage(), e);
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
        List<Client> clients = rows.stream()
                .map(this::mapRowToClient)
                .collect(Collectors.toList());

        return clientRepository.saveAll(clients)
                .collectList()
                .map(saved -> ImportResponse.builder()
                        .success(true)
                        .message("Se importaron " + saved.size() + " clientes exitosamente.")
                        .totalProcessed(rows.size())
                        .totalImported(saved.size())
                        .errors(new ArrayList<>())
                        .build());
    }
}
