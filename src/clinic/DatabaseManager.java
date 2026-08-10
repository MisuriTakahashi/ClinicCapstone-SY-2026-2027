/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 *
 * @author PC
 */
import java.io.File;
import java.sql.*;





public class DatabaseManager {

    private static final String DB_URL = "jdbc:h2:./data/Clinic_db;CIPHER=AES;";
    private static final String USER = "admin";
    
    private static final String FILE_ENCRYPTION_KEY = "TebanPo123";
    private static final String USER_PASSWORD = "admin123";
    private static final String FULL_PASSWORD = FILE_ENCRYPTION_KEY + " " + USER_PASSWORD;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, FULL_PASSWORD);
    }

    /**
     * Initializes tables and imports seed data automatically if present.
     */
    public static void initializeDatabase() {
        String createProductsTable = "CREATE TABLE IF NOT EXISTS PRODUCTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "category VARCHAR(100) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL"
                + ");";
        
        String createAccountsTable = "CREATE TABLE IF NOT EXISTS ACCOUNTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(50) NOT NULL"
                + ");";

        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS MEDICINES ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "exp_date VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL"
                + ");";

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
                + "guardian_phone VARCHAR(50)"
                + ");";

        File seedFile = new File("./data/seed_data.sql");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Create table structure
            stmt.execute(createAccountsTable);
            stmt.execute(createProductsTable);
            stmt.execute(createMedicinesTable);
            stmt.execute(createVisitsTable);
            
            // 2. Automatically import data on new clones if seed_data.sql exists
            if (seedFile.exists()) {
                try {
                    stmt.execute("RUNSCRIPT FROM './data/seed_data.sql'");
                    System.out.println("✅ Seed data imported successfully from seed_data.sql!");
                } catch (SQLException e) {
                    // Ignores errors if tables/data were already populated
                    System.out.println("ℹ️ Seed data script executed (existing records preserved).");
                }
            }

            System.out.println("H2 Encrypted Database initialized successfully.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call this on your main PC to export your current database to seed_data.sql.
     */
    public static void exportData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SCRIPT TO './data/seed_data.sql'");
            System.out.println("✅ Data exported to ./data/seed_data.sql successfully!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void testDatabaseConnection() {
        String insertSQL = "INSERT INTO products(name, category, price, stock) VALUES(?, ?, ?, ?)";
        String selectSQL = "SELECT * FROM products WHERE name = ?";

        try (Connection conn = getConnection()) {
            System.out.println("✅ Connection successful!");

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, "Amoxicillin");
                pstmt.setString(2, "Antibiotics");
                pstmt.setDouble(3, 15.50);
                pstmt.setInt(4, 100);
                pstmt.executeUpdate();
                System.out.println("✅ Test product inserted successfully!");
            } catch (SQLException e) {
                System.out.println("ℹ️ Test product already exists, fetching data...");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, "Amoxicillin");
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    System.out.println("\n--- Database Record Found ---");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Category: " + rs.getString("category"));
                    System.out.println("Price: $" + rs.getDouble("price"));
                    System.out.println("Stock: " + rs.getInt("stock"));
                    System.out.println("-----------------------------\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Test Failed!");
            e.printStackTrace();
        }
    }
}
