package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.model.ChiTietPhieuNhap;
import com.example.apptaphoa.model.PhieuNhapHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class PurchaseJdbcRepository implements PurchaseRepository {

    @Override
    public int savePurchase(PhieuNhapHang receipt, List<ChiTietPhieuNhap> lines, double debtAdded) {
        try (Connection c = Db.open()) {
            c.setAutoCommit(false);
            try {
                int maPN = insertReceipt(c, receipt);
                for (ChiTietPhieuNhap line : lines) {
                    line.setMaPN(maPN);
                    insertLine(c, line);
                    increaseStock(c, line);
                }
                if (debtAdded > 0) {
                    addDebt(c, receipt.getMaNCC(), debtAdded);
                }
                c.commit();
                return maPN;
            } catch (SQLException e) {
                rollback(c);
                throw new DataAccessException("Không lưu được phiếu nhập", e);
            } catch (RuntimeException e) {
                rollback(c);
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không kết nối được cơ sở dữ liệu", e);
        }
    }

    private static int insertReceipt(Connection c, PhieuNhapHang receipt) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO PhieuNhapHang (NgayNhap, TongTien, DaThanhToan, MaNCC, MaNV) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(receipt.getNgayNhap().getTime()));
            ps.setDouble(2, receipt.getTongTien());
            ps.setDouble(3, receipt.getDaThanhToan());
            ps.setInt(4, receipt.getMaNCC());
            ps.setString(5, receipt.getMaNV());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static void insertLine(Connection c, ChiTietPhieuNhap line) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO ChiTietPhieuNhap (MaPN, MaSanPham, SoLuong, DonGiaNhap, ThanhTien) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, line.getMaPN());
            ps.setInt(2, line.getMaSanPham());
            ps.setInt(3, line.getSoLuong());
            ps.setDouble(4, line.getDonGiaNhap());
            ps.setDouble(5, line.getThanhTien());
            ps.executeUpdate();
        }
    }

    private static void increaseStock(Connection c, ChiTietPhieuNhap line) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE SanPham SET SoLuongTonKho = SoLuongTonKho + ? WHERE MaSanPham = ?")) {
            ps.setInt(1, line.getSoLuong());
            ps.setInt(2, line.getMaSanPham());
            if (ps.executeUpdate() != 1) {
                throw new BusinessException("Sản phẩm mã " + line.getMaSanPham() + " không tồn tại");
            }
        }
    }

    private static void addDebt(Connection c, int maNCC, double amount) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE NhaCungCap SET DuNo = DuNo + ? WHERE MaNCC = ?")) {
            ps.setDouble(1, amount);
            ps.setInt(2, maNCC);
            if (ps.executeUpdate() != 1) {
                throw new BusinessException("Nhà cung cấp mã " + maNCC + " không tồn tại");
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
