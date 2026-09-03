/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 * Represents an authenticated application account.
 */
public class AccountSystem {

    public static final String ROLE_HEAD_ADMIN = "HEAD_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private String name;
    private String password;
    private String role;
    private boolean protectedAccount;

    public AccountSystem(String name, String password, String role) {
        this(name, password, role, false);
    }

    public AccountSystem(String name, String password, String role, boolean protectedAccount) {
        this.name = name;
        this.password = password;
        this.role = normalizeRole(role);
        this.protectedAccount = protectedAccount;
    }

    public String GetName() {
        return name;
    }

    public String GetPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isProtectedAccount() {
        return protectedAccount;
    }

    public void SetName(String name) {
        this.name = name;
    }

    public void SetPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = normalizeRole(role);
    }

    public void setProtectedAccount(boolean protectedAccount) {
        this.protectedAccount = protectedAccount;
    }

    public boolean isHeadAdmin() {
        return ROLE_HEAD_ADMIN.equals(normalizeRole(role));
    }

    /**
     * Head Admin inherits all Admin permissions.
     */
    public boolean isAdmin() {
        return isHeadAdmin() || ROLE_ADMIN.equals(normalizeRole(role));
    }

    public boolean isNormalUser() {
        return ROLE_USER.equals(normalizeRole(role));
    }

    public boolean canAccessAdminPanel() {
        return isAdmin();
    }

    /**
     * Centralized account-management deletion rule.
     * The database/service layer still performs the authoritative check using
     * the current database records before executing a DELETE statement.
     */
    public boolean canDeleteAccount(AccountSystem target) {
        if (target == null || name == null || target.name == null) {
            return false;
        }

        if (name.equalsIgnoreCase(target.name)) {
            return false;
        }

        if (target.isProtectedAccount() || target.isHeadAdmin()) {
            return false;
        }

        if (isHeadAdmin()) {
            return target.isAdmin() || target.isNormalUser();
        }

        return isAdmin() && target.isNormalUser();
    }

    public String getNormalizedRole() {
        return normalizeRole(role);
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            return null;
        }

        String value = role.trim();

        if (value.equalsIgnoreCase(ROLE_HEAD_ADMIN)
                || value.equalsIgnoreCase("Head Admin")
                || value.equalsIgnoreCase("HeadAdmin")) {
            return ROLE_HEAD_ADMIN;
        }

        if (value.equalsIgnoreCase(ROLE_ADMIN)
                || value.equalsIgnoreCase("Admin")) {
            return ROLE_ADMIN;
        }

        if (value.equalsIgnoreCase(ROLE_USER)
                || value.equalsIgnoreCase("User")
                || value.equalsIgnoreCase("Normal User")) {
            return ROLE_USER;
        }

        return null;
    }
}