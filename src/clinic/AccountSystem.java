/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 *
 * @author PC
 */
public class AccountSystem {

    private String name;
    private String password;
    private String role;

    public AccountSystem(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // =========================
    // GETTERS
    // =========================

    public String GetName() {
        return name;
    }

    public String GetPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // =========================
    // SETTERS
    // =========================

    public void SetName(String name) {
        this.name = name;
    }

    public void SetPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =========================
    // ROLE CHECKS
    // =========================

    /**
     * Returns true only if this account is a Head Admin.
     */
    public boolean isHeadAdmin() {
        if (role == null) {
            return false;
        }

        return role.equalsIgnoreCase("HEAD_ADMIN")
                || role.equalsIgnoreCase("Head Admin")
                || role.equalsIgnoreCase("HeadAdmin");
    }

    /**
     * Returns true for both:
     * HEAD_ADMIN
     * ADMIN
     *
     * A Head Admin inherits Admin permissions.
     */
    public boolean isAdmin() {
        if (role == null) {
            return false;
        }

        return isHeadAdmin()
                || role.equalsIgnoreCase("ADMIN")
                || role.equalsIgnoreCase("Admin");
    }

    /**
     * Returns true for a normal User account.
     */
    public boolean isNormalUser() {
        if (role == null) {
            return false;
        }

        return role.equalsIgnoreCase("USER")
                || role.equalsIgnoreCase("User")
                || role.equalsIgnoreCase("Normal User");
    }

    /**
     * HEAD_ADMIN and ADMIN can access the Admin Panel.
     * USER cannot.
     */
    public boolean canAccessAdminPanel() {
        return isAdmin();
    }

    /**
     * Converts old role names into the new standard role names.
     */
    public String getNormalizedRole() {

        if (isHeadAdmin()) {
            return "HEAD_ADMIN";
        }

        if (role != null
                && (role.equalsIgnoreCase("ADMIN")
                || role.equalsIgnoreCase("Admin"))) {
            return "ADMIN";
        }

        if (isNormalUser()) {
            return "USER";
        }

        return role;
    }

    public String toCsvLine() {
        return "\"" + name + "\",\"" + password + "\",\"" + role + "\"";
    }
}
