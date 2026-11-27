package com.example.apptaphoa.security;

import com.example.apptaphoa.exception.AccessDeniedException;
import com.example.apptaphoa.model.NhanVien;

import java.util.Optional;

/** Người dùng đang đăng nhập. Thay thế AuthUtil. Mọi kiểm tra quyền nằm ở đây. */
public final class Session {
    private static NhanVien current;

    private Session() {}

    public static synchronized void start(NhanVien user) {
        current = user;
    }

    public static synchronized void end() {
        current = null;
    }

    /** Người đang đăng nhập, hoặc rỗng nếu chưa đăng nhập. */
    public static synchronized Optional<NhanVien> current() {
        return Optional.ofNullable(current);
    }

    /** Trả về người dùng hiện tại, hoặc ném AccessDeniedException nếu chưa đăng nhập. */
    public static synchronized NhanVien requireLogin() {
        if (current == null) {
            throw new AccessDeniedException("Bạn chưa đăng nhập");
        }
        return current;
    }

    /** Ném AccessDeniedException nếu người dùng không có đúng vai trò này. */
    public static NhanVien requireRole(Role role) {
        NhanVien user = requireLogin();
        if (Role.fromDbValue(user.getVaiTro()) != role) {
            throw new AccessDeniedException("Chức năng này chỉ dành cho " + role.dbValue());
        }
        return user;
    }
}
