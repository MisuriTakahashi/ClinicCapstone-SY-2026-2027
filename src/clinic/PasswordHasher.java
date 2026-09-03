/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing utility using PBKDF2-HMAC-SHA256.
 *
 * Stored format:
 * PBKDF2-SHA256$iterations$base64Salt$base64Hash
 */
public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "PBKDF2-SHA256";

    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        return hashPassword(password.toCharArray());
    }

    public static String hashPassword(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);

        byte[] hash = derive(password, salt, ITERATIONS);

        return PREFIX + "$"
                + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        return verifyPassword(password.toCharArray(), storedHash);
    }

    public static boolean verifyPassword(char[] password, String storedHash) {
        if (password == null
                || storedHash == null
                || !isHash(storedHash)) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$", -1);

            if (parts.length != 4 || !PREFIX.equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            byte[] salt =
                    Base64.getDecoder().decode(parts[2]);

            byte[] expected =
                    Base64.getDecoder().decode(parts[3]);

            byte[] actual =
                    derive(password, salt, iterations);

            return MessageDigest.isEqual(expected, actual);

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean isHash(String value) {
        return value != null
                && value.startsWith(PREFIX + "$");
    }

    private static byte[] derive(
            char[] password,
            byte[] salt,
            int iterations) {

        if (iterations <= 0) {
            throw new IllegalArgumentException(
                    "Invalid PBKDF2 iteration count."
            );
        }

        try {
            KeySpec spec =
                    new PBEKeySpec(
                            password,
                            salt,
                            iterations,
                            HASH_LENGTH_BITS
                    );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                    "PBKDF2 password hashing is unavailable.",
                    ex
            );
        }
    }
}
