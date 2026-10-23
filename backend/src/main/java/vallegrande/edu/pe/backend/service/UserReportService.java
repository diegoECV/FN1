package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.backend.dto.ImportResponse;
import vallegrande.edu.pe.backend.model.User;
import vallegrande.edu.pe.backend.repository.UserRepository;
import vallegrande.edu.pe.backend.util.ReportExportUtil;
import vallegrande.edu.pe.backend.util.ReportImportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserReportService {

    private final UserRepository userRepository;
    private static final String DEFAULT_BCRYPT = "$2a$10$VFiqJivXp8EmC60CVyL3W.D7iebJlVAZXhhn1lix0eLuE43y9MxeW"; // password123

    public UserReportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final String[] HEADERS = {
        "ID", "Usuario", "Nombre Completo", "Email", "Teléfono", "Rol", "Departamento", "Estado", "Último Acceso", "Fecha Creación", "Creado Por", "Fecha Modificación", "Modificado Por"
    };

    private List<String> mapUserToRow(User u) {
        return List.of(
            u.getId() != null ? u.getId() : "",
            u.getUsername() != null ? u.getUsername() : "",
            u.getName() != null ? u.getName() : "",
            u.getEmail() != null ? u.getEmail() : "",
            u.getPhone() != null ? u.getPhone() : "",
            u.getRole() != null ? u.getRole() : "",
            u.getDepartment() != null ? u.getDepartment() : "",
            (u.getIsActive() == null || Boolean.TRUE.equals(u.getIsActive())) ? "ACTIVO" : "INACTIVO",
            u.getLastLogin() != null ? u.getLastLogin() : "",
            u.getCreatedAt() != null ? u.getCreatedAt() : "",
            u.getCreatedBy() != null ? u.getCreatedBy() : "",
            u.getUpdatedAt() != null ? u.getUpdatedAt() : "",
            u.getUpdatedBy() != null ? u.getUpdatedBy() : ""
        );
    }

    private User mapRowToUser(List<String> r) {
        String id = r.size() > 0 && !r.get(0).isBlank() ? r.get(0) : "U-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String username = r.size() > 1 && !r.get(1).isBlank() ? r.get(1) : "";
        String name = r.size() > 2 ? r.get(2) : "";
        String email = r.size() > 3 ? r.get(3) : username;
        if (username.isBlank() && !email.isBlank()) {
            username = email;
        }
        String phone = r.size() > 4 ? r.get(4) : "+51999000111";
        String role = r.size() > 5 && !r.get(5).isBlank() ? r.get(5) : "USER";
        String dept = r.size() > 6 ? r.get(6) : "Operaciones";
        boolean active = r.size() <= 7 || r.get(7).equalsIgnoreCase("ACTIVO") || r.get(7).equalsIgnoreCase("true") || r.get(7).equalsIgnoreCase("1");
        String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return User.builder()
                .id(id)
                .username(username)
                .name(name)
                .email(email)
                .password(DEFAULT_BCRYPT)
                .phone(phone)
                .role(role)
                .department(dept)
                .isActive(active)
                .createdAt(now)
                .createdBy("IMPORT_FILE")
                .updatedAt(now)
                .updatedBy("IMPORT_FILE")
                .build();
    }

    // --- EXPORTACIÓN ---

    public Mono<byte[]> exportToExcel() {
        return userRepository.findAll()
            .map(this::mapUserToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generateExcel("Usuarios", HEADERS, rows));
    }

    public Mono<byte[]> exportToPdf() {
        return userRepository.findAll()
            .map(this::mapUserToRow)
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .map(rows -> ReportExportUtil.generatePdf("Padrón de Usuarios del Sistema", HEADERS, rows));
    }

    public Mono<byte[]> exportToCsv() {
        return userRepository.findAll()
            .map(this::mapUserToRow)
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
                        throw new RuntimeException("Error al parsear Excel de usuarios: " + e.getMessage(), e);
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
                        throw new RuntimeException("Error al parsear CSV de usuarios: " + e.getMessage(), e);
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
        List<User> users = rows.stream()
                .map(this::mapRowToUser)
                .filter(u -> !u.getUsername().isBlank())
                .collect(Collectors.toList());

        return userRepository.saveAll(users)
                .collectList()
                .map(saved -> ImportResponse.builder()
                        .success(true)
                        .message("Se importaron " + saved.size() + " usuarios exitosamente.")
                        .totalProcessed(rows.size())
                        .totalImported(saved.size())
                        .errors(new ArrayList<>())
                        .build());
    }
}
