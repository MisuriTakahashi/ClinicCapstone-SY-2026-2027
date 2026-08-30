/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
 
/**
 *
 * @author PC
 */
 
 
public class VisitData {
     private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
 
    public void checkIn(String name, String gradeSection, String lrn, String reason, String medUsed,
                         int medsQty, String guardianName, String guardianPhone) throws SQLException {
 
        try (Connection conn = DatabaseManager.getConnection()) {
            checkIn(conn, name, gradeSection, lrn, reason, medUsed, medsQty, guardianName, guardianPhone);
        }
    }
 
    /**
     * Connection-based overload: performs the INSERT using the caller's
     * connection/transaction. Does NOT commit or close the connection.
     */
    void checkIn(Connection conn, String name, String gradeSection, String lrn, String reason, String medUsed,
                 int medsQty, String guardianName, String guardianPhone) throws SQLException {
 
        String now = LocalDateTime.now().format(TIME_FORMAT);
        String sql = "INSERT INTO VISITS(name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone) VALUES(?,?,?,?,?,?,?,?,?,?)";
 
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, gradeSection);
            ps.setString(3, lrn);
            ps.setString(4, reason);
            ps.setString(5, medUsed);
            ps.setInt(6, medsQty);
            ps.setString(7, now);
            ps.setString(8, "In Clinic");
            ps.setString(9, guardianName);
            ps.setString(10, guardianPhone);
            ps.executeUpdate();
        }
    }
 
    /** Result of a transactional check-in: whether it was recorded, and whether stock was deducted. */
    public record CheckInResult(boolean success, boolean medicineDeducted) {
    }
 
    /**
     * Transactional check-in: records the visit AND deducts medicine stock
     * (when applicable) in a single commit/rollback unit, using one shared
     * connection. Replaces the previous pattern of calling checkIn() and
     * MedicineData.useMedicine() as two independent, uncoordinated writes.
     *
     * Matches prior UI behavior: if stock is insufficient, the check-in
     * still succeeds (medicineDeducted = false) so the front desk can warn
     * the user without blocking the check-in itself.
     */
    public CheckInResult checkInWithMedicine(
        String name, String gradeSection, String lrn, String reason,
        String medUsed, int medsQty, String guardianName, String guardianPhone,
        MedicineData medicineData, String performedBy) throws SQLException {

    try (Connection conn = DatabaseManager.getConnection()) {

        conn.setAutoCommit(false);

        try {
            checkIn(conn, name, gradeSection, lrn, reason, medUsed, medsQty, guardianName, guardianPhone);

            boolean deducted = true;

            if (medUsed != null && !medUsed.equalsIgnoreCase("None") && medsQty > 0) {
                deducted = medicineData.useMedicine(conn, medUsed, name, medsQty, performedBy);
            }

            // Record the check-in itself in the shared activity log, in the SAME
            // transaction as the VISITS insert / medicine deduction above. If
            // anything fails, the rollback below undoes this too — so a VISITS
            // row can never exist without a matching log entry, or vice versa.
            String logDetails = buildCheckInLogDetails(
                    name, gradeSection, lrn, reason, medUsed, medsQty, deducted);

            ActivityLogData.log(conn, "CHECK_IN", logDetails, performedBy);

            conn.commit();

            return new CheckInResult(true, deducted);

        } catch (SQLException | RuntimeException ex) {

            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }

            throw ex;

        } finally {
            conn.setAutoCommit(true);
        }
    }
}

