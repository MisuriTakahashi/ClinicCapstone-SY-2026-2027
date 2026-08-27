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

    public boolean useMedicine(
            String productName,
            String studentName,
            int quantity,
            String performedBy)
            throws SQLException, IOException {

        String sql =
                "UPDATE MEDICINES "
                + "SET quantity = quantity - ? "
                + "WHERE name = ? "
                + "AND quantity >= ?";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
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

        logActivity(
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

        String sql =
                "UPDATE MEDICINES "
                + "SET quantity = quantity + ? "
                + "WHERE name = ?";

        int rows;

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, productName);

            rows = ps.executeUpdate();
        }

        if (rows > 0) {

            logActivity(
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