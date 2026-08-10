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

        String now = LocalDateTime.now().format(TIME_FORMAT);
        String sql = "INSERT INTO VISITS(name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = "SELECT 1 FROM VISITS WHERE lrn = ? AND status = 'In Clinic'";
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
                + "WHERE id = (SELECT id FROM VISITS WHERE lrn = ? AND status = 'In Clinic' "
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

        for (CheckinSystem v : loadAll()) {
            if (v.getCheckInTime().startsWith(today)) {
                totalToday++;
                if (v.getStatus().equals("Sent Home")) sentHomeToday++;
            }
        }
        return new int[]{totalToday, sentHomeToday};
    }

    public CheckinSystem findActiveVisit(String lrn) throws SQLException {
        String sql = "SELECT name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone FROM VISITS "
                + "WHERE lrn = ? AND status = 'In Clinic'";

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
                              String newReason, String newMedUsed, int newMedsQty) throws SQLException {

        String sql = "UPDATE VISITS SET name = ?, grade_section = ?, reason = ?, "
                + "med_used = ?, meds_qty = ? WHERE lrn = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, newGradeSection);
            ps.setString(3, newReason);
            ps.setString(4, newMedUsed);
            ps.setInt(5, newMedsQty);
            ps.setString(6, lrn);
            return ps.executeUpdate() > 0;
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
}
