package com.example.apptaphoa.exception;

/** Lỗi truy cập cơ sở dữ liệu. Bọc SQLException để tầng trên không phụ thuộc JDBC. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
