package com.example.apptaphoa.exception;

/** Vi phạm quy tắc nghiệp vụ. Thông báo là tiếng Việt, có thể hiển thị trực tiếp cho người dùng. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
