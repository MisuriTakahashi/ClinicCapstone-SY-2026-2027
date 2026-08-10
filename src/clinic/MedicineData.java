/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
/**
 *
 * @author PC
 */


public class MedicineData {
     private final File activityLogFile;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MedicineData(String activityLogPath) {
        this.activityLogFile = new File(activityLogPath);
    }

    public MedicineData() {
        this("inventory_activity.log");
    }

    public ArrayList<Medicine> loadAll() throws SQLException {
        ArrayList<Medicine> medicine = new ArrayList<>();
        String sql = "SELECT name, exp_date, quantity FROM MEDICINES";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                medicine.add(new Medicine(
                        rs.getString("name"),
                        rs.getString("exp_date"),
                        rs.getInt("quantity")
                ));
            }
        }
        return medicine;
    }

    public void addItem(String name, String expDate, int quantity) throws SQLException, IOException {
        String sql = "INSERT INTO MEDICINES(name, exp_date, quantity) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, expDate);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
        logActivity("Added " + quantity + "x " + name);
    }

    private void logActivity(String message) throws IOException {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(activityLogFile, true))) {
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        }
    }

    public boolean editItem(String currentName, String newName, String newExpDate, int newQuantity)
            throws SQLException, IOException {

        String sql = "UPDATE MEDICINES SET name = ?, exp_date = ?, quantity = ? WHERE name = ?";
        int rows;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, newExpDate);
            ps.setInt(3, newQuantity);
            ps.setString(4, currentName);
            rows = ps.executeUpdate();
        }
        if (rows == 0) return false;
        logActivity("Edited " + currentName + " -> " + newName + " (" + newQuantity + "x)");
        return true;
    }

    public boolean deleteItem(String name) throws SQLException, IOException {
        String sql = "DELETE FROM MEDICINES WHERE name = ?";
        int rows;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            rows = ps.executeUpdate();
        }
        if (rows == 0) return false;
        logActivity("Deleted " + name);
        return true;
    }

    public ArrayList<String> loadActivityLog() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        if (!activityLogFile.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(activityLogFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public boolean useMedicine(String productName, String studentName, int quantity)
            throws SQLException, IOException {

        String selectSql = "SELECT quantity FROM MEDICINES WHERE name = ?";
        String updateSql = "UPDATE MEDICINES SET quantity = ? WHERE name = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            int currentQty;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, productName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    currentQty = rs.getInt("quantity");
                }
            }
            if (currentQty < quantity) return false;

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, currentQty - quantity);
                ps.setString(2, productName);
                ps.executeUpdate();
            }
        }
        logActivity("Student " + studentName + " Used " + quantity + "x " + productName);
        return true;
    }

    public boolean nameExists(String name) throws SQLException {
        String sql = "SELECT 1 FROM MEDICINES WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Medicine findByName(String name) throws SQLException {
        String sql = "SELECT name, exp_date, quantity FROM MEDICINES WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
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
