package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.exception.InsufficientStockException;
import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class SalesJdbcRepository implements SalesRepository {

    @Override
    public int saveInvoice(HoaDon invoice, List<ChiTietHoaDon> lines) {
        try (Connection c = Db.open()) {
            c.setAutoCommit(false);
            try {
                int maHD = insertInvoice(c, invoice);
                for (ChiTietHoaDon line : lines) {
                    line.setMaHD(maHD);
                    insertLine(c, line);
                    decreaseStock(c, line);
                }
                c.commit();
                return maHD;
            } catch (SQLException e) {
                rollback(c);
                throw new DataAccessException("Không lưu được hóa đơn", e);
            } catch (RuntimeException e) {
                rollback(c);
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không kết nối được cơ sở dữ liệu", e);
        }
    }

    private static int insertInvoice(Connection c, HoaDon invoice) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO HoaDon (ThoiGian, TongTien, HinhThucTT, MaNV) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(invoice.getThoiGian().getTime()));
            ps.setDouble(2, invoice.getTongTien());
            ps.setString(3, invoice.getHinhThucTT());
            ps.setString(4, invoice.getMaNV());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static void insertLine(Connection c, ChiTietHoaDon line) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO ChiTietHoaDon (MaHD, MaSanPham, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, line.getMaHD());
            ps.setInt(2, line.getMaSanPham());
            ps.setInt(3, line.getSoLuong());
            ps.setDouble(4, line.getDonGia());
            ps.setDouble(5, line.getThanhTien());
            ps.executeUpdate();
        }
    }

    /** Điều kiện SoLuongTonKho >= ? giữ việc kiểm tra và trừ kho trong một câu lệnh, tránh race condition. */
    private static void decreaseStock(Connection c, ChiTietHoaDon line) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE SanPham SET SoLuongTonKho = SoLuongTonKho - ? WHERE MaSanPham = ? AND SoLuongTonKho >= ?")) {
            ps.setInt(1, line.getSoLuong());
            ps.setInt(2, line.getMaSanPham());
            ps.setInt(3, line.getSoLuong());
            if (ps.executeUpdate() != 1) {
                throw new InsufficientStockException(line.getTenSanPham());
            }
        }
    }

    private static void rollback(Connection c) {
        try {
            c.rollback();
        } catch (SQLException ignored) {
            // Lỗi rollback không che lỗi gốc; kết nối sẽ bị đóng bởi try-with-resources.
        }
    }
}
