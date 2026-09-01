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
    
     private static void validatePassword(char[] password) {

        if (password == null
                || password.length == 0) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }
    }
     
       /**
     * Resets another account's password. This is an administrative
     * password RESET — the Head Admin does not need to know the old
     * password.
     *
     * Only a Head Admin may call this, and only for an account other
     * than their own.
     *
     * Head Admin accounts are NOT blocked as reset targets — this is
     * intentional, so a locked-out Head Admin can be recovered by a
     * fellow Head Admin. Instead of a hard block, a reset where the
     * target is also a Head Admin is logged under a distinct,
     * clearly-flagged activity type (HEAD_ADMIN_PASSWORD_RESET) so it
     * stands out during an audit. The extra "are you sure" friction for
     * this case lives in AdminPanel's UI layer — this method still logs
     * the sensitive action even if called directly.
     *
     * The actor's role is reloaded from H2 (not taken from the
     * AccountSystem object passed in), so this cannot be bypassed by
     * calling the method directly with a forged role.
     *
     * Returns true if the password was updated, false if the target
     * account no longer exists by the time the update runs.
     */
    public boolean resetPassword(
            AccountSystem actor,
            String targetName,
            char[] newPassword) throws SQLException {

        AccountSystem currentActor =
                requireHeadAdminActor(actor);

        if (targetName == null
                || targetName.trim().isEmpty()) {

            throw new SecurityException(
                    "Invalid password reset request."
            );
        }

        String cleanTargetName = targetName.trim();

        if (currentActor.GetName()
                .equalsIgnoreCase(cleanTargetName)) {

            throw new SecurityException(
                    "You cannot reset your own password using this function."
            );
        }

        validatePassword(newPassword);

        String passwordHash =
                PasswordHasher.hashPassword(newPassword);

        String sql =
                "UPDATE ACCOUNTS SET password = ? "
                + "WHERE id = ?";

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps =
                         conn.prepareStatement(sql)) {

                // Re-read the target inside the transaction so the
                // check runs against the current database state, not
                // a stale in-memory copy.
                AccountSystem freshTarget =
                        findByName(cleanTargetName);

                if (freshTarget == null) {

                    conn.rollback();
                    return false;
                }

                if (currentActor.GetName()
                        .equalsIgnoreCase(
                                freshTarget.GetName())) {

                    conn.rollback();

                    throw new SecurityException(
                            "You cannot reset your own password using this function."
                    );
                }

                int targetId =
                        findId(conn, freshTarget.GetName());

                if (targetId <= 0) {

                    conn.rollback();
                    return false;
                }

                ps.setString(1, passwordHash);
                ps.setInt(2, targetId);

                int rows = ps.executeUpdate();

                if (rows == 0) {

                    conn.rollback();
                    return false;
                }

                // Flag Head-Admin-to-Head-Admin resets distinctly in
                // the activity log so they're easy to spot in an audit.
                boolean targetIsHeadAdmin =
                        freshTarget.isHeadAdmin();

                String action = targetIsHeadAdmin
                        ? "HEAD_ADMIN_PASSWORD_RESET"
                        : "PASSWORD_RESET";

                String details = targetIsHeadAdmin
                        ? "SECURITY: Head Admin account \""
                          + freshTarget.GetName()
                          + "\" had its password reset by fellow "
                          + "Head Admin \""
                          + currentActor.GetName()
                          + "\"."
                        : "Password for account \""
                          + freshTarget.GetName()
                          + "\" was updated by \""
                          + currentActor.GetName()
                          + "\".";

                ActivityLogData.log(
                        conn,
                        action,
                        details,
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
    
    /**
     * Same shape as requireCurrentActor(), but requires HEAD_ADMIN
     * specifically rather than any admin role. Used only by
     * resetPassword() so that an ADMIN or USER caller gets the exact
     * "Only Head Admin accounts can reset passwords." message even if
     * they call this method directly instead of going through the UI.
     */
    private AccountSystem requireHeadAdminActor(
            AccountSystem actor)
            throws SQLException {

        if (actor == null
                || actor.GetName() == null
                || actor.GetName().trim().isEmpty()) {

            throw new SecurityException(
                    "You must be logged in to perform "
                    + "this operation."
            );
        }

        AccountSystem currentActor =
                findByName(actor.GetName());

        if (currentActor == null) {

            throw new SecurityException(
                    "The logged-in account no longer exists."
            );
        }

        if (actor.GetPassword() == null
                || !actor.GetPassword()
                        .equals(
                                currentActor.GetPassword()
                        )) {

            throw new SecurityException(
                    "The authenticated account context is invalid."
            );
        }

        if (!currentActor.isHeadAdmin()) {

            throw new SecurityException(
                    "Only Head Admin accounts can reset passwords."
            );
        }

        return currentActor;
    }
    /**
     * Lets the currently logged-in account change its OWN password,
     * after verifying the CURRENT password against the hash stored in H2.
     *
     * This is intentionally a separate method from resetPassword():
     * resetPassword() is an administrative reset that does NOT require
     * knowing the old password and is restricted to Head Admin acting on
     * OTHER accounts. changeOwnPassword() is the opposite shape — it only
     * ever touches the caller's own row, and always demands proof of the
     * current password first.
     *
     * Returns true if the password was changed, false if the account no
     * longer exists. Throws SecurityException if the current password is
     * wrong.
     */
    
    public boolean changeOwnPassword(
            AccountSystem actor,
            char[] currentPassword,
            char[] newPassword) throws SQLException {

        if (actor == null
                || actor.GetName() == null
                || actor.GetName().trim().isEmpty()) {

            throw new SecurityException(
                    "You must be logged in to change your password."
            );
        }

        if (newPassword == null || newPassword.length == 0) {

            throw new IllegalArgumentException(
                    "New password is required."
            );
        }

        String sql =
                "UPDATE ACCOUNTS SET password = ? "
                + "WHERE id = ?";

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps =
                         conn.prepareStatement(sql)) {

                // Re-read the caller's own record inside the transaction so
                // the current-password check runs against the live database
                // state, not a stale in-memory copy.
                AccountSystem freshActor =
                        findByName(actor.GetName());

                if (freshActor == null) {
                    conn.rollback();
                    return false;
                }

                if (!PasswordHasher.verifyPassword(
                        currentPassword,
                        freshActor.GetPassword())) {

                    conn.rollback();

                    throw new SecurityException(
                            "The current password you entered is incorrect."
                    );
                }

                int actorId =
                        findId(conn, freshActor.GetName());

                if (actorId <= 0) {
                    conn.rollback();
                    return false;
                }

                String passwordHash =
                        PasswordHasher.hashPassword(newPassword);

                ps.setString(1, passwordHash);
                ps.setInt(2, actorId);

                int rows = ps.executeUpdate();

                if (rows == 0) {
                    conn.rollback();
                    return false;
                }

                ActivityLogData.log(
                        conn,
                        "PASSWORD_CHANGE",
                        "Account \"" + freshActor.GetName()
                        + "\" changed their own password.",
                        freshActor.GetName()
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

}