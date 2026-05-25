package org.services;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.models.Sell;
import org.models.SellItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── PDF ───────────────────────────────────────────────────────────────────

    public byte[] generatePdf(List<Sell> sells, LocalDateTime start, LocalDateTime end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 40, 30);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(30, 58, 138));
            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font cellFont  = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

            Paragraph title = new Paragraph("BQRINES — Reporte de Ventas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            String range = (start != null && end != null)
                    ? "Período: " + start.format(FMT) + " al " + end.format(FMT)
                    : "Todas las ventas";
            Font subFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
            Paragraph sub = new Paragraph(range + "   |   Total registros: " + sells.size(), subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(10);
            doc.add(sub);

            // Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 9f, 14f, 8f, 22f, 9f, 14f});

            String[] headers = {"#", "Fecha", "Cliente", "Identificación", "Productos", "Total", "Registrado por"};
            Color headerBg = new Color(30, 58, 138);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            boolean alt = false;
            for (Sell s : sells) {
                Color rowBg = alt ? new Color(239, 246, 255) : Color.WHITE;
                alt = !alt;

                addCell(table, String.valueOf(s.getId()), cellFont, rowBg, Element.ALIGN_CENTER);
                addCell(table, s.getDate() != null ? s.getDate().format(FMT) : "—", cellFont, rowBg, Element.ALIGN_CENTER);
                addCell(table, s.getClientName(), cellFont, rowBg, Element.ALIGN_LEFT);
                addCell(table, s.getBuyerDocument() != null ? s.getBuyerDocument() : "—", cellFont, rowBg, Element.ALIGN_CENTER);

                StringBuilder items = new StringBuilder();
                for (SellItem item : s.getItems()) {
                    String tipo = switch (item.getProductType()) {
                        case "vehicle" -> "[V]";
                        case "spares"  -> "[R]";
                        default        -> "[E]";
                    };
                    items.append(tipo).append(" ").append(item.getProductName())
                         .append(" x").append(item.getQuantity()).append("\n");
                }
                addCell(table, items.toString().trim(), cellFont, rowBg, Element.ALIGN_LEFT);

                addCell(table, "$" + formatNum(s.getTotal()), cellFont, rowBg, Element.ALIGN_RIGHT);
                addCell(table, s.getRegisteredBy(), cellFont, rowBg, Element.ALIGN_LEFT);
            }

            doc.add(table);

            // Summary total
            double grandTotal = sells.stream().mapToDouble(Sell::getTotal).sum();
            Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(30, 58, 138));
            Paragraph totalPara = new Paragraph("Total facturado: $" + formatNum(grandTotal), totalFont);
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            totalPara.setSpacingBefore(8);
            doc.add(totalPara);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private void addCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(4);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    // ── Excel ─────────────────────────────────────────────────────────────────

    public byte[] generateExcel(List<Sell> sells, LocalDateTime start, LocalDateTime end) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Ventas");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Alternating row style
            CellStyle altStyle = wb.createCellStyle();
            altStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] cols = {"#", "Fecha", "Cliente", "Identificación", "Productos", "Total", "Registrado por"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Sell s : sells) {
                Row row = sheet.createRow(rowIdx++);
                boolean isAlt = rowIdx % 2 == 0;

                setCell(row, 0, String.valueOf(s.getId()), isAlt ? altStyle : null);
                setCell(row, 1, s.getDate() != null ? s.getDate().format(FMT) : "—", isAlt ? altStyle : null);
                setCell(row, 2, s.getClientName(), isAlt ? altStyle : null);
                setCell(row, 3, s.getBuyerDocument() != null ? s.getBuyerDocument() : "—", isAlt ? altStyle : null);

                StringBuilder items = new StringBuilder();
                for (SellItem item : s.getItems()) {
                    String tipo = switch (item.getProductType()) {
                        case "vehicle" -> "[V]";
                        case "spares"  -> "[R]";
                        default        -> "[E]";
                    };
                    items.append(tipo).append(" ").append(item.getProductName())
                         .append(" x").append(item.getQuantity()).append("; ");
                }
                setCell(row, 4, items.toString().trim(), isAlt ? altStyle : null);
                setCell(row, 5, "$" + formatNum(s.getTotal()), isAlt ? altStyle : null);
                setCell(row, 6, s.getRegisteredBy(), isAlt ? altStyle : null);
            }

            // Auto-size columns
            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }

    private String formatNum(double value) {
        return String.format("%,.0f", value).replace(",", ".");
    }
}
