package clinic;

/**
 * In-memory context for the account authenticated in this application run.
 * Authorization-sensitive database operations still re-check the actor from
 * the database; this class is only the UI's single source of session state.
 */
public final class UserSession {
    private static volatile AccountSystem currentUser;

    private UserSession() {
    }

    public static void start(AccountSystem account) {
        currentUser = account;
    }

    public static AccountSystem getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
