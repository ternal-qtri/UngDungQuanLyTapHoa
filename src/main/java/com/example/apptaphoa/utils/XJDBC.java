package com.example.apptaphoa.utils;

import java.sql.*;

public class XJDBC {
    private static Connection connection;

    public static boolean isReady() {
        try { return (connection != null && !connection.isClosed()); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static Connection openConnection() {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        // Sửa lại db, user, pass cho đúng môi trường của bạn
        String dburl  = "jdbc:sqlserver://localhost:1433;databaseName=AppTapHoa;user=sa;password=Tri12092005;encrypt=false;";

        try {
            if (!isReady()) {
                Class.forName(driver);
                connection = DriverManager.getConnection(dburl);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static void closeConnection() {
        try { if (isReady()) connection.close(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    private static PreparedStatement getStmt(String sql, Object... values) throws SQLException {
        Connection conn = openConnection();
        PreparedStatement stmt = sql.trim().startsWith("{") ? conn.prepareCall(sql) : conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) stmt.setObject(i + 1, values[i]);
        return stmt;
    }

    public static int executeUpdate(String sql, Object... values) {
        try (PreparedStatement stmt = getStmt(sql, values)) {
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ResultSet executeQuery(String sql, Object... values) {
        try {
            PreparedStatement stmt = getStmt(sql, values);
            return stmt.executeQuery();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static void main(String[] args) {
        try {
            Connection conn = openConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Kết nối cơ sở dữ liệu thành công!");
                closeConnection();
                System.out.println("Đã đóng kết nối.");
            } else {
                System.out.println("Kết nối cơ sở dữ liệu thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra trong quá trình kết nối:");
            e.printStackTrace();
        }
    }
}
