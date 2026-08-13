/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.SQLException;

/**
 *
 * @author PC
 */
public class SessionManager {
     private static final File SESSION_FILE = new File("./data/session.properties");

    /** Called when the window X is clicked - remembers who is currently logged in. */
    public static void saveSession(AccountSystem account) {
        if (account == null) return;

        Properties props = new Properties();
        props.setProperty("username", account.GetName());
        props.setProperty("role", account.getRole());

        try {
            File parent = SESSION_FILE.getParentFile();
            if (parent != null) parent.mkdirs();

            try (FileOutputStream out = new FileOutputStream(SESSION_FILE)) {
                props.store(out, "Clinic app remembered session - no password is stored here");
            }
        } catch (IOException ex) {
            // If this fails, the user will just be asked to log in again next launch.
            System.err.println("Could not save session: " + ex.getMessage());
        }
    }

    /**
     * Called on app startup. Returns the previously logged-in account if a
     * session was remembered AND that account still exists in the database,
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
