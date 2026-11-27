package com.example.apptaphoa.repository;

import com.example.apptaphoa.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Mở kết nối tới SQL Server. Người gọi đóng bằng try-with-resources. */
public final class Db {
    private Db() {}

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(AppConfig.dbUrl(), AppConfig.dbUser(), AppConfig.dbPassword());
    }
}
