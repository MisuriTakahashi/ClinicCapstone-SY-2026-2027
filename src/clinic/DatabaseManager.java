/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 * @author PC
 */
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:h2:./data/Clinic_db;CIPHER=AES;";
    private static final String USER = "admin";
    private static final String FILE_ENCRYPTION_KEY = "TebanPo123";
    private static final String USER_PASSWORD = "admin123";
    private static final String FULL_PASSWORD = FILE_ENCRYPTION_KEY + " " + USER_PASSWORD;
    private static Connection sharedConnection;

    public static Connection getConnection() throws SQLException {
        if (sharedConnection == null || sharedConnection.isClosed()) {
            sharedConnection = DriverManager.getConnection(DB_URL, USER, FULL_PASSWORD);
        }
        return sharedConnection;
    }

    public static void initializeDatabase() {
        new File("./data").mkdirs(); // make sure the folder exists before H2 writes

        String createProductsTable = "CREATE TABLE IF NOT EXISTS PRODUCTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "category VARCHAR(100) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL);";
        String createAccountsTable = "CREATE TABLE IF NOT EXISTS ACCOUNTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(50) NOT NULL);";
        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS MEDICINES ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "exp_date VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL);";
        String createVisitsTable = "CREATE TABLE IF NOT EXISTS VISITS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "grade_section VARCHAR(100) NOT NULL, "
                + "lrn VARCHAR(50) NOT NULL, "
                + "reason VARCHAR(500), "
                + "med_used VARCHAR(255), "
                + "meds_qty INT NOT NULL, "
                + "check_in_time VARCHAR(50) NOT NULL, "
                + "status VARCHAR(50) NOT NULL, "
                + "guardian_name VARCHAR(255), "
                + "guardian_phone VARCHAR(50));";

        File seedFile = new File("./data/seed_data.sql");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountsTable);
            stmt.execute(createProductsTable);
            stmt.execute(createMedicinesTable);
            stmt.execute(createVisitsTable);

            if (seedFile.exists()) {
                try {
                    stmt.execute("RUNSCRIPT FROM './data/seed_data.sql'");
                    System.out.println("✅ Seed data imported from seed_data.sql!");
                } catch (SQLException e) {
                    System.out.println("ℹ️ Seed script skipped (data already present).");
                }
            }
            System.out.println("H2 Encrypted Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exportData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SCRIPT TO './data/seed_data.sql'");
            System.out.println("✅ Data exported to ./data/seed_data.sql successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void testDatabaseConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connection successful!");
        } catch (SQLException e) {
            System.err.println("❌ Database Test Failed!");
            e.printStackTrace();
        }
    }

}