/** Builds a human-readable ACTIVITY_LOG details string for a check-in. */
private static String buildCheckInLogDetails(
        String name, String gradeSection, String lrn, String reason,
        String medUsed, int medsQty, boolean deducted) {

    StringBuilder sb = new StringBuilder();
    sb.append("Student ").append(name)
      .append(" (LRN: ").append(lrn).append(", ").append(gradeSection).append(")")
      .append(" checked in.");

    if (reason != null && !reason.isBlank()) {
        sb.append(" Reason: ").append(reason).append(".");
    }

       if (medUsed != null && !medUsed.equalsIgnoreCase("None") && medsQty > 0) {
            sb.append(" Medicine given: ").append(medsQty).append("x ").append(medUsed);
            if (!deducted) {
                sb.append(" (stock insufficient — not deducted)");
            }
            sb.append(".");
        } else {
            sb.append(" No medicine given.");
        }

        return sb.toString();
}
 
    public ArrayList<CheckinSystem> loadAll() throws SQLException {
        ArrayList<CheckinSystem> visits = new ArrayList<>();
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone FROM VISITS";
 
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                visits.add(new CheckinSystem(
                        rs.getString("name"),
                        rs.getString("grade_section"),
                        rs.getString("lrn"),
                        rs.getString("reason"),
                        rs.getString("med_used"),
                        rs.getInt("meds_qty"),
                        rs.getString("check_in_time"),
                        rs.getString("status"),
                        rs.getString("guardian_name"),
                        rs.getString("guardian_phone")
                ));
            }
        }
        return visits;
    }
 
    public boolean isCurrentlyCheckedIn(String lrn) throws SQLException {
         String sql = "SELECT 1 FROM VISITS WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
 
    public boolean markSentHome(String lrn) throws SQLException {
       String sql = "UPDATE VISITS SET status = 'Sent Home' "
            + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE "
            + "ORDER BY id ASC LIMIT 1)";
 
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            return ps.executeUpdate() > 0;
        }
    }
 
    public int[] getTodayCounts() throws SQLException {
        String today = LocalDate.now().toString();
        int totalToday = 0;
        int sentHomeToday = 0;
        int sentBackToday = 0;
 
        String sql = "SELECT status FROM VISITS WHERE check_in_time LIKE ? AND archived = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totalToday++;
                    String status = rs.getString("status");
                    if ("Sent Home".equals(status)) sentHomeToday++;
                    else if ("Sent Back".equals(status)) sentBackToday++;
                }
            }
        }
        return new int[]{totalToday, sentHomeToday, sentBackToday};
    }
    
    public ArrayList<CheckinSystem> getVisitsByDate(String dateIso) throws SQLException {
        ArrayList<CheckinSystem> visits = new ArrayList<>();
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone FROM VISITS "
                + "WHERE check_in_time LIKE ? ORDER BY id ASC";
 
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dateIso + "%"); // dateIso = "yyyy-MM-dd"
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    visits.add(new CheckinSystem(
                            rs.getString("name"), rs.getString("grade_section"), rs.getString("lrn"),
                            rs.getString("reason"), rs.getString("med_used"), rs.getInt("meds_qty"),
                            rs.getString("check_in_time"), rs.getString("status"),
                            rs.getString("guardian_name"), rs.getString("guardian_phone")
                    ));
                }
            }
        }
        return visits;
    }
    
    /** Marks all visits for the given date as archived. Does NOT delete any rows. */
    public int archiveDate(String dateIso) throws SQLException {
        String sql = "UPDATE VISITS SET archived = TRUE WHERE check_in_time LIKE ? AND archived = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dateIso + "%");
            return ps.executeUpdate(); // returns how many rows were archived
        }
}
    
    /** Same as loadAll(), but excludes archived visits — used for the "current" check-in view. */
    public ArrayList<CheckinSystem> loadActive() throws SQLException {
        ArrayList<CheckinSystem> visits = new ArrayList<>();
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone FROM VISITS WHERE archived = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                visits.add(new CheckinSystem(
                        rs.getString("name"), rs.getString("grade_section"), rs.getString("lrn"),
                        rs.getString("reason"), rs.getString("med_used"), rs.getInt("meds_qty"),
                        rs.getString("check_in_time"), rs.getString("status"),
                        rs.getString("guardian_name"), rs.getString("guardian_phone")
                ));
            }
        }
        return visits;
}
 
    public CheckinSystem findActiveVisit(String lrn) throws SQLException {
         String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
            + "check_in_time, status, guardian_name, guardian_phone FROM VISITS "
            + "WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE";
 
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CheckinSystem(
                            rs.getString("name"),
                            rs.getString("grade_section"),
                            rs.getString("lrn"),
                            rs.getString("reason"),
                            rs.getString("med_used"),
                            rs.getInt("meds_qty"),
                            rs.getString("check_in_time"),
                            rs.getString("status"),
                            rs.getString("guardian_name"),
                            rs.getString("guardian_phone")
                    );
                }
                return null;
            }
        }
    }
 
    public boolean editVisit(String lrn, String newName, String newGradeSection,
                        
            String newReason, String newMedUsed, int newMedsQty,
            String newGuardianName, String newGuardianPhone) throws SQLException {
 
        try (Connection conn = DatabaseManager.getConnection()) {
            return editVisit(conn, lrn, newName, newGradeSection, newReason, newMedUsed, newMedsQty,
                    newGuardianName, newGuardianPhone);
        }
    }
 
    /**
     * Connection-based overload: performs the UPDATE using the caller's
     * connection/transaction. Does NOT commit or close the connection.
     */
    boolean editVisit(Connection conn, String lrn, String newName, String newGradeSection,
            String newReason, String newMedUsed, int newMedsQty,
            String newGuardianName, String newGuardianPhone) throws SQLException {
 
            String sql = "UPDATE VISITS SET name = ?, grade_section = ?, reason = ?, "
                + "med_used = ?, meds_qty = ?, guardian_name = ?, guardian_phone = ? "
                + "WHERE lrn = ? AND archived = FALSE";
 
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newName);
                ps.setString(2, newGradeSection);
                ps.setString(3, newReason);
                ps.setString(4, newMedUsed);
                ps.setInt(5, newMedsQty);
                ps.setString(6, newGuardianName);
                ps.setString(7, newGuardianPhone);
                ps.setString(8, lrn);
                return ps.executeUpdate() > 0;
            }
    }
 
    /**
     * Transactional edit: reconciles medicine stock (restock the old
     * medicine, deduct the new one) AND updates the visit record in a
     * single commit/rollback unit.
     *
     * This fixes a real bug in the previous uncoordinated flow: if the
     * visit UPDATE failed (e.g. record no longer exists), the stock
     * changes made just before it were never rolled back, silently
     * corrupting inventory counts. Here, if editVisit reports no match,
     * the whole transaction — including the stock adjustment — rolls back.
     */
    public boolean editVisitWithMedicineAdjustment(
            String lrn, String newName, String newGradeSection, String newReason,
            String oldMedUsed, int oldMedsQty,
            String newMedUsed, int newMedsQty,
            String newGuardianName, String newGuardianPhone,
            MedicineData medicineData, String performedBy) throws SQLException {
 
        try (Connection conn = DatabaseManager.getConnection()) {
 
            conn.setAutoCommit(false);
 
            try {
                if (oldMedUsed != null && !oldMedUsed.equalsIgnoreCase("None") && oldMedsQty > 0) {
                    medicineData.restockMedicine(conn, oldMedUsed, oldMedsQty, performedBy);
                }
 
                if (newMedUsed != null && !newMedUsed.equalsIgnoreCase("None") && newMedsQty > 0) {
                    medicineData.useMedicine(conn, newMedUsed, newName, newMedsQty, performedBy);
                }
 
                boolean success = editVisit(conn, lrn, newName, newGradeSection, newReason,
                        newMedUsed, newMedsQty, newGuardianName, newGuardianPhone);
 
                if (!success) {
                    conn.rollback();
                    return false;
                }
 
                conn.commit();
                return true;
 
            } catch (SQLException | RuntimeException ex) {
 
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
 
                throw ex;
 
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
 
    public String findNameForLrn(String lrn) throws SQLException {
        String sql = "SELECT name FROM VISITS WHERE lrn = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        }
    }
    
    public boolean markSentBack(String lrn) throws SQLException {
        String sql = "UPDATE VISITS SET status = 'Sent Back' "
            + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE "
            + "ORDER BY id ASC LIMIT 1)";
 
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            return ps.executeUpdate() > 0;
        }
    }
    
    
}