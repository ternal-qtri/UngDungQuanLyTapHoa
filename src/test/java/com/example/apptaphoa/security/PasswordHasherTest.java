package com.example.apptaphoa.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void verifiesCorrectPassword() {
        String stored = PasswordHasher.hash("Quanly@123");
        assertTrue(PasswordHasher.verify("Quanly@123", stored));
    }

    @Test
    void rejectsWrongPassword() {
        String stored = PasswordHasher.hash("Quanly@123");
        assertFalse(PasswordHasher.verify("quanly@123", stored));
    }

    @Test
    void samePasswordGetsDifferentSalt() {
        assertNotEquals(PasswordHasher.hash("abc"), PasswordHasher.hash("abc"));
    }

    @Test
    void hashHasExpectedFormat() {
        String stored = PasswordHasher.hash("abc");
        assertTrue(stored.startsWith("pbkdf2$310000$"));
        assertEquals(4, stored.split("\\$").length);
    }

    @Test
    void malformedStoredValueIsRejectedWithoutException() {
        assertFalse(PasswordHasher.verify("abc", "plaintext-password"));
        assertFalse(PasswordHasher.verify("abc", "pbkdf2$x$y$z"));
        assertFalse(PasswordHasher.verify("abc", null));
    }
}
