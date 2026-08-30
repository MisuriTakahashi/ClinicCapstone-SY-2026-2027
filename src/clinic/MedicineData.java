/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data access for medicines/inventory.
 *
 * Activity logging is database-backed. The old String-path constructor is
 * retained only for source compatibility with the existing Swing forms;
 * the supplied path is no longer used.
 */
public class MedicineData {

    public MedicineData(String ignoredActivityLogPath) {
        // Kept for compatibility.
        // Activity records now live in H2.
    }

    public MedicineData() {
        // Activity records now live in H2.
    }

    public ArrayList<Medicine> loadAll()
            throws SQLException {

        ArrayList<Medicine> medicine =
                new ArrayList<>();

        String sql =
                "SELECT name, exp_date, quantity "
                + "FROM MEDICINES";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                medicine.add(
                        new Medicine(
                                rs.getString("name"),
                                rs.getString("exp_date"),
                                rs.getInt("quantity")
                        )
                );
            }
        }

        return medicine;
    }

    public void addItem(
            String name,
            String expDate,
            int quantity,
            String performedBy)
            throws SQLException, IOException {

        String sql =
                "INSERT INTO MEDICINES("
                + "name, exp_date, quantity"
                + ") VALUES(?, ?, ?)";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, expDate);
            ps.setInt(3, quantity);

            ps.executeUpdate();
        }

        logActivity(
                "ADD_MEDICINE",
                quantity + "x " + name,
                performedBy
        );
    }

    private void logActivity(
            String action,
            String details,
            String performedBy)
            throws IOException {

        try {

            ActivityLogData.log(
                    action,
                    details,
                    performedBy
            );

        } catch (SQLException ex) {

            throw new IOException(
                    "Could not save activity to "
                    + "the H2 database.",
                    ex
            );
        }
    }

    public boolean editItem(
            String currentName,
            String newName,
            String newExpDate,
            int newQuantity,
            String performedBy)
            throws SQLException, IOException {

        String sql =
                "UPDATE MEDICINES "
                + "SET name = ?, exp_date = ?, quantity = ? "
                + "WHERE name = ?";

        int rows;

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, newExpDate);
            ps.setInt(3, newQuantity);
            ps.setString(4, currentName);

            rows = ps.executeUpdate();
        }

        if (rows == 0) {
            return false;
        }

        logActivity(
                "EDIT_MEDICINE",
                currentName
                + " -> "
                + newName
                + " ("
                + newQuantity
                + "x)",
                performedBy
        );

        return true;
    }

    public boolean deleteItem(
            String name,
            String performedBy)
            throws SQLException, IOException {

        String sql =
                "DELETE FROM MEDICINES "
                + "WHERE name = ?";

        int rows;

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name);

            rows = ps.executeUpdate();
        }

        if (rows == 0) {
            return false;
        }

        logActivity(
                "DELETE_MEDICINE",
                name,
                performedBy
        );

        return true;
    }

    /**
     * Kept with the same signature used by the existing Admin Panel and
     * ReportExporter. It now reads from ACTIVITY_LOG in H2.
     */
    public ArrayList<String> loadActivityLog()
            throws IOException {

        try {

            return ActivityLogData.loadFormatted();

        } catch (SQLException ex) {

            throw new IOException(
                    "Could not load activity records "
                    + "from H2.",
                    ex
            );
        }
    }

    /**
     * Public entry point — opens its own connection and transaction.
     * The stock UPDATE and the activity-log INSERT now commit together,
     * so a crash between them can no longer desync the audit log from
     * the actual stock count.
     */
    public boolean useMedicine(
            String productName,
            String studentName,
            int quantity,
            String performedBy)
            throws SQLException, IOException {

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                boolean deducted =
                        useMedicine(
                                conn,
                                productName,
                                studentName,
                                quantity,
                                performedBy
                        );

                conn.commit();

                return deducted;

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
     * Connection-based overload: performs the atomic stock deduction and
     * activity log insert using the caller's connection/transaction.
     * Does NOT commit or close the connection — the caller owns that.
     * Used by VisitData's transactional check-in/edit-visit methods so
     * the visit write and the stock deduction commit or roll back
     * together.
     */
    boolean useMedicine(
            Connection conn,
            String productName,
            String studentName,
            int quantity,
            String performedBy)
            throws SQLException {

        String sql =
                "UPDATE MEDICINES "
                + "SET quantity = quantity - ? "
                + "WHERE name = ? "
                + "AND quantity >= ?";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, productName);
            ps.setInt(3, quantity);

            int rows =
                    ps.executeUpdate();

            if (rows == 0) {
                return false;
            }
        }

        ActivityLogData.log(
                conn,
                "USE_MEDICINE",
                "Student "
                + studentName
                + " used "
                + quantity
                + "x "
                + productName,
                performedBy
        );

        return true;
    }

    /**
     * Public entry point — opens its own connection and transaction, so
     * the stock UPDATE and its activity-log INSERT commit together.
     */
    public boolean restockMedicine(
            String productName,
            int quantity,
            String performedBy)
            throws SQLException, IOException {

        if (productName == null
                || productName.equalsIgnoreCase("None")
                || quantity <= 0) {

            return false;
        }

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                boolean restocked =
                        restockMedicine(
                                conn,
                                productName,
                                quantity,
                                performedBy
                        );

                conn.commit();

                return restocked;

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
     * Connection-based overload, same shape as {@link #useMedicine(Connection,
     * String, String, int, String)}. Does NOT validate productName/quantity —
     * callers (the public overload above, or VisitData's transactional
     * methods) are expected to have already checked those.
     */
    boolean restockMedicine(
            Connection conn,
            String productName,
            int quantity,
            String performedBy)
            throws SQLException {

        String sql =
                "UPDATE MEDICINES "
                + "SET quantity = quantity + ? "
                + "WHERE name = ?";

        int rows;

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, productName);

            rows = ps.executeUpdate();
        }

        if (rows > 0) {

            ActivityLogData.log(
                    conn,
                    "RESTOCK_MEDICINE",
                    "Returned "
                    + quantity
                    + "x "
                    + productName
                    + " (visit edited)",
                    performedBy
            );

            return true;
        }

        return false;
    }

    public boolean nameExists(String name)
            throws SQLException {

        String sql =
                "SELECT 1 FROM MEDICINES "
                + "WHERE name = ?";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs =
                         ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    public Medicine findByName(String name)
            throws SQLException {

        String sql =
                "SELECT name, exp_date, quantity "
                + "FROM MEDICINES "
                + "WHERE name = ?";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return new Medicine(
                            rs.getString("name"),
                            rs.getString("exp_date"),
                            rs.getInt("quantity")
                    );
                }

                return null;
            }
        }
    }
}