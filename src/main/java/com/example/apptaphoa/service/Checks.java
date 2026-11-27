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

    /** Làm tròn tiền đến đồng xu (2 chữ số thập phân). */
    static double money(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
