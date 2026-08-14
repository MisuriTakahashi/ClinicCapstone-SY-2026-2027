/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author PC
 */
public class ReportExporter {
    private final VisitData visitData = new VisitData();
    private final MedicineData medicineData;
    

    public ReportExporter(MedicineData medicineData) {
        this.medicineData = medicineData; // reuse the same instance AdminPanel already uses
    }

    /** Returns true if there is at least one check-in or inventory record for the date. */
    public boolean hasRecordsForDate(LocalDate date) throws SQLException, IOException {
        String iso = date.toString();
        if (!visitData.getVisitsByDate(iso).isEmpty()) return true;
        for (String line : medicineData.loadActivityLog()) {
            if (line.startsWith("[" + iso)) return true;
        }
        return false;
    }

    /** Writes the CSV report for the given date to the given file. */
    public void writeDailyReport(LocalDate date, File destination) throws SQLException, IOException {
        String iso = date.toString();
        ArrayList<CheckinSystem> visits = visitData.getVisitsByDate(iso);
        ArrayList<String> logLines = medicineData.loadActivityLog();

        try (PrintWriter out = new PrintWriter(destination, "UTF-8")) {
            // --- Report header ---
            String prettyDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                    + " - " + date.getDayOfWeek().toString().substring(0, 1)
                    + date.getDayOfWeek().toString().substring(1).toLowerCase();
            out.println("Student Check-in and Inventory Daily Report");
            out.println("Date: " + prettyDate);
            out.println();

            // --- Section 1: Check-in Logs ---
            out.println("CHECK-IN LOGS");
            out.println("Student Name,Grade/Section,LRN,Check-in Date/Time,Reason,"
                    + "Medicine Used,Medicine Quantity,Status,Guardian Name,Guardian Phone");
            for (CheckinSystem v : visits) {
                out.println(csv(v.getName()) + "," + csv(v.getGradeSection()) + "," + csv(v.getLrn()) + ","
                        + csv(v.getCheckInTime()) + "," + csv(v.getReason()) + "," + csv(v.getMedUsed()) + ","
                        + v.getmedsQty() + "," + csv(v.getStatus()) + ","
                        + csv(v.getGuardianName()) + "," + csv(v.getGuardianPhoneNums()));
            }
            if (visits.isEmpty()) out.println("No check-in records for this date.");
            out.println();

            // --- Section 2: Inventory / Medicine Logs ---
            out.println("INVENTORY / MEDICINE LOGS");
            out.println("Date/Time,Activity");
            boolean anyLog = false;
            for (String line : logLines) {
                if (!line.startsWith("[" + iso)) continue;
                anyLog = true;
                int close = line.indexOf(']');
                String timestamp = line.substring(1, close);
                String activity = line.substring(close + 2); // skip "] "
                out.println(csv(timestamp) + "," + csv(activity));
            }
            if (!anyLog) out.println("No inventory activity for this date.");
        }
    }

    /** Wraps a field in quotes and escapes internal quotes, CSV-safe. */
    private String csv(String value) {
        if (value == null) value = "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    
    
    /** Returns true if there is at least one check-in record for the date (ignores inventory). */
        public boolean hasCheckinRecordsForDate(LocalDate date) throws SQLException {
            return !visitData.getVisitsByDate(date.toString()).isEmpty();
        }

        public void writeCheckinReport(LocalDate date, File destination) throws SQLException, IOException {
        String iso = date.toString();
        
        ArrayList<CheckinSystem> visits = visitData.getVisitsByDate(iso);

        String prettyDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                + " - " + date.getDayOfWeek().toString().substring(0, 1)
                + date.getDayOfWeek().toString().substring(1).toLowerCase();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Check-in Log");

            // --- Reusable cell styles ---
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Applied to long-text columns (Reason, Medicine Used, Guardian Name) so long
            // values wrap instead of forcing the column excessively wide.
            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            // Real Excel date format, applied only to the Check-in Date/Time cell so Excel
            // renders it as "August 14, 2026 6:12 AM" instead of a raw string or "########".
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper()
                    .createDataFormat().getFormat("mmmm d, yyyy h:mm AM/PM"));

            String[] headers = {"Student Name", "Grade/Section", "LRN", "Check-in Date/Time", "Reason",
                    "Medicine Used", "Medicine Quantity", "Status", "Guardian Name", "Guardian Phone"};

            int rowIndex = 0;

            // --- Title rows (kept identical in wording to the existing CSV report) ---
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Student Check-in Log Report");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

            Row dateRow = sheet.createRow(rowIndex++);
            dateRow.createCell(0).setCellValue("Date: " + prettyDate);

            rowIndex++; // blank spacer row, matches the blank line in the CSV version

            Row sectionRow = sheet.createRow(rowIndex++);
            sectionRow.createCell(0).setCellValue("CHECK-IN LOGS");

            // --- Header row ---
            Row headerRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
                cell.setCellStyle(headerStyle);
            }

            // --- Data rows ---
            DateTimeFormatter checkinFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            for (CheckinSystem v : visits) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(safe(v.getName()));
                row.createCell(1).setCellValue(safe(v.getGradeSection()));
                row.createCell(2).setCellValue(safe(v.getLrn()));

                // Check-in Date/Time — written as a real Excel date cell, not text, so it
                // displays correctly and can be sorted/filtered like a normal Excel date.
                Cell dateCell = row.createCell(3);
                try {
                    LocalDateTime checkinDateTime = LocalDateTime.parse(v.getCheckInTime(), checkinFormat);
                    dateCell.setCellValue(checkinDateTime);
                    dateCell.setCellStyle(dateStyle);
                } catch (DateTimeParseException ex) {
                    // Safety fallback: if a record's stored time doesn't match the expected
                    // pattern, keep the export from failing by writing it as plain text.
                    dateCell.setCellValue(safe(v.getCheckInTime()));
                }

                Cell reasonCell = row.createCell(4);
                reasonCell.setCellValue(safe(v.getReason()));
                reasonCell.setCellStyle(wrapStyle);

                Cell medCell = row.createCell(5);
                medCell.setCellValue(safe(v.getMedUsed()));
                medCell.setCellStyle(wrapStyle);

                row.createCell(6).setCellValue(v.getmedsQty());
                row.createCell(7).setCellValue(safe(v.getStatus()));

                Cell guardianCell = row.createCell(8);
                guardianCell.setCellValue(safe(v.getGuardianName()));
                guardianCell.setCellStyle(wrapStyle);

                row.createCell(9).setCellValue(safe(v.getGuardianPhoneNums()));
            }

            if (visits.isEmpty()) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No check-in records for this date.");
            }

            // --- Auto-size every column, AFTER all headers and data have been written ---
            final int MAX_COLUMN_WIDTH = 50 * 256; // caps very long text columns at ~50 characters wide
            for (int col = 0; col < headers.length; col++) {
                sheet.autoSizeColumn(col);
                if (sheet.getColumnWidth(col) > MAX_COLUMN_WIDTH) {
                    sheet.setColumnWidth(col, MAX_COLUMN_WIDTH);
                }
            }

            // --- Save the workbook as a real .xlsx file ---
            try (OutputStream out = new FileOutputStream(destination)) {
                workbook.write(out);
            }
        }
    }

    /** Returns an empty string instead of null, for safe writing into Excel cells. */
    private String safe(String value) {
        return value == null ? "" : value;
    }
}
