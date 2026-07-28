package com.silverwing.dorothy.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Should successfully encode a raw password")
    void testEncodePassword() {
        String rawPassword = "securePassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        // BCrypt encoded passwords usually start with $2a$, $2b$ or $2y$
        assertTrue(encodedPassword.startsWith("$2"));
    }

    @Test
    @DisplayName("Should return true when raw password matches the encoded password")
    void testPasswordMatch_Success() {
        String rawPassword = "mySecretPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should return false when raw password does not match the encoded password")
    void testPasswordMatch_Failure() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should produce different hashes for the same password due to random salt")
    void testEncodingIsRandomized() {
        String rawPassword = "constantPassword";

        String firstEncoding = passwordEncoder.encode(rawPassword);
        String secondEncoding = passwordEncoder.encode(rawPassword);

        assertNotEquals(firstEncoding, secondEncoding);

        // Both should still be valid matches for the same raw password
        assertTrue(passwordEncoder.matches(rawPassword, firstEncoding));
        assertTrue(passwordEncoder.matches(rawPassword, secondEncoding));
    }

    @Test
    @DisplayName("Should produce different hashes for the same password due to random salt")
    void testEncoding() {
        String rawPassword = "7972";

        String firstEncoding = passwordEncoder.encode(rawPassword);
        System.out.println(rawPassword+" :"+firstEncoding);
    }

}