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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesJdbcRepository implements SalesRepository {

    @Override
    public List<HoaDon> findInvoices(LocalDate from, LocalDate to, String maNV) {
        String sql = "SELECT MaHD, ThoiGian, TongTien, HinhThucTT, MaNV FROM HoaDon "
                + "WHERE CAST(ThoiGian AS date) BETWEEN ? AND ? "
                + (maNV == null ? "" : "AND MaNV = ? ")
                + "ORDER BY ThoiGian DESC";
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            if (maNV != null) ps.setString(3, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                List<HoaDon> list = new ArrayList<>();
                while (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getInt("MaHD"));
                    hd.setThoiGian(rs.getTimestamp("ThoiGian"));
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setHinhThucTT(rs.getString("HinhThucTT"));
                    hd.setMaNV(rs.getString("MaNV"));
                    list.add(hd);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được lịch sử hóa đơn", e);
        }
    }

    @Override
    public List<ChiTietHoaDon> findLines(int maHD) {
        String sql = "SELECT ct.MaHD, ct.MaSanPham, sp.TenSanPham, ct.SoLuong, ct.DonGia, ct.ThanhTien "
                + "FROM ChiTietHoaDon ct JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham WHERE ct.MaHD = ?";
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                List<ChiTietHoaDon> list = new ArrayList<>();
                while (rs.next()) {
                    ChiTietHoaDon line = new ChiTietHoaDon();
                    line.setMaHD(rs.getInt("MaHD"));
                    line.setMaSanPham(rs.getInt("MaSanPham"));
                    line.setTenSanPham(rs.getString("TenSanPham"));
                    line.setSoLuong(rs.getInt("SoLuong"));
                    line.setDonGia(rs.getDouble("DonGia"));
                    line.setThanhTien(rs.getDouble("ThanhTien"));
                    list.add(line);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được chi tiết hóa đơn", e);
        }
    }

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
