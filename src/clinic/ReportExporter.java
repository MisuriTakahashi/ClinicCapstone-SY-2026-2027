/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

    /** Writes the combined Check-in Logs + Inventory/Medicine Logs + Stock Overview report as a real .xlsx workbook. */
    public void writeDailyReport(LocalDate date, File destination) throws SQLException, IOException {
        String iso = date.toString();
        ArrayList<CheckinSystem> visits = visitData.getVisitsByDate(iso);
        ArrayList<String> logLines = medicineData.loadActivityLog();

        // Structured entries for this date only, used to compute per-medicine usage totals
        // for the Stock Overview section below. loadFormatted()/logLines above is still what
        // drives the plain "Activities Logs" text section, unchanged from before.
        ArrayList<ActivityLogData.Entry> allEntries = ActivityLogData.loadEntries();
        HashMap<String, Integer> usedTodayByMedicine = new HashMap<>();
        for (ActivityLogData.Entry entry : allEntries) {
            if (entry.timestamp() == null) continue;
            LocalDate entryDate = entry.timestamp().toLocalDateTime().toLocalDate();
            if (!entryDate.equals(date)) continue;

            ActivityLogData.UsageInfo usage =
                    ActivityLogData.parseMedicineUsage(entry.action(), entry.details());
            if (usage == null) continue;

            usedTodayByMedicine.merge(
                    usage.medicineName(),
                    usage.quantity(),
                    Integer::sum
            );
        }

        String prettyDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                + " - " + date.getDayOfWeek().toString().substring(0, 1)
                + date.getDayOfWeek().toString().substring(1).toLowerCase();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Daily Report");

            // --- Reusable cell styles (same approach as writeCheckinReport) ---
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle sectionStyle = workbook.createCellStyle();
            Font sectionFont = workbook.createFont();
            sectionFont.setBold(true);
            sectionFont.setFontHeightInPoints((short) 12);
            sectionStyle.setFont(sectionFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper()
                    .createDataFormat().getFormat("mmmm d, yyyy h:mm AM/PM"));

            // --- Stock Overview row styles ---
            // Expired rows get a red fill, low-stock (but not expired) rows get a yellow
            // fill, and normal in-stock rows have no fill at all.
            CellStyle expiredStyle = workbook.createCellStyle();
            expiredStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            expiredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle lowStockStyle = workbook.createCellStyle();
            lowStockStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            lowStockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] checkinHeaders = {"Student Name", "Grade/Section", "LRN", "Check-in Date/Time", "Reason",
                    "Medicine Used", "Medicine Quantity", "Status", "Guardian Name", "Guardian Phone"};
            String[] inventoryHeaders = {"Date/Time", "Activity"};
            String[] stockHeaders = {"Medicine Name", "Current Quantity", "Expiration Date", "Status", "Given Out (This Date)"};
            int columnCount = checkinHeaders.length; // widest section - drives auto-sizing below

            int rowIndex = 0;

            // --- Title ---
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Student Check-in and Inventory Daily Report");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            Row dateRow = sheet.createRow(rowIndex++);
            dateRow.createCell(0).setCellValue("Date: " + prettyDate);

            rowIndex++; // blank spacer row

            // --- Section 1: Check-in Logs ---
            Row checkinSectionRow = sheet.createRow(rowIndex++);
            Cell checkinSectionCell = checkinSectionRow.createCell(0);
            checkinSectionCell.setCellValue("CHECK-IN LOGS");
            checkinSectionCell.setCellStyle(sectionStyle);

            Row checkinHeaderRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < checkinHeaders.length; col++) {
                Cell cell = checkinHeaderRow.createCell(col);
                cell.setCellValue(checkinHeaders[col]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter checkinFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            for (CheckinSystem v : visits) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(safe(v.getName()));
                row.createCell(1).setCellValue(safe(v.getGradeSection()));
                row.createCell(2).setCellValue(safe(v.getLrn())); // text cell - no scientific notation, no lost leading zeros

                Cell dateCell = row.createCell(3);
                try {
                    LocalDateTime checkinDateTime = LocalDateTime.parse(v.getCheckInTime(), checkinFormat);
                    dateCell.setCellValue(checkinDateTime);
                    dateCell.setCellStyle(dateStyle);
                } catch (DateTimeParseException ex) {
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

                row.createCell(9).setCellValue(safe(v.getGuardianPhoneNums())); // text cell, same reason as LRN
            }
            if (visits.isEmpty()) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No check-in records for this date.");
            }

            rowIndex++; // blank spacer row between sections

            // --- Section 2: Inventory / Medicine Logs ---
            Row inventorySectionRow = sheet.createRow(rowIndex++);
            Cell inventorySectionCell = inventorySectionRow.createCell(0);
            inventorySectionCell.setCellValue("Activities Logs");
            inventorySectionCell.setCellStyle(sectionStyle);

            Row inventoryHeaderRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < inventoryHeaders.length; col++) {
                Cell cell = inventoryHeaderRow.createCell(col);
                cell.setCellValue(inventoryHeaders[col]);
                cell.setCellStyle(headerStyle);
            }

            boolean anyLog = false;
            for (String line : logLines) {
                if (!line.startsWith("[" + iso)) continue;
                anyLog = true;
                int close = line.indexOf(']');
                String timestamp = line.substring(1, close);
                String activity = line.substring(close + 2); // skip "] "

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(timestamp); // its own cell - column A
                row.createCell(1).setCellValue(activity);  // its own cell - column B
            }
            if (!anyLog) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No inventory activity for this date.");
            }

            rowIndex++; // blank spacer row between sections

            // --- Section 3: Stock Overview ---
            // Shows the CURRENT inventory (not historical to this date - the database only
            // tracks live quantities), flagged for expiry/low stock, plus how many units of
            // each medicine were given out on this specific date.
            Row stockSectionRow = sheet.createRow(rowIndex++);
            Cell stockSectionCell = stockSectionRow.createCell(0);
            stockSectionCell.setCellValue("STOCK OVERVIEW");
            stockSectionCell.setCellStyle(sectionStyle);

            Row stockHeaderRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < stockHeaders.length; col++) {
                Cell cell = stockHeaderRow.createCell(col);
                cell.setCellValue(stockHeaders[col]);
                cell.setCellStyle(headerStyle);
            }

            ArrayList<Medicine> inventory = medicineData.loadAll();
            for (Medicine med : inventory) {
                boolean expired = med.isExpired();
                boolean lowStock = med.isLowStock();

                String statusText = expired ? "Expired" : (lowStock ? "Low Stock" : "Normal");
                CellStyle rowStyle = expired ? expiredStyle : (lowStock ? lowStockStyle : null);

                int givenOutToday = usedTodayByMedicine.getOrDefault(med.getname(), 0);

                Row row = sheet.createRow(rowIndex++);

                Cell nameCell = row.createCell(0);
                nameCell.setCellValue(safe(med.getname()));
                if (rowStyle != null) nameCell.setCellStyle(rowStyle);

                Cell qtyCell = row.createCell(1);
                qtyCell.setCellValue(med.getquantity());
                if (rowStyle != null) qtyCell.setCellStyle(rowStyle);

                Cell expCell = row.createCell(2);
                expCell.setCellValue(safe(med.getExpDate()));
                if (rowStyle != null) expCell.setCellStyle(rowStyle);

                Cell statusCell = row.createCell(3);
                statusCell.setCellValue(statusText);
                if (rowStyle != null) statusCell.setCellStyle(rowStyle);

                Cell givenOutCell = row.createCell(4);
                givenOutCell.setCellValue(givenOutToday);
                if (rowStyle != null) givenOutCell.setCellStyle(rowStyle);
            }
            if (inventory.isEmpty()) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No medicines in inventory.");
            }

            // --- Auto-size every column, AFTER all headers and data have been written ---
            final int MAX_COLUMN_WIDTH = 50 * 256; // caps very long text columns at ~50 characters wide
            for (int col = 0; col < columnCount; col++) {
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