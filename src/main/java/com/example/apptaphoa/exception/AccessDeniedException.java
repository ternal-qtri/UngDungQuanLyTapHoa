package com.example.apptaphoa.exception;

/** Người dùng chưa đăng nhập hoặc không đủ quyền cho thao tác. */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
