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

        /** Writes a Check-in-ONLY CSV report (no inventory) for the given date. */
        public void writeCheckinReport(LocalDate date, File destination) throws SQLException, IOException {
            String iso = date.toString();
            ArrayList<CheckinSystem> visits = visitData.getVisitsByDate(iso);

            try (PrintWriter out = new PrintWriter(destination, "UTF-8")) {
                String prettyDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                        + " - " + date.getDayOfWeek().toString().substring(0, 1)
                        + date.getDayOfWeek().toString().substring(1).toLowerCase();
                out.println("Student Check-in Log Report");
                out.println("Date: " + prettyDate);
                out.println();

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
            }
        }
}
