package security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String passwordToHash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is required");
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password.toCharArray(), salt, ITERATIONS);
        return "pbkdf2-sha256$" + ITERATIONS + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(hash);
    }

    public boolean verify(String password, String encoded) {
        if (password == null || encoded == null) {
            return false;
        }
        if (encoded.startsWith("pbkdf2-sha256$")) {
            try {
                String[] parts = encoded.split("\\$", -1);
                if (parts.length != 4) {
                    return false;
                }
                int iterations = Integer.parseInt(parts[1]);
                if (iterations < 1 || iterations > 2_000_000) {
                    return false;
                }
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expected = Base64.getDecoder().decode(parts[3]);
                return MessageDigest.isEqual(expected, derive(password.toCharArray(), salt, iterations));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return MessageDigest.isEqual(legacyMd2(password), encoded.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }

    public boolean needsUpgrade(String encoded) {
        return encoded == null || !encoded.startsWith("pbkdf2-sha256$");
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing is unavailable", e);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }

    private static byte[] legacyMd2(String password) {
        try {
            return java.util.HexFormat.of().formatHex(
                    MessageDigest.getInstance("MD2").digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                    .getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
