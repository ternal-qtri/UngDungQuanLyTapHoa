package com.example.apptaphoa.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

/**
 * Một yêu cầu OTP đặt lại mật khẩu.
 * Hết hạn sau TTL, tối đa MAX_ATTEMPTS lần nhập sai, chỉ dùng được một lần.
 * Chỉ giữ băm của mã, không giữ mã thô.
 */
public final class OtpChallenge {
    public static final Duration TTL = Duration.ofMinutes(5);
    public static final int MAX_ATTEMPTS = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final String codeHash;
    private final Instant expiresAt;
    private int attemptsLeft = MAX_ATTEMPTS;
    private boolean used;

    private OtpChallenge(String codeHash, Instant expiresAt) {
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
    }

    /** Mã 6 chữ số ngẫu nhiên. Mã này cần gửi cho người dùng, không lưu lại. */
    public static String newCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public static OtpChallenge of(String code, Instant now) {
        return new OtpChallenge(PasswordHasher.hash(code), now.plus(TTL));
    }

    public boolean verify(String code, Instant now) {
        if (used || attemptsLeft <= 0 || now.isAfter(expiresAt)) return false;
        if (PasswordHasher.verify(code, codeHash)) {
            used = true;
            return true;
        }
        attemptsLeft--;
        return false;
    }

    public boolean isExhausted() {
        return used || attemptsLeft <= 0;
    }
}
