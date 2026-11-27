package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.security.OtpChallenge;
import com.example.apptaphoa.security.PasswordHasher;
import com.example.apptaphoa.security.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

    /** Đồng hồ có thể chỉnh để kiểm tra OTP hết hạn. */
    static final class MutableClock extends Clock {
        Instant now = T0;
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    private Fakes.Users users;
    private final List<String> sentCodes = new ArrayList<>();
    private MutableClock clock;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        users = new Fakes.Users();
        users.insert(Fakes.user("QL001", "quanly", "Quản lý", PasswordHasher.hash("Quanly@123")));
        clock = new MutableClock();
        sentCodes.clear();
        auth = new AuthService(users, (email, code) -> sentCodes.add(code), clock,
                new AuditService(new Fakes.NhatKy()));
    }

    @AfterEach
    void tearDown() {
        Session.end();
    }

    @Test
    void loginWithCorrectPasswordStartsSession() {
        NhanVien user = auth.login("quanly", "Quanly@123");

        assertEquals("QL001", user.getMaNV());
        assertEquals("QL001", Session.requireLogin().getMaNV());
    }

    @Test
    void wrongPasswordIsRejectedWithGenericMessage() {
        BusinessException ex = assertThrows(BusinessException.class, () -> auth.login("quanly", "sai"));
        assertEquals("Tên đăng nhập hoặc mật khẩu không đúng", ex.getMessage());
    }

    @Test
    void unknownUserGetsSameMessageAsWrongPassword() {
        BusinessException ex = assertThrows(BusinessException.class, () -> auth.login("khongco", "abc"));
        assertEquals("Tên đăng nhập hoặc mật khẩu không đúng", ex.getMessage());
    }

    @Test
    void otpResetsPasswordAndCannotBeReused() {
        auth.requestOtp("quanly", "quanly@example.com");
        String code = sentCodes.get(0);

        auth.resetPassword("quanly", code, "MatKhauMoi1");

        assertTrue(PasswordHasher.verify("MatKhauMoi1", users.findByUsername("quanly").get().getMatKhau()));
        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", code, "MatKhauKhac1"));
    }

    @Test
    void wrongOtpIsRejected() {
        auth.requestOtp("quanly", "quanly@example.com");
        String wrong = sentCodes.get(0).equals("000000") ? "111111" : "000000";

        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", wrong, "MatKhauMoi1"));
    }

    @Test
    void expiredOtpIsRejected() {
        auth.requestOtp("quanly", "quanly@example.com");
        String code = sentCodes.get(0);
        clock.now = T0.plus(OtpChallenge.TTL).plusSeconds(1);

        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", code, "MatKhauMoi1"));
    }

    @Test
    void otpLocksAfterMaxWrongAttempts() {
        auth.requestOtp("quanly", "quanly@example.com");
        String code = sentCodes.get(0);
        String wrong = code.equals("000000") ? "111111" : "000000";

        for (int i = 0; i < OtpChallenge.MAX_ATTEMPTS; i++) {
            assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", wrong, "MatKhauMoi1"));
        }
        // Sau 5 lần sai, mã đúng cũng không còn dùng được.
        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", code, "MatKhauMoi1"));
    }

    @Test
    void resetWithoutRequestIsRejected() {
        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", "123456", "MatKhauMoi1"));
    }

    @Test
    void requestOtpWithWrongEmailIsRejected() {
        assertThrows(BusinessException.class, () -> auth.requestOtp("quanly", "sai@example.com"));
        assertTrue(sentCodes.isEmpty());
    }

    @Test
    void newPasswordMustBeLongEnough() {
        auth.requestOtp("quanly", "quanly@example.com");
        assertThrows(BusinessException.class, () -> auth.resetPassword("quanly", sentCodes.get(0), "abc"));
    }

    @Test
    void changePasswordRequiresCurrentPassword() {
        auth.login("quanly", "Quanly@123");

        assertThrows(BusinessException.class, () -> auth.changePassword("sai", "MatKhauMoi1"));
        auth.changePassword("Quanly@123", "MatKhauMoi1");
        assertTrue(PasswordHasher.verify("MatKhauMoi1", users.findByUsername("quanly").get().getMatKhau()));
    }
}
