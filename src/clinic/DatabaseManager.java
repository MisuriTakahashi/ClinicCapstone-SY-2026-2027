package clinic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central H2 database configuration and initialization.
 *
 * The existing encrypted H2 database is preserved. Initialization only adds
 * missing structures/columns and never drops application tables.
 */
public class DatabaseManager {

    private static final String DB_URL =
            "jdbc:h2:./data/Clinic_db;CIPHER=AES;";

    private static final String USER = "admin";

    private static final String FILE_ENCRYPTION_KEY =
            "TebanPo123";

    private static final String USER_PASSWORD =
            "admin123";

    private static final String FULL_PASSWORD =
            FILE_ENCRYPTION_KEY
            + " "
            + USER_PASSWORD;

    private DatabaseManager() {
    }

    /**
     * Returns a fresh JDBC connection.
     * Callers should close it with try-with-resources.
     */
    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                DB_URL,
                USER,
                FULL_PASSWORD
        );
    }

    public static void initializeDatabase() {

        new File("./data").mkdirs();

        String createProductsTable =
                "CREATE TABLE IF NOT EXISTS PRODUCTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "category VARCHAR(100) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL)";

        String createAccountsTable =
                "CREATE TABLE IF NOT EXISTS ACCOUNTS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(50) NOT NULL, "
                + "is_protected BOOLEAN NOT NULL DEFAULT FALSE)";

        String createMedicinesTable =
                "CREATE TABLE IF NOT EXISTS MEDICINES ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL, "
                + "exp_date VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL)";

        String createVisitsTable =
                "CREATE TABLE IF NOT EXISTS VISITS ("
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
                + "guardian_phone VARCHAR(50))";

        String createActivityLogTable =
                "CREATE TABLE IF NOT EXISTS ACTIVITY_LOG ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "timestamp TIMESTAMP NOT NULL "
                + "DEFAULT CURRENT_TIMESTAMP, "
                + "action VARCHAR(100) NOT NULL, "
                + "details VARCHAR(1000), "
                + "actor VARCHAR(255) NOT NULL)";

        try (Connection conn =
                     getConnection();
             Statement stmt =
                     conn.createStatement()) {

            stmt.execute(createAccountsTable);
            stmt.execute(createProductsTable);
            stmt.execute(createMedicinesTable);
            stmt.execute(createVisitsTable);
            stmt.execute(createActivityLogTable);

            /*
             * Safe schema migrations for existing databases.
             */
            stmt.execute(
                    "ALTER TABLE ACCOUNTS "
                    + "ADD COLUMN IF NOT EXISTS "
                    + "is_protected BOOLEAN "
                    + "NOT NULL DEFAULT FALSE"
            );

            stmt.execute(
                    "ALTER TABLE VISITS "
                    + "ADD COLUMN IF NOT EXISTS "
                    + "archived BOOLEAN "
                    + "NOT NULL DEFAULT FALSE"
            );

            migrateAccountRolesAndPasswords(conn);

            ensureDefaultHeadAdmin(conn);

            /*
             * Import existing plaintext inventory history once.
             * No new activity is written to this file.
             */
            File legacyActivityLog =
                    new File("./inventory_activity.log");

            try {

                int imported =
                        ActivityLogData.importLegacyFile(
                                legacyActivityLog
                        );

                if (imported > 0) {

                    System.out.println(
                            "Imported "
                            + imported
                            + " legacy activity record(s) "
                            + "into H2."
                    );
                }

            } catch (Exception ex) {

                System.err.println(
                        "Legacy activity log import skipped: "
                        + ex.getMessage()
                );
            }

            System.out.println(
                    "H2 Encrypted Database initialized successfully."
            );

        } catch (SQLException e) {

            System.err.println(
                    "H2 database initialization failed."
            );

            e.printStackTrace();

            throw new IllegalStateException(
                    "Could not initialize the encrypted "
                    + "H2 database.",
                    e
            );
        }
    }

    /**
     * Converts legacy role labels and plaintext account passwords in-place.
     */
    private static void migrateAccountRolesAndPasswords(
            Connection conn) throws SQLException {

        String select =
                "SELECT id, role, password "
                + "FROM ACCOUNTS";

        String update =
                "UPDATE ACCOUNTS "
                + "SET role = ?, password = ? "
                + "WHERE id = ?";

        String updateRoleOnly =
                "UPDATE ACCOUNTS "
                + "SET role = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps =
                     conn.prepareStatement(select);
             ResultSet rs =
                     ps.executeQuery()) {

            java.util.ArrayList<AccountMigrationRow> rows =
                    new java.util.ArrayList<>();

            while (rs.next()) {

                rows.add(
                        new AccountMigrationRow(
                                rs.getInt("id"),
                                rs.getString("role"),
                                rs.getString("password")
                        )
                );
            }

            for (AccountMigrationRow row : rows) {

                String normalizedRole =
                        AccountSystem.normalizeRole(
                                row.role
                        );

                if (normalizedRole == null) {

                    normalizedRole =
                            AccountSystem.ROLE_USER;
                }

                boolean passwordNeedsHash =
                        !PasswordHasher.isHash(
                                row.password
                        );

                if (passwordNeedsHash) {

                    String plaintext =
                            row.password == null
                            ? ""
                            : row.password;

                    String hash =
                            PasswordHasher.hashPassword(
                                    plaintext
                            );

                    try (PreparedStatement updatePs =
                                 conn.prepareStatement(
                                         update
                                 )) {

                        updatePs.setString(
                                1,
                                normalizedRole
                        );

                        updatePs.setString(
                                2,
                                hash
                        );

                        updatePs.setInt(
                                3,
                                row.id
                        );

                        updatePs.executeUpdate();
                    }

                } else if (
                        !normalizedRole.equals(
                                row.role
                        )) {

                    try (PreparedStatement updatePs =
                                 conn.prepareStatement(
                                         updateRoleOnly
                                 )) {

                        updatePs.setString(
                                1,
                                normalizedRole
                        );

                        updatePs.setInt(
                                2,
                                row.id
                        );

                        updatePs.executeUpdate();
                    }
                }
            }
        }
    }

    private static void ensureDefaultHeadAdmin(
            Connection conn) throws SQLException {

        int headAdminCount = 0;

        String countSql =
                "SELECT COUNT(*) "
                + "FROM ACCOUNTS "
                + "WHERE role = ?";

        try (PreparedStatement ps =
                     conn.prepareStatement(countSql)) {

            ps.setString(
                    1,
                    AccountSystem.ROLE_HEAD_ADMIN
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {
                    headAdminCount =
                            rs.getInt(1);
                }
            }
        }

        if (headAdminCount > 0) {

            try (PreparedStatement ps =
                         conn.prepareStatement(
                                 "UPDATE ACCOUNTS "
                                 + "SET is_protected = TRUE "
                                 + "WHERE role = ?"
                         )) {

                ps.setString(
                        1,
                        AccountSystem.ROLE_HEAD_ADMIN
                );

                ps.executeUpdate();
            }

            return;
        }

        String username =
                SecurityConfig
                        .DEFAULT_HEAD_ADMIN_USERNAME
                        .trim();

        if (username.isEmpty()) {

            throw new SQLException(
                    "Default Head Admin username "
                    + "cannot be empty."
            );
        }

        String existingSql =
                "SELECT id, role "
                + "FROM ACCOUNTS "
                + "WHERE UPPER(name) = UPPER(?)";

        try (PreparedStatement ps =
                     conn.prepareStatement(existingSql)) {

            ps.setString(1, username);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    throw new SQLException(
                            "The configured default "
                            + "Head Admin username '"
                            + username
                            + "' already belongs to "
                            + "a non-Head-Admin account. "
                            + "Change the configured username "
                            + "before starting the application."
                    );
                }
            }
        }

        if (SecurityConfig.DEFAULT_HEAD_ADMIN_PASSWORD
                == null
                || SecurityConfig.DEFAULT_HEAD_ADMIN_PASSWORD
                        .isEmpty()) {

            throw new SQLException(
                    "Default Head Admin password "
                    + "cannot be empty."
            );
        }

        String insert =
                "INSERT INTO ACCOUNTS("
                + "name, password, role, is_protected"
                + ") VALUES(?, ?, ?, TRUE)";

        try (PreparedStatement ps =
                     conn.prepareStatement(insert)) {

            ps.setString(
                    1,
                    username
            );

            ps.setString(
                    2,
                    PasswordHasher.hashPassword(
                            SecurityConfig
                                    .DEFAULT_HEAD_ADMIN_PASSWORD
                    )
            );

            ps.setString(
                    3,
                    AccountSystem.ROLE_HEAD_ADMIN
            );

            ps.executeUpdate();
        }

        ActivityLogData.log(
                conn,
                "CREATE_ACCOUNT",
                "Created protected default "
                + AccountSystem.ROLE_HEAD_ADMIN
                + " account: "
                + username,
                "SYSTEM"
        );

        System.out.println(
                "Default protected Head Admin created: "
                + username
        );
    }

    private record AccountMigrationRow(
            int id,
            String role,
            String password) {
    }

    /**
     * Keeps the existing manual database-export feature.
     * It is not used for account storage or account migration.
     */
    public static void exportData() {

        try (Connection conn =
                     getConnection();
             Statement stmt =
                     conn.createStatement()) {

            stmt.execute(
                    "SCRIPT TO './data/seed_data.sql'"
            );

            System.out.println(
                    "Data exported to "
                    + "./data/seed_data.sql successfully!"
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void testDatabaseConnection() {

        try (Connection conn =
                     getConnection()) {

            System.out.println(
                    "Connection successful!"
            );

        } catch (SQLException e) {

            System.err.println(
                    "Database Test Failed!"
            );

            e.printStackTrace();
        }
    }
}