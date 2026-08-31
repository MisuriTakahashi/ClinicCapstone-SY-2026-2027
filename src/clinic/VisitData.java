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
 
       /**
     * Connection-based overload: reads the active visit using the caller's
     * connection/transaction. Does NOT commit or close the connection.
     */
    CheckinSystem findActiveVisit(Connection conn, String lrn) throws SQLException {
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
            + "check_in_time, status, guardian_name, guardian_phone FROM VISITS "
            + "WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
    
      /** Connection-based overload: performs the UPDATE using the caller's connection/transaction. */
    boolean markSentHome(Connection conn, String lrn) throws SQLException {
        String sql = "UPDATE VISITS SET status = 'Sent Home' "
            + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE "
            + "ORDER BY id ASC LIMIT 1)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            return ps.executeUpdate() > 0;
        }
    }

    /** Connection-based overload: performs the UPDATE using the caller's connection/transaction. */
    boolean markSentBack(Connection conn, String lrn) throws SQLException {
        String sql = "UPDATE VISITS SET status = 'Sent Back' "
            + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND status = 'In Clinic' AND archived = FALSE "
            + "ORDER BY id ASC LIMIT 1)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Transactional "sent home": finds the active visit, updates its status, and
     * writes the matching ACTIVITY_LOG entry — all in one commit/rollback unit
     * on a single shared Connection, same pattern as checkInWithMedicine() above.
     * If any step fails, everything rolls back, so a status change can never
     * exist without its audit log (or vice versa).
     */
    public boolean markSentHome(String lrn, String performedBy) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {
                CheckinSystem visit = findActiveVisit(conn, lrn);
                if (visit == null) {
                    conn.rollback();
                    return false;
                }

                boolean updated = markSentHome(conn, lrn);
                if (!updated) {
                    conn.rollback();
                    return false;
                }

                String details = "Student \"" + visit.getName()
                        + "\" (LRN: " + visit.getLrn() + ") was sent home.";

                ActivityLogData.log(conn, "SENT_HOME", details, performedBy);

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

    /**
     * Transactional "sent back to classroom" — mirror of markSentHome(lrn, performedBy) above.
     */
    public boolean markSentBack(String lrn, String performedBy) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {
                CheckinSystem visit = findActiveVisit(conn, lrn);
                if (visit == null) {
                    conn.rollback();
                    return false;
                }

                boolean updated = markSentBack(conn, lrn);
                if (!updated) {
                    conn.rollback();
                    return false;
                }

                String details = "Student \"" + visit.getName()
                        + "\" (LRN: " + visit.getLrn() + ") was sent back to the classroom.";

                ActivityLogData.log(conn, "SENT_BACK", details, performedBy);

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
                + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND archived = FALSE "
                + "ORDER BY id DESC LIMIT 1)";
 
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
     * medicine, deduct the new one), updates the visit record, AND writes
     * a single comprehensive EDIT audit-log entry — all in one
     * commit/rollback unit on a shared Connection, same pattern as
     * checkInWithMedicine()/markSentHome()/markSentBack() above.
     *
     * The "before" state is read from the database at the START of this
     * same transaction (not from whatever the caller had cached earlier),
     * so the audit entry always reflects what was actually stored. If
     * nothing about the record actually changes, no EDIT entry is written
     * at all. If the visit UPDATE fails (e.g. record no longer exists),
     * the whole transaction — including the stock adjustment and any log
     * entry — rolls back, so a log entry can never exist without its
     * matching data change (or vice versa).
     */
    public boolean editVisitWithMedicineAdjustment(
            String lrn, String newName, String newGradeSection, String newReason,
            String oldMedUsed, int oldMedsQty,
            String newMedUsed, int newMedsQty,
            String newGuardianName, String newGuardianPhone,
            MedicineData medicineData, String performedBy,
            boolean changeMedicine) throws SQLException {

        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {
                
                  CheckinSystem before = findVisitForEdit(conn, lrn);

                if (before == null) {
                    conn.rollback();
                    return false;
                }

                // Use the medicine actually stored on the row right now (read
                // inside this same transaction) as the "old" side of the stock
                // reconciliation, rather than the oldMedUsed/oldMedsQty the
                // caller captured earlier when the row was selected. Keeps the
                // restock amount correct even if the record changed in between.
                String actualOldMedUsed = before.getMedUsed();
                int actualOldMedsQty = before.getmedsQty();

                // If the user explicitly chose NO to changing medicine, the
                // database's current medicine is authoritative. Do not trust
                // the cached table selection because it may be stale (for
                // example, the table can still contain an older quantity).
                // This prevents a no-op edit from being recorded as a
                // medicine change such as 1x -> 14x.
                if (!changeMedicine) {
                    newMedUsed = actualOldMedUsed;
                    newMedsQty = actualOldMedsQty;
                }

                boolean medicineUnchanged =
                        nullToEmpty(actualOldMedUsed).equalsIgnoreCase(nullToEmpty(newMedUsed))
                        && actualOldMedsQty == newMedsQty;

                // Skip the restock/deduct pair entirely when nothing about the
                // medicine actually changed — otherwise every edit (even a
                // guardian-only or reason-only edit) would create a pointless
                // RESTOCK_MEDICINE + USE_MEDICINE pair in the audit log.
                if (!medicineUnchanged) {

                    if (actualOldMedUsed != null && !actualOldMedUsed.equalsIgnoreCase("None") && actualOldMedsQty > 0) {
                        medicineData.restockMedicine(conn, actualOldMedUsed, actualOldMedsQty, performedBy);
                    }

                    if (newMedUsed != null && !newMedUsed.equalsIgnoreCase("None") && newMedsQty > 0) {
                        medicineData.useMedicine(conn, newMedUsed, newName, newMedsQty, performedBy);
                    }
                }
                
                String editDetails = buildEditLogDetails(
                        before, newName, newGradeSection, newReason,
                        newMedUsed, newMedsQty, newGuardianName, newGuardianPhone);

                // True no-op edit: do not UPDATE the row, do not touch
                // inventory, and do not create an EDIT audit entry.
                if (editDetails == null) {
                    conn.rollback();
                    return true;
                }

                // IMPORTANT: actually persist the edited fields. The previous
                // version generated the audit message and committed inventory
                // changes without executing the VISITS UPDATE, which made the
                // audit log claim that fields changed while the table still
                // showed the old values.
                boolean updated = editVisit(conn, lrn, newName, newGradeSection,
                        newReason, newMedUsed, newMedsQty,
                        newGuardianName, newGuardianPhone);

                if (!updated) {
                    throw new SQLException("The student record could not be updated.");
                }

                ActivityLogData.log(conn, "EDIT", editDetails, performedBy);

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

    /**
     * Connection-based helper: reads the current row for a visit that is
     * eligible for editing (same WHERE clause as editVisit() itself —
     * "not archived", any status). Used to snapshot the "before" values
     * for the EDIT audit entry inside editVisitWithMedicineAdjustment(),
     * so the diff always reflects what's actually in the database, not a
     * possibly-stale UI selection.
     */
    private CheckinSystem findVisitForEdit(Connection conn, String lrn) throws SQLException {
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone FROM VISITS "
                + "WHERE lrn = ? AND archived = FALSE ORDER BY id DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

    /**
     * Builds the human-readable EDIT audit details string by comparing the
     * "before" record to the incoming new values. Returns null when every
     * editable field is unchanged, so the caller skips writing an EDIT
     * entry entirely (no-op edits are never logged).
     */
    private static String buildEditLogDetails(
            CheckinSystem before, String newName, String newGradeSection, String newReason,
            String newMedUsed, int newMedsQty,
            String newGuardianName, String newGuardianPhone) {

        ArrayList<String> changes = new ArrayList<>();

        String oldName = nullToEmpty(before.getName());
        String oldGradeSection = nullToEmpty(before.getGradeSection());
        String oldReason = nullToEmpty(before.getReason());
        String oldMedUsed = nullToEmpty(before.getMedUsed());
        int oldMedsQty = before.getmedsQty();
        String oldGuardianName = nullToEmpty(before.getGuardianName());
        String oldGuardianPhone = nullToEmpty(before.getGuardianPhoneNums());

        String safeNewName = nullToEmpty(newName);
        String safeNewGradeSection = nullToEmpty(newGradeSection);
        String safeNewReason = nullToEmpty(newReason);
        String safeNewMedUsed = nullToEmpty(newMedUsed);
        String safeNewGuardianName = nullToEmpty(newGuardianName);
        String safeNewGuardianPhone = nullToEmpty(newGuardianPhone);

        if (!oldName.equals(safeNewName)) {
            changes.add("Name changed from \"" + oldName + "\" to \"" + safeNewName + "\"");
        }

        if (!oldGradeSection.equals(safeNewGradeSection)) {
            changes.add("Grade/Section changed from \"" + oldGradeSection
                    + "\" to \"" + safeNewGradeSection + "\"");
        }

        if (!oldReason.equals(safeNewReason)) {
            changes.add("Reason changed");
        }

        boolean medicineChanged =
                !oldMedUsed.equalsIgnoreCase(safeNewMedUsed) || oldMedsQty != newMedsQty;
        if (medicineChanged) {
            String fromMed = oldMedUsed.isEmpty() || oldMedUsed.equalsIgnoreCase("None")
                    ? "no medicine" : oldMedsQty + "x " + oldMedUsed;
            String toMed = safeNewMedUsed.isEmpty() || safeNewMedUsed.equalsIgnoreCase("None")
                    ? "no medicine" : newMedsQty + "x " + safeNewMedUsed;
            changes.add("Medicine changed from " + fromMed + " to " + toMed);
        }

        if (!oldGuardianName.equals(safeNewGuardianName)) {
            changes.add("Guardian changed from \"" + oldGuardianName + "\" to \"" + safeNewGuardianName + "\"");
        }

        if (!oldGuardianPhone.equals(safeNewGuardianPhone)) {
            changes.add("Guardian phone changed from \"" + oldGuardianPhone
                    + "\" to \"" + safeNewGuardianPhone + "\"");
        }

        if (changes.isEmpty()) {
            return null;
        }

        return "Student \"" + safeNewName + "\" (LRN: " + before.getLrn() + ") was edited. "
                + String.join("; ", changes) + ".";
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
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