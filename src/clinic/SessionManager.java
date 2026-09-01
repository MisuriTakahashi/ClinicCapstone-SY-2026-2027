package clinic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.SQLException;

public class SessionManager {
    private static final File SESSION_FILE = new File("./data/session.properties");

    // ==================================================
    // SESSION TIMEOUT OPTIONS
    // ONLY ONE OPTION SHOULD BE UNCOMMENTED
    // ==================================================

    // 5 MINUTES
    // public static final long SESSION_TIMEOUT = 5L * 60L * 1000L;

    // 30 MINUTES - CURRENT ACTIVE SETTING
    public static final long SESSION_TIMEOUT = 30L * 60L * 1000L;

    // 1 HOUR
    // public static final long SESSION_TIMEOUT = 60L * 60L * 1000L;

    /** Called on window X close - remembers who is logged in AND when they were last active. */
    public static void saveSession(AccountSystem account, long lastActivityTime) {
        if (account == null) return;

        Properties props = new Properties();
        props.setProperty("username", account.GetName());
        props.setProperty("role", account.getRole());
        props.setProperty("lastActivity", String.valueOf(lastActivityTime));

        try {
            File parent = SESSION_FILE.getParentFile();
            if (parent != null) parent.mkdirs();

            try (FileOutputStream out = new FileOutputStream(SESSION_FILE)) {
                props.store(out, "Clinic app remembered session - no password is stored here");
            }
        } catch (IOException ex) {
            System.err.println("Could not save session: " + ex.getMessage());
        }
    }

    /**
     * Called on app startup. Returns the previously logged-in account if the
     * remembered session is still valid (account exists AND not timed out),
     * or null if there is nothing to restore.
     */
    public static AccountSystem loadSession() {
        if (!SESSION_FILE.exists()) return null;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(SESSION_FILE)) {
            props.load(in);
        } catch (IOException ex) {
            return null;
        }

        String username = props.getProperty("username");
        if (username == null || username.isEmpty()) return null;

        String lastActivityRaw = props.getProperty("lastActivity");
        if (lastActivityRaw == null || lastActivityRaw.isEmpty()) {
            clearSession(); // old session file with no timestamp - can't trust it
            return null;
        }

        long lastActivity;
        try {
            lastActivity = Long.parseLong(lastActivityRaw);
        } catch (NumberFormatException ex) {
            clearSession(); // corrupted timestamp
            return null;
        }

        long elapsed = System.currentTimeMillis() - lastActivity;
        if (elapsed >= SESSION_TIMEOUT) {
            clearSession();
            return null;
        }

        try {
            AccountSystem account = new AccountData().findByName(username);
            if (account == null) {
                clearSession(); // account no longer exists - don't restore it
                return null;
            }
            return account;
        } catch (SQLException ex) {
            return null; // safest fallback is the login screen
        }
    }

    /** Called when the user intentionally presses Logout - forgets the remembered session. */
    public static void clearSession() {
        if (SESSION_FILE.exists()) {
            SESSION_FILE.delete();
        }
    }
}