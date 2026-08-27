/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 * Central configuration for the default application Head Admin.
 *
 * Before deployment, change the fallback username/password below, or provide
 * the environment variables:
 *
 * CLINIC_DEFAULT_HEAD_ADMIN_USERNAME
 * CLINIC_DEFAULT_HEAD_ADMIN_PASSWORD
 */
public final class SecurityConfig {

    private SecurityConfig() {
    }

    public static final String DEFAULT_HEAD_ADMIN_USERNAME =
            readSetting(
                    "CLINIC_DEFAULT_HEAD_ADMIN_USERNAME",
                    "headadmin"
            );

    public static final String DEFAULT_HEAD_ADMIN_PASSWORD =
            readSetting(
                    "CLINIC_DEFAULT_HEAD_ADMIN_PASSWORD",
                    "headadmin123"
            );

    private static String readSetting(
            String environmentVariable,
            String fallback) {

        String value =
                System.getenv(environmentVariable);

        if (value == null || value.isBlank()) {
            value =
                    System.getProperty(environmentVariable);
        }

        return (value == null || value.isBlank())
                ? fallback
                : value;
    }
}