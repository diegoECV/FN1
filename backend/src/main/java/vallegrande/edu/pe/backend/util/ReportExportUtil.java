package vallegrande.edu.pe.backend.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ReportExportUtil {

    private ReportExportUtil() {}

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera un reporte en Excel (.xlsx) con estilos empresariales.
     */
    public static byte[] generateExcel(String sheetTitle, String[] headers, List<List<String>> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetTitle != null && !sheetTitle.isBlank() ? sheetTitle : "Reporte");

            // Estilos para encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Estilos para datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Fila de Encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Filas de Datos
            int rowIdx = 1;
            for (List<String> rowData : rows) {
                Row row = sheet.createRow(rowIdx++);
                for (int col = 0; col < headers.length; col++) {
                    Cell cell = row.createCell(col);
                    String val = (col < rowData.size() && rowData.get(col) != null) ? rowData.get(col) : "";
                    cell.setCellValue(val);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un reporte PDF con OpenPDF.
     */
    public static byte[] generatePdf(String title, String[] headers, List<List<String>> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(headers.length > 5 ? PageSize.A4.rotate() : PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(18, 52, 86));
            Paragraph titlePara = new Paragraph("VISONS S.A.C. — " + title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(6);
            document.add(titlePara);

            // Fecha de emisión
            Font dateFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
            Paragraph datePara = new Paragraph("Generado el: " + LocalDateTime.now().format(DATE_FORMAT), dateFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingAfter(15);
            document.add(datePara);

            // Tabla
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            // Encabezados de tabla
            Font thFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, thFont));
                cell.setBackgroundColor(new Color(24, 76, 120));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Contenido
            Font tdFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
            boolean alternate = false;
            for (List<String> rowData : rows) {
                Color bg = alternate ? new Color(245, 247, 250) : Color.WHITE;
                for (int col = 0; col < headers.length; col++) {
                    String val = (col < rowData.size() && rowData.get(col) != null) ? rowData.get(col) : "";
                    PdfPCell cell = new PdfPCell(new Phrase(val, tdFont));
                    cell.setBackgroundColor(bg);
                    cell.setPadding(5);
                    table.addCell(cell);
                }
                alternate = !alternate;
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un archivo CSV en UTF-8 con BOM para visualización correcta en Excel.
     */
    public static byte[] generateCsv(String[] headers, List<List<String>> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Escribir UTF-8 BOM
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
                writer.writeNext(headers);
                for (List<String> rowData : rows) {
                    writer.writeNext(rowData.toArray(new String[0]));
                }
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte CSV: " + e.getMessage(), e);
        }
    }
}
