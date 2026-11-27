package com.example.apptaphoa.security;

/** Vai trò người dùng. dbValue là giá trị lưu trong cột NhanVien.VaiTro. */
public enum Role {
    MANAGER("Quản lý"),
    STAFF("Nhân viên");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static Role fromDbValue(String value) {
        for (Role role : values()) {
            if (role.dbValue.equals(value)) return role;
        }
        throw new IllegalArgumentException("Vai trò không hợp lệ: " + value);
    }
}
