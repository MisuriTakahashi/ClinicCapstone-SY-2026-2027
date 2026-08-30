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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

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

    /** Returns true if there is at least one check-in or activity-log record for the date. */
    public boolean hasRecordsForDate(LocalDate date) throws SQLException, IOException {
        if (!visitData.getVisitsByDate(date.toString()).isEmpty()) return true;
        for (ActivityLogData.Entry entry : ActivityLogData.loadEntries()) {
            if (entry.timestamp() == null) continue;
            if (entry.timestamp().toLocalDateTime().toLocalDate().equals(date)) return true;
        }
        return false;
    }

    /** Writes the combined Check-in Logs + Inventory/Medicine Logs + Stock Overview + Statistics report as a real .xlsx workbook. */
    public void writeDailyReport(LocalDate date, File destination, String performedBy) throws SQLException, IOException {
        String iso = date.toString();
        ArrayList<CheckinSystem> visits = visitData.getVisitsByDate(iso);

        // Structured entries for this date, read directly from ACTIVITY_LOG via
        // entry.timestamp().toLocalDateTime().toLocalDate(). This is the single
        // source used both for the per-medicine "given out today" totals below
        // AND for the ACTIVITIES / AUDIT LOG section further down — no more
        // parsing loadFormatted() strings or matching an ISO date prefix against
        // them (that format mismatch was why activities were being skipped).
        ArrayList<ActivityLogData.Entry> allEntries = ActivityLogData.loadEntries();
        ArrayList<ActivityLogData.Entry> entriesForDate = new ArrayList<>();
        for (ActivityLogData.Entry entry : allEntries) {
            if (entry.timestamp() == null) continue;
            LocalDate entryDate = entry.timestamp().toLocalDateTime().toLocalDate();
            if (!entryDate.equals(date)) continue;
            entriesForDate.add(entry);
        }

        HashMap<String, Integer> usedTodayByMedicine = new HashMap<>();
        HashMap<String, Integer> returnedTodayByMedicine = new HashMap<>();
        for (ActivityLogData.Entry entry : entriesForDate) {
            ActivityLogData.UsageInfo usage =
                    ActivityLogData.parseMedicineUsage(entry.action(), entry.details());
            if (usage != null) {
                usedTodayByMedicine.merge(
                        usage.medicineName(),
                        usage.quantity(),
                        Integer::sum
                );
                continue;
            }

            ActivityLogData.UsageInfo restock =
                    ActivityLogData.parseMedicineRestock(entry.action(), entry.details());
            if (restock != null) {
                returnedTodayByMedicine.merge(
                        restock.medicineName(),
                        restock.quantity(),
                        Integer::sum
                );
            }
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

            // --- Statistics section styles ---
            CellStyle statLabelStyle = workbook.createCellStyle();
            Font statLabelFont = workbook.createFont();
            statLabelFont.setBold(true);
            statLabelStyle.setFont(statLabelFont);
            statLabelStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            statLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            statLabelStyle.setBorderTop(BorderStyle.THIN);
            statLabelStyle.setBorderBottom(BorderStyle.THIN);
            statLabelStyle.setBorderLeft(BorderStyle.THIN);
            statLabelStyle.setBorderRight(BorderStyle.THIN);

            CellStyle statValueStyle = workbook.createCellStyle();
            statValueStyle.setBorderTop(BorderStyle.THIN);
            statValueStyle.setBorderBottom(BorderStyle.THIN);
            statValueStyle.setBorderLeft(BorderStyle.THIN);
            statValueStyle.setBorderRight(BorderStyle.THIN);

            CellStyle statValueCenterStyle = workbook.createCellStyle();
            statValueCenterStyle.cloneStyleFrom(statValueStyle);
            statValueCenterStyle.setAlignment(HorizontalAlignment.CENTER);
            Font statValueCenterFont = workbook.createFont();
            statValueCenterFont.setBold(true);
            statValueCenterStyle.setFont(statValueCenterFont);

            String[] checkinHeaders = {"Student Name", "Grade/Section", "LRN", "Check-in Date/Time", "Reason",
                    "Medicine Used", "Medicine Quantity", "Status", "Guardian Name", "Guardian Phone"};
            String[] inventoryHeaders = {"Date/Time", "Action / Activity", "Details", "Performed By"};
            String[] stockHeaders = {"Medicine Name", "Current Quantity", "Expiration Date", "Status",
                    "Given Out (This Date)", "Returned (Edited Visits)"};
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

            // --- Section 2: Activities / Audit Log ---
            // Every ACTIVITY_LOG record for the selected date (check-ins, sent home/back,
            // medicine actions, account actions, report exports, etc.) - not just medicine
            // activity. Built from entriesForDate (structured Entry records, computed above),
            // never from loadFormatted() strings, so this no longer depends on any particular
            // display date format.
            Row inventorySectionRow = sheet.createRow(rowIndex++);
            Cell inventorySectionCell = inventorySectionRow.createCell(0);
            inventorySectionCell.setCellValue("ACTIVITIES / AUDIT LOG");
            inventorySectionCell.setCellStyle(sectionStyle);

            Row inventoryHeaderRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < inventoryHeaders.length; col++) {
                Cell cell = inventoryHeaderRow.createCell(col);
                cell.setCellValue(inventoryHeaders[col]);
                cell.setCellStyle(headerStyle);
            }

            // entriesForDate comes from loadEntries(), which orders newest-first; the report
            // reads more naturally in chronological order, so walk it back-to-front here
            // without mutating the shared list (still used above for usedTodayByMedicine).
            for (int i = entriesForDate.size() - 1; i >= 0; i--) {
                ActivityLogData.Entry entry = entriesForDate.get(i);

                Row row = sheet.createRow(rowIndex++);

                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(entry.timestamp().toLocalDateTime());
                dateCell.setCellStyle(dateStyle);

                row.createCell(1).setCellValue(safe(entry.action()));

                String detailsText =
                        ActivityLogData.humanizeDetails(entry.action(), entry.details());
                Cell detailsCell = row.createCell(2);
                detailsCell.setCellValue(safe(detailsText));
                detailsCell.setCellStyle(wrapStyle);

                // The actual logged-in account name - never overwritten with "Unknown"
                // unless the stored record itself has no actor.
                row.createCell(3).setCellValue(
                        safe(ActivityLogData.displayActor(entry.actor())));
            }
            if (entriesForDate.isEmpty()) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No activities for this date.");
            }

            rowIndex++; // blank spacer row between sections

            // --- Section 3: Stock Overview ---
            // Shows the CURRENT inventory (not historical to this date - the database only
            // tracks live quantities), flagged for expiry/low stock, plus:
            //   - "Given Out (This Date)": the NET amount actually given out on this date
            //     (raw USE_MEDICINE total minus anything returned the same date via an
            //     edited visit), so it never shows units as "given out" once they've been
            //     put back.
            //   - "Returned (Edited Visits)": the raw RESTOCK_MEDICINE total for this date,
            //     shown on its own so a return is visible instead of just silently zeroing
            //     out the given-out figure.
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

                int rawGivenOutToday = usedTodayByMedicine.getOrDefault(med.getname(), 0);
                int rawReturnedToday = returnedTodayByMedicine.getOrDefault(med.getname(), 0);

                // Net BOTH sides against each other, the same way a ledger nets debits
                // against credits. Every edit fires a matched RESTOCK (undo old amount)
                // + USE (apply new amount) pair, so editing the same visit twice adds two
                // of each - summing raw restocks alone was inflating "Returned" (e.g. 200)
                // even when the day's net effect on that medicine was actually zero.
                // Netting both ways means exactly one side is non-zero (or both zero),
                // and it always matches the medicine's truly-current given-out amount.
                // The full step-by-step history (every individual restock/use) is still
                // visible above in ACTIVITIES / AUDIT LOG - nothing is hidden, this just
                // stops double-counting the same units bouncing back and forth.
                int netGivenOutToday = Math.max(0, rawGivenOutToday - rawReturnedToday);
                int netReturnedToday = Math.max(0, rawReturnedToday - rawGivenOutToday);

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
                givenOutCell.setCellValue(netGivenOutToday);
                if (rowStyle != null) givenOutCell.setCellStyle(rowStyle);

                Cell returnedCell = row.createCell(5);
                returnedCell.setCellValue(netReturnedToday);
                if (rowStyle != null) returnedCell.setCellStyle(rowStyle);
            }
            if (inventory.isEmpty()) {
                sheet.createRow(rowIndex++).createCell(0).setCellValue("No medicines in inventory.");
            }

            rowIndex++; // blank spacer row between sections

            // --- Section 4: Statistics ---
            // Reuses AdminPanel.computeWeeklyStats() — the exact same computation that
            // drives the on-screen statistics cards — for the Monday-Friday week that
            // CONTAINS the selected report date (not just the single selected day).
            Row statsSectionRow = sheet.createRow(rowIndex++);
            Cell statsSectionCell = statsSectionRow.createCell(0);
            statsSectionCell.setCellValue("STATISTICS");
            statsSectionCell.setCellStyle(sectionStyle);

            ArrayList<CheckinSystem> allVisits = visitData.loadAll(); // includes archived, same as AdminPanel
            AdminPanel.WeeklyStats stats = AdminPanel.computeWeeklyStats(allVisits, date);

            DateTimeFormatter periodFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");

            Row periodRow = sheet.createRow(rowIndex++);
            Cell periodLabelCell = periodRow.createCell(0);
            periodLabelCell.setCellValue("Reporting Period");
            periodLabelCell.setCellStyle(statLabelStyle);

            Cell periodValueCell = periodRow.createCell(1);
            periodValueCell.setCellValue(
                    stats.mondayOfWeek().format(periodFormat) + " - " + stats.fridayOfWeek().format(periodFormat));
            periodValueCell.setCellStyle(statValueStyle);

            rowIndex++; // blank spacer row

            Object[][] statRows = {
                {"Weekly Check-ins", stats.weeklyCheckins()},
                {"Currently in Clinic", stats.inClinic()},
                {"Sent Back", stats.sentBack()},
                {"Sent Home", stats.sentHome()},
                {"Frequently Used Medicine", stats.topMedicine()},
                {"Common Reason", stats.topReason()}
            };
            for (Object[] pair : statRows) {
                Row row = sheet.createRow(rowIndex++);

                Cell labelCell = row.createCell(0);
                labelCell.setCellValue((String) pair[0]);
                labelCell.setCellStyle(statLabelStyle);

                Cell valueCell = row.createCell(1);
                if (pair[1] instanceof Integer count) {
                    valueCell.setCellValue(count);
                } else {
                    valueCell.setCellValue((String) pair[1]);
                }
                valueCell.setCellStyle(statValueStyle);
            }

            rowIndex++; // blank spacer row

            Row dailySectionRow = sheet.createRow(rowIndex++);
            Cell dailySectionCell = dailySectionRow.createCell(0);
            dailySectionCell.setCellValue("DAILY CHECK-INS");
            dailySectionCell.setCellStyle(sectionStyle);

            String[] dailyHeaders = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            Row dailyHeaderRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < dailyHeaders.length; col++) {
                Cell cell = dailyHeaderRow.createCell(col);
                cell.setCellValue(dailyHeaders[col]);
                cell.setCellStyle(headerStyle);
            }

            int[] dailyCounts = {stats.monday(), stats.tuesday(), stats.wednesday(), stats.thursday(), stats.friday()};
            Row dailyValueRow = sheet.createRow(rowIndex++);
            for (int col = 0; col < dailyCounts.length; col++) {
                Cell cell = dailyValueRow.createCell(col);
                cell.setCellValue(dailyCounts[col]);
                cell.setCellStyle(statValueCenterStyle);
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

            // Log the export only now that the .xlsx has actually been saved to disk.
            // If the log insert itself fails (e.g. DB unreachable), the export is NOT
            // considered failed — the file already exists — so this is reported to
            // stderr only, same pattern as the legacy-log import in DatabaseManager.
            try {
                String details = "Exported daily clinic report for " + iso
                        + " to: " + destination.getAbsolutePath();
                ActivityLogData.log("EXPORT_REPORT", details, performedBy);
            } catch (SQLException logEx) {
                System.err.println("Could not record EXPORT_REPORT activity log: " + logEx.getMessage());
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

    public void writeCheckinReport(LocalDate date, File destination, String performedBy) throws SQLException, IOException {
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

            // Log the export only now that the .xlsx has actually been saved to disk.
            // Same non-fatal handling as writeDailyReport() above.
            try {
                String details = "Exported check-in log report for " + iso
                        + " to: " + destination.getAbsolutePath();
                ActivityLogData.log("EXPORT_REPORT", details, performedBy);
            } catch (SQLException logEx) {
                System.err.println("Could not record EXPORT_REPORT activity log: " + logEx.getMessage());
            }
        }
    }

    /** Returns an empty string instead of null, for safe writing into Excel cells. */
    private String safe(String value) {
        return value == null ? "" : value;
    }
}