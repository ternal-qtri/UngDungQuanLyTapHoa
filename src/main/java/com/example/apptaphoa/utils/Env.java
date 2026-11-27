package com.example.apptaphoa.utils;

/** Đọc bí mật từ biến môi trường, không lưu trong source. */
public final class Env {
    private Env() {}

    public static String require(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Thiếu biến môi trường " + name);
        }
        return value;
    }
}
