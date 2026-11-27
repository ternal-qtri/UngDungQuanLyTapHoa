package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.repository.NhanVienRepository;
import com.example.apptaphoa.security.OtpChallenge;
import com.example.apptaphoa.security.PasswordHasher;
import com.example.apptaphoa.security.Session;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/** Đăng nhập, đổi mật khẩu và đặt lại mật khẩu bằng OTP. */
public class AuthService {
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** Gửi mã OTP tới email người dùng. Tách riêng để dễ thay thế khi kiểm thử. */
    public interface OtpSender {
        void send(String toEmail, String code);
    }

    private final NhanVienRepository users;
    private final OtpSender otpSender;
    private final Clock clock;
    private final Map<String, OtpChallenge> pendingOtp = new HashMap<>();

    public AuthService(NhanVienRepository users, OtpSender otpSender, Clock clock) {
        this.users = users;
        this.otpSender = otpSender;
        this.clock = clock;
    }

    public NhanVien login(String username, String password) {
        String name = Checks.requireText(username, "Tên đăng nhập");
        if (password == null || password.isEmpty()) {
            throw new BusinessException("Mật khẩu không được để trống");
        }
        NhanVien user = users.findByUsername(name)
                .filter(u -> PasswordHasher.verify(password, u.getMatKhau()))
                .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng"));
        Session.start(user);
        return user;
    }

    public void logout() {
        Session.end();
    }

    /** Đổi mật khẩu cho người đang đăng nhập. */
    public void changePassword(String oldPassword, String newPassword) {
        NhanVien current = Session.requireLogin();
        if (!PasswordHasher.verify(oldPassword, current.getMatKhau())) {
            throw new BusinessException("Mật khẩu hiện tại không đúng");
        }
        checkNewPassword(newPassword);
        users.updatePasswordHash(current.getTenDangNhap(), PasswordHasher.hash(newPassword));
    }

    /** Bước 1 quên mật khẩu: kiểm tra tên đăng nhập + email rồi gửi OTP. */
    public void requestOtp(String username, String email) {
        String name = Checks.requireText(username, "Tên đăng nhập");
        String mail = Checks.requireText(email, "Email");
        NhanVien user = users.findByUsernameAndEmail(name, mail)
                .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc email không đúng"));
        String code = OtpChallenge.newCode();
        pendingOtp.put(name, OtpChallenge.of(code, clock.instant()));
        otpSender.send(user.getEmail(), code);
    }

    /** Bước 2 quên mật khẩu: xác nhận OTP rồi đặt mật khẩu mới. */
    public void resetPassword(String username, String code, String newPassword) {
        String name = Checks.requireText(username, "Tên đăng nhập");
        OtpChallenge challenge = pendingOtp.get(name);
        if (challenge == null) {
            throw new BusinessException("Bạn chưa yêu cầu mã OTP");
        }
        if (!challenge.verify(code, clock.instant())) {
            if (challenge.isExhausted()) pendingOtp.remove(name);
            throw new BusinessException("Mã OTP không đúng hoặc đã hết hạn");
        }
        checkNewPassword(newPassword);
        pendingOtp.remove(name);
        users.updatePasswordHash(name, PasswordHasher.hash(newPassword));
    }

    private static void checkNewPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException("Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
    }
}
