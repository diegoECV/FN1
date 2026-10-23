package vallegrande.edu.pe.backend.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReportImportUtil {

    private ReportImportUtil() {}

    /**
     * Parsea un archivo Excel (.xlsx / .xls) ignorando la primera fila de encabezados.
     */
    public static List<List<String>> parseExcel(byte[] bytes) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Workbook workbook = WorkbookFactory.create(bis)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false; // Saltar encabezados
                    continue;
                }
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.add(formatter.formatCellValue(cell).trim());
                }
                if (rowData.stream().anyMatch(s -> !s.isBlank())) {
                    rows.add(rowData);
                }
            }
        }
        return rows;
    }

    /**
     * Parsea un archivo CSV ignorando la primera fila de encabezados.
     */
    public static List<List<String>> parseCsv(byte[] bytes) throws IOException, CsvValidationException {
        List<List<String>> rows = new ArrayList<>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             CSVReader reader = new CSVReader(new InputStreamReader(bis, StandardCharsets.UTF_8))) {
            String[] line;
            boolean firstRow = true;
            while ((line = reader.readNext()) != null) {
                if (firstRow) {
                    firstRow = false; // Saltar encabezados
                    continue;
                }
                List<String> rowData = Arrays.stream(line)
                        .map(String::trim)
                        .collect(Collectors.toList());
                if (rowData.stream().anyMatch(s -> !s.isBlank())) {
                    rows.add(rowData);
                }
            }
        }
        return rows;
    }
}
