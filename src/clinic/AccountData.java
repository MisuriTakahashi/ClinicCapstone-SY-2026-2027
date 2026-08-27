/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class AccountData {

    // =========================
    // LOAD ALL ACCOUNTS
    // =========================

    public ArrayList<AccountSystem> loadAll() throws SQLException {

        ArrayList<AccountSystem> accounts = new ArrayList<>();

        String sql = "SELECT name, password, role FROM ACCOUNTS";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                accounts.add(
                    new AccountSystem(
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("role")
                    )
                );
            }
        }

        return accounts;
    }

    // =========================
    // CHECK IF NAME EXISTS
    // =========================

    public boolean nameExists(String name) throws SQLException {

        String sql =
            "SELECT 1 FROM ACCOUNTS WHERE UPPER(name) = UPPER(?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================
    // CREATE ACCOUNT
    // =========================

    /**
     * Creates an account while checking the permissions
     * of the currently logged-in account.
     *
     * HEAD_ADMIN:
     *      Can create ADMIN and USER.
     *
     * ADMIN:
     *      Can create USER only.
     *
     * USER:
     *      Cannot create accounts.
     */
    public void createAccount(
            AccountSystem actor,
            String name,
            String password,
            String role) throws SQLException {

        if (actor == null) {
            throw new SecurityException(
                "You must be logged in to create an account."
            );
        }

        String normalizedRole = normalizeRole(role);

        if (normalizedRole == null) {
            throw new SecurityException(
                "Invalid account role."
            );
        }

        // Nobody can create another Head Admin
        if ("HEAD_ADMIN".equals(normalizedRole)) {
            throw new SecurityException(
                "A Head Admin cannot be created from account management."
            );
        }

        // Only Head Admin can create Admin
        if ("ADMIN".equals(normalizedRole)
                && !actor.isHeadAdmin()) {

            throw new SecurityException(
                "Only the Head Admin can create an Admin account."
            );
        }

        // Admin and Head Admin can create Users
        if ("USER".equals(normalizedRole)
                && !actor.isAdmin()) {

            throw new SecurityException(
                "You do not have permission to create a User account."
            );
        }

        String sql =
            "INSERT INTO ACCOUNTS(name, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, normalizedRole);

            ps.executeUpdate();
        }
    }

    // =========================
    // OLD CSV MIGRATION SUPPORT
    // =========================
    //
    // LEAVE THIS FOR NOW.
    //
    // We are NOT removing CSV yet because this
    // phase is ONLY the access-level upgrade.
    //
    // We will remove it in the next phase.

    private void createAccount(
            String name,
            String password,
            String role) throws SQLException {

        String sql =
            "INSERT INTO ACCOUNTS(name, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, normalizeRole(role));

            ps.executeUpdate();
        }
    }

    // =========================
    // LOGIN
    // =========================

    public AccountSystem authenticate(
            String name,
            String password) throws SQLException {

        String sql =
            "SELECT name, password, role " +
            "FROM ACCOUNTS " +
            "WHERE name = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new AccountSystem(
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }

                return null;
            }
        }
    }

    // =========================
    // DELETE ACCOUNT
    // =========================

    /**
     * Deletes an account according to the role hierarchy.
     *
     * HEAD_ADMIN:
     *      Can delete ADMIN
     *      Can delete USER
     *      Cannot delete HEAD_ADMIN
     *      Cannot delete itself
     *
     * ADMIN:
     *      Can delete USER
     *      Cannot delete ADMIN
     *      Cannot delete HEAD_ADMIN
     *      Cannot delete itself
     *
     * USER:
     *      Cannot delete anyone
     */
    public boolean deleteAccount(
            AccountSystem actor,
            String targetName) throws SQLException {

        if (actor == null) {
            throw new SecurityException(
                "You must be logged in to delete an account."
            );
        }

        if (targetName == null
                || targetName.trim().isEmpty()) {

            throw new SecurityException(
                "Invalid account deletion request."
            );
        }

        AccountSystem target =
            findByName(targetName.trim());

        if (target == null) {
            return false;
        }

        // =========================
        // CANNOT DELETE YOURSELF
        // =========================

        if (actor.GetName().equalsIgnoreCase(target.GetName())) {

            throw new SecurityException(
                "You cannot delete your own account."
            );
        }

        // =========================
        // HEAD ADMIN PROTECTION
        // =========================

        if (target.isHeadAdmin()) {

            throw new SecurityException(
                "The Head Admin account cannot be deleted."
            );
        }

        // =========================
        // NORMAL USER
        // =========================

        if (!actor.isAdmin()) {

            throw new SecurityException(
                "You do not have permission to delete accounts."
            );
        }

        // =========================
        // ADMIN
        // =========================

        if (actor.isAdmin()
                && !actor.isHeadAdmin()) {

            if (!target.isNormalUser()) {

                throw new SecurityException(
                    "Admins can only delete Normal User accounts."
                );
            }
        }

        // =========================
        // HEAD ADMIN
        // =========================
        //
        // Head Admin can delete:
        // ADMIN
        // USER
        //
        // Head Admin cannot delete:
        // HEAD_ADMIN
        //
        // Already protected above.

        String sql =
            "DELETE FROM ACCOUNTS WHERE name = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, target.GetName());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // CSV MIGRATION
    // =========================
    //
    // DO NOT REMOVE YET.
    //
    // We will remove this separately
    // after the access system is confirmed working.

    public int migrateFromCsv(String csvPath)
            throws SQLException, IOException {

        File file = new File(csvPath);

        if (!file.exists()) {
            return 0;
        }

        int migratedCount = 0;

        try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts =
                    line.split("\",\"");

                if (parts.length < 3) {
                    continue;
                }

                String name =
                    parts[0].replaceAll("^\"|\"$", "");

                String password =
                    parts[1].replaceAll("^\"|\"$", "");

                String role =
                    parts[2].replaceAll("^\"|\"$", "");

                if (!nameExists(name)) {

                    createAccount(
                        name,
                        password,
                        role
                    );

                    migratedCount++;
                }
            }
        }

        return migratedCount;
    }

    // =========================
    // FIND ACCOUNT
    // =========================

    public AccountSystem findByName(String name)
            throws SQLException {

        String sql =
            "SELECT name, password, role " +
            "FROM ACCOUNTS " +
            "WHERE name = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new AccountSystem(
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }

                return null;
            }
        }
    }

    // =========================
    // ROLE NORMALIZATION
    // =========================

    private String normalizeRole(String role) {

        if (role == null) {
            return null;
        }

        if (role.equalsIgnoreCase("HEAD_ADMIN")
                || role.equalsIgnoreCase("Head Admin")
                || role.equalsIgnoreCase("HeadAdmin")) {

            return "HEAD_ADMIN";
        }

        if (role.equalsIgnoreCase("ADMIN")
                || role.equalsIgnoreCase("Admin")) {

            return "ADMIN";
        }

        if (role.equalsIgnoreCase("USER")
                || role.equalsIgnoreCase("User")
                || role.equalsIgnoreCase("Normal User")) {

            return "USER";
        }

        return null;
    }
}