package clinic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * H2-backed account data/service layer.
 *
 * Authorization is enforced here, not only by the Swing UI. The actor's
 * current role is reloaded from H2 before account-management operations are
 * performed, so a caller cannot bypass permissions by constructing an
 * AccountSystem object with a forged role.
 */
public class AccountData {

    public ArrayList<AccountSystem> loadAll()
            throws SQLException {

        ArrayList<AccountSystem> accounts =
                new ArrayList<>();

        String sql =
                "SELECT name, password, role, is_protected "
                + "FROM ACCOUNTS ORDER BY name";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                accounts.add(
                        fromResultSet(rs)
                );
            }
        }

        return accounts;
    }

    public boolean nameExists(String name)
            throws SQLException {

        if (name == null
                || name.trim().isEmpty()) {

            return false;
        }

        String sql =
                "SELECT 1 FROM ACCOUNTS "
                + "WHERE UPPER(name) = UPPER(?)";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());

            try (ResultSet rs =
                         ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    /**
     * Creates an account using the role hierarchy:
     *
     * HEAD_ADMIN -> ADMIN or USER
     * ADMIN      -> USER only
     * USER       -> nothing
     */
    public void createAccount(
            AccountSystem actor,
            String name,
            String password,
            String role) throws SQLException {

        AccountSystem currentActor =
                requireCurrentActor(actor);

        String normalizedRole =
                AccountSystem.normalizeRole(role);

        if (normalizedRole == null
                || AccountSystem.ROLE_HEAD_ADMIN
                        .equals(normalizedRole)) {

            throw new SecurityException(
                    "Only ADMIN or USER accounts can "
                    + "be created from account management."
            );
        }

        if (AccountSystem.ROLE_ADMIN
                .equals(normalizedRole)
                && !currentActor.isHeadAdmin()) {

            throw new SecurityException(
                    "Only the Head Admin can create "
                    + "an Admin account."
            );
        }

        if (AccountSystem.ROLE_USER
                .equals(normalizedRole)
                && !currentActor.isAdmin()) {

            throw new SecurityException(
                    "You do not have permission to "
                    + "create a User account."
            );
        }

        String cleanName =
                validateAccountName(name);

        validatePassword(password);

        if (nameExists(cleanName)) {

            throw new SQLException(
                    "An account with this name already exists."
            );
        }

        String passwordHash =
                PasswordHasher.hashPassword(password);

        String sql =
                "INSERT INTO ACCOUNTS("
                + "name, password, role, is_protected"
                + ") VALUES(?, ?, ?, FALSE)";

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps =
                         conn.prepareStatement(sql)) {

                ps.setString(1, cleanName);
                ps.setString(2, passwordHash);
                ps.setString(3, normalizedRole);

                ps.executeUpdate();

                ActivityLogData.log(
                        conn,
                        "CREATE_ACCOUNT",
                        "Created "
                        + normalizedRole
                        + " account: "
                        + cleanName,
                        currentActor.GetName()
                );

                conn.commit();

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
     * Authenticates by username first, then verifies the password hash.
     * Passwords are never compared in SQL.
     */
    public AccountSystem authenticate(
            String name,
            String password) throws SQLException {

        if (name == null
                || name.trim().isEmpty()
                || password == null) {

            return null;
        }

        String sql =
                "SELECT name, password, role, is_protected "
                + "FROM ACCOUNTS "
                + "WHERE UPPER(name) = UPPER(?)";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                String storedHash =
                        rs.getString("password");

                if (!PasswordHasher.verifyPassword(
                        password,
                        storedHash)) {

                    return null;
                }

                return fromResultSet(rs);
            }
        }
    }

    /**
     * Deletes an account only when the actor and target permissions are valid.
     * Both actor and target are read again from H2 before DELETE is executed.
     */
    public boolean deleteAccount(
            AccountSystem actor,
            String targetName) throws SQLException {

        AccountSystem currentActor =
                requireCurrentActor(actor);

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

        if (!currentActor.canDeleteAccount(target)) {

            if (currentActor.GetName()
                    .equalsIgnoreCase(
                            target.GetName())) {

                throw new SecurityException(
                        "You cannot delete your own account."
                );
            }

            if (target.isHeadAdmin()
                    || target.isProtectedAccount()) {

                throw new SecurityException(
                        "The protected Head Admin account "
                        + "cannot be deleted."
                );
            }

            if (!currentActor.isAdmin()) {

                throw new SecurityException(
                        "You do not have permission "
                        + "to delete accounts."
                );
            }

            throw new SecurityException(
                    "Admins can only delete Normal User "
                    + "accounts. The Head Admin can delete "
                    + "Admin and User accounts."
            );
        }

        String sql =
                "DELETE FROM ACCOUNTS "
                + "WHERE id = ? "
                + "AND is_protected = FALSE";

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps =
                         conn.prepareStatement(sql)) {

                AccountSystem freshTarget =
                        findByName(
                                target.GetName()
                        );

                if (freshTarget == null) {

                    conn.rollback();
                    return false;
                }

                if (!currentActor.canDeleteAccount(
                        freshTarget)) {

                    conn.rollback();

                    throw new SecurityException(
                            "The account can no longer "
                            + "be deleted under the "
                            + "current permissions."
                    );
                }

                int targetId =
                        findId(
                                conn,
                                freshTarget.GetName()
                        );

                if (targetId <= 0) {

                    conn.rollback();
                    return false;
                }

                ps.setInt(1, targetId);

                int rows =
                        ps.executeUpdate();

                if (rows == 0) {

                    conn.rollback();
                    return false;
                }

                ActivityLogData.log(
                        conn,
                        "DELETE_ACCOUNT",
                        "Deleted "
                        + freshTarget.getNormalizedRole()
                        + " account: "
                        + freshTarget.GetName(),
                        currentActor.GetName()
                );

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

    public AccountSystem findByName(String name)
            throws SQLException {

        if (name == null
                || name.trim().isEmpty()) {

            return null;
        }

        String sql =
                "SELECT name, password, role, is_protected "
                + "FROM ACCOUNTS "
                + "WHERE UPPER(name) = UPPER(?)";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());

            try (ResultSet rs =
                         ps.executeQuery()) {

                return rs.next()
                        ? fromResultSet(rs)
                        : null;
            }
        }
    }

    private AccountSystem requireCurrentActor(
            AccountSystem actor)
            throws SQLException {

        if (actor == null
                || actor.GetName() == null
                || actor.GetName().trim().isEmpty()) {

            throw new SecurityException(
                    "You must be logged in to perform "
                    + "this account operation."
            );
        }

        AccountSystem currentActor =
                findByName(actor.GetName());

        if (currentActor == null) {

            throw new SecurityException(
                    "The logged-in account no longer exists."
            );
        }

        /*
         * The AccountSystem supplied by the authenticated UI contains
         * the password hash loaded from H2. Require it to match the
         * current DB record as an additional guard against fabricated
         * AccountSystem objects with a forged username/role.
         */
        if (actor.GetPassword() == null
                || !actor.GetPassword()
                        .equals(
                                currentActor.GetPassword()
                        )) {

            throw new SecurityException(
                    "The authenticated account context is invalid."
            );
        }

        if (!currentActor.isAdmin()) {

            throw new SecurityException(
                    "You do not have administrative "
                    + "permission for this operation."
            );
        }

        return currentActor;
    }

    private static String validateAccountName(
            String name) {

        if (name == null) {

            throw new IllegalArgumentException(
                    "Account name is required."
            );
        }

        String cleanName = name.trim();

        if (cleanName.isEmpty()) {

            throw new IllegalArgumentException(
                    "Account name is required."
            );
        }

        if (cleanName.length() > 255) {

            throw new IllegalArgumentException(
                    "Account name must be 255 characters "
                    + "or fewer."
            );
        }

        return cleanName;
    }

    private static void validatePassword(
            String password) {

        if (password == null
                || password.isEmpty()) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }
    }

    private static AccountSystem fromResultSet(
            ResultSet rs) throws SQLException {

        return new AccountSystem(
                rs.getString("name"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getBoolean("is_protected")
        );
    }

    private static int findId(
            Connection conn,
            String name) throws SQLException {

        String sql =
                "SELECT id FROM ACCOUNTS "
                + "WHERE UPPER(name) = UPPER(?)";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs =
                         ps.executeQuery()) {

                return rs.next()
                        ? rs.getInt(1)
                        : -1;
            }
        }
    }
    
        /**
     * First-run only. Creates one or more protected HEAD_ADMIN accounts
     * in a single transaction. There is no logged-in actor yet, so this
     * intentionally does NOT go through requireCurrentActor(). Instead,
     * it re-checks — inside the same transaction that performs the
     * inserts — that the ACCOUNTS table is still completely empty. If
     * it isn't, nothing is inserted and a SecurityException is thrown.
     */
    public void createInitialHeadAdmins(
            java.util.List<PendingHeadAdmin> pendingHeadAdmins)
            throws SQLException {

        if (pendingHeadAdmins == null
                || pendingHeadAdmins.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one Head Admin is required."
            );
        }

        String insert =
                "INSERT INTO ACCOUNTS("
                + "name, password, role, is_protected"
                + ") VALUES(?, ?, ?, TRUE)";

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                try (PreparedStatement countPs =
                             conn.prepareStatement(
                                     "SELECT COUNT(*) FROM ACCOUNTS"
                             );
                     ResultSet countRs =
                             countPs.executeQuery()) {

                    countRs.next();

                    if (countRs.getInt(1) > 0) {

                        throw new SecurityException(
                                "First-Run Head Admin setup can "
                                + "only run on a database with "
                                + "no existing accounts."
                        );
                    }
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(insert)) {

                    for (PendingHeadAdmin admin
                            : pendingHeadAdmins) {

                        ps.setString(1, admin.name());
                        ps.setString(2, admin.passwordHash());
                        ps.setString(
                                3,
                                AccountSystem.ROLE_HEAD_ADMIN
                        );

                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                for (PendingHeadAdmin admin
                        : pendingHeadAdmins) {

                    ActivityLogData.log(
                            conn,
                            "CREATE_ACCOUNT",
                            "Created protected "
                            + AccountSystem.ROLE_HEAD_ADMIN
                            + " account: "
                            + admin.name(),
                            "SYSTEM_SETUP"
                    );
                }

                conn.commit();

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
     * One validated, already-hashed Head Admin waiting to be inserted
     * during First-Run Setup. Holds only the password hash — never
     * plaintext — and nothing is written to H2 until all of them are
     * collected and createInitialHeadAdmins() is called.
     */
    public record PendingHeadAdmin(
            String name,
            String passwordHash) {
    }
}