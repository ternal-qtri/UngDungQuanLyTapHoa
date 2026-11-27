package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;

/** Kiểm tra đầu vào dùng chung cho các service. Ném BusinessException với thông báo tiếng Việt. */
final class Checks {
    private Checks() {}

    static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(fieldName + " không được để trống");
        }
        return value.trim();
    }

    static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new BusinessException(fieldName + " phải lớn hơn 0");
        }
    }

    static void requireNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new BusinessException(fieldName + " không được âm");
        }
    }

    /** Số điện thoại tùy chọn: để trống thì trả về null, nhập sai định dạng thì ném lỗi. */
    static String optionalPhone(String value, String fieldName) {
        if (value == null || value.isBlank()) return null;
        String phone = value.trim();
        if (!phone.matches("\\+?[0-9 ]{8,15}")) {
            throw new BusinessException(fieldName + " chỉ gồm chữ số (có thể có dấu +), dài 8 đến 15 ký tự");
        }
        return phone;
    }

    /** Email tùy chọn: để trống thì trả về null, nhập sai định dạng thì ném lỗi. */
    static String optionalEmail(String value, String fieldName) {
        if (value == null || value.isBlank()) return null;
        String email = value.trim();
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new BusinessException(fieldName + " không đúng định dạng");
        }
        return email;
    }

    /** Làm tròn tiền đến đồng xu (2 chữ số thập phân). */
    static double money(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
