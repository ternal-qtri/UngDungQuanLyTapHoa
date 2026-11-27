package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SanPhamJdbcRepository implements SanPhamRepository {
    private static final String COLUMNS = "MaSanPham, TenSanPham, DonGia, SoLuongTonKho, LoaiSanPham";

    @Override
    public List<SanPham> findAll() {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM SanPham ORDER BY TenSanPham");
             ResultSet rs = ps.executeQuery()) {
            List<SanPham> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được danh sách sản phẩm", e);
        }
    }

    @Override
    public Optional<SanPham> findById(int maSanPham) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM SanPham WHERE MaSanPham = ?")) {
            ps.setInt(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được sản phẩm", e);
        }
    }

    @Override
    public int insert(SanPham sp) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO SanPham (TenSanPham, DonGia, SoLuongTonKho, LoaiSanPham) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setDouble(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuongTonKho());
            ps.setString(4, sp.getLoaiSanPham());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không thêm được sản phẩm", e);
        }
    }

    @Override
    public void update(SanPham sp) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE SanPham SET TenSanPham = ?, DonGia = ?, SoLuongTonKho = ?, LoaiSanPham = ? "
                             + "WHERE MaSanPham = ?")) {
            ps.setString(1, sp.getTenSanPham());
            ps.setDouble(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuongTonKho());
            ps.setString(4, sp.getLoaiSanPham());
            ps.setInt(5, sp.getMaSanPham());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không cập nhật được sản phẩm", e);
        }
    }

    @Override
    public void delete(int maSanPham) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM SanPham WHERE MaSanPham = ?")) {
            ps.setInt(1, maSanPham);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không xóa được sản phẩm (có thể đã có trong hóa đơn)", e);
        }
    }

    private static SanPham map(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaSanPham(rs.getInt("MaSanPham"));
        sp.setTenSanPham(rs.getString("TenSanPham"));
        sp.setDonGia(rs.getDouble("DonGia"));
        sp.setSoLuongTonKho(rs.getInt("SoLuongTonKho"));
        sp.setLoaiSanPham(rs.getString("LoaiSanPham"));
        return sp;
    }
}
