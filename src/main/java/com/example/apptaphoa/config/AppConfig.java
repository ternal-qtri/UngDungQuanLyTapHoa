package com.example.apptaphoa.config;

/** Cấu hình ứng dụng, đọc từ biến môi trường. Không lưu bí mật trong source. */
public final class AppConfig {
    private static final String DEFAULT_DB_URL =
            "jdbc:sqlserver://localhost:1433;databaseName=AppTapHoa;encrypt=true;trustServerCertificate=true;";

    private AppConfig() {}

    public static String dbUrl() {
        String value = System.getenv("TAPHOA_DB_URL");
        return (value == null || value.isBlank()) ? DEFAULT_DB_URL : value;
    }

    public static String dbUser() {
        return require("TAPHOA_DB_USER");
    }

    public static String dbPassword() {
        return require("TAPHOA_DB_PASSWORD");
    }

    private static String require(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Thiếu biến môi trường " + name);
        }
        return value;
    }
}
