package clinic;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import clinic.DatabaseManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author PC
 */

public class MigrateCsvData {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        migrateAccounts();
        migrateMedicines();
        migrateVisits();
        System.out.println("✅ Migration finished.");
    }

    private static String[] parseCsvLine(String line) {
        String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].replace("\"", "").trim();
        }
        return data;
    }

    private static void migrateAccounts() {
        File file = new File("accounts.csv");
        if (!file.exists()) {
            System.out.println("accounts.csv not found, skipping.");
            return;
        }

        AccountData dao = new AccountData();
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = parseCsvLine(line);
                if (data.length < 3) continue;

                String name = data[0], password = data[1], role = data[2];
                if (!dao.nameExists(name)) {
                    dao.createAccount(name, password, role);
                    count++;
                }
            }
            System.out.println("Accounts migrated: " + count);
        } catch (IOException | java.sql.SQLException e) {
            System.err.println("Failed to migrate accounts: " + e.getMessage());
        }
    }

    private static void migrateMedicines() {
        File file = new File("products.csv");
        if (!file.exists()) {
            System.out.println("products.csv not found, skipping.");
            return;
        }

        MedicineData dao = new MedicineData("inventory_activity.log");
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = parseCsvLine(line);
                if (data.length < 3) continue;

                String name = data[0], expDate = data[1];
                int quantity = Integer.parseInt(data[2]);

                if (!dao.nameExists(name)) {
                    dao.addItem(name, expDate, quantity);
                    count++;
                }
            }
            System.out.println("Medicines migrated: " + count);
        } catch (IOException | java.sql.SQLException e) {
            System.err.println("Failed to migrate medicines: " + e.getMessage());
        }
    }

    // Inserts directly (not via VisitDao.checkIn) so the original status
    // and check-in time from the CSV are preserved, not overwritten.
    private static void migrateVisits() {
        File file = new File("visits.csv");
        if (!file.exists()) {
            System.out.println("visits.csv not found, skipping.");
            return;
        }

        String sql = "INSERT INTO VISITS(name, grade_section, lrn, reason, med_used, meds_qty, "
                + "check_in_time, status, guardian_name, guardian_phone) VALUES(?,?,?,?,?,?,?,?,?,?)";

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = parseCsvLine(line);
                if (data.length < 10) continue;

                ps.setString(1, data[0]);              // name
                ps.setString(2, data[1]);              // gradeSection
                ps.setString(3, data[2]);              // lrn
                ps.setString(4, data[3]);              // reason
                ps.setString(5, data[4]);              // medUsed
                ps.setInt(6, Integer.parseInt(data[5])); // medsQty
                ps.setString(7, data[6]);              // checkInTime
                ps.setString(8, data[7]);              // status
                ps.setString(9, data[8]);              // guardianName
                ps.setString(10, data[9]);             // guardianPhone
                ps.executeUpdate();
                count++;
            }
            System.out.println("Visits migrated: " + count);
        } catch (IOException | java.sql.SQLException e) {
            System.err.println("Failed to migrate visits: " + e.getMessage());
        }
    }
}
