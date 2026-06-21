package com.example.apptaphoa.dao;

import com.example.apptaphoa.utils.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.apptaphoa.model.SanPham;

//NOTE: IMPL
public class SanPhamDao_ {
    // Thêm nhân viên mới
    public boolean create(SanPham sp) {
        String sql = "INSERT INTO SanPham (MaSanPham, TenSanPham, DonGia, SoLuongTonKho, LoaiSanPham) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setDouble(3, sp.getDonGia());
            ps.setInt(4, sp.getSoLuongTonKho());
            ps.setString(5, sp.getLoaiSanPham());
           
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Sửa thông tin nhân viên
    public boolean update(SanPham sp) {
        String sql = "UPDATE SanPham SET TenSanPham=?, DonGia=?, SoLuongTonKho=?, LoaiSanPham=? WHERE MaSanPham=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setDouble(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuongTonKho());
            ps.setString(4, sp.getLoaiSanPham());
            ps.setInt(5, sp.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Xóa nhân viên theo mã
    public boolean delete(int maSanPham) {
        String sql = "DELETE FROM SanPham WHERE MaSanPham=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Tìm nhân viên theo mã
    public SanPham findById(int maSanPham) {
        String sql = "SELECT * FROM SanPham WHERE MaSanPham=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maSanPham);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSanPham(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Lấy tất cả nhân viên
    public static List<SanPham> findAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        try (Connection con = JDBCUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapSanPham(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Tìm sản phẩm theo tên sản phẩm và mã sản phẩm (login)
    public SanPham findByUsernameAndPassword(String tenSanPham, Integer maSanPham) {
        String sql = "SELECT * FROM SanPham WHERE TenSanPham = ? AND MaSanPham = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenSanPham);
            ps.setInt(2, maSanPham);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSanPham(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Hàm chuyển ResultSet thành object NhanVien
    private static SanPham mapSanPham(ResultSet rs) throws SQLException {
        SanPham nv = new SanPham();
        nv.setMaSanPham(rs.getInt("MaSanPham"));
        nv.setTenSanPham(rs.getString("TenSanPham"));
        nv.setDonGia(rs.getDouble("DonGia"));
        nv.setSoLuongTonKho(rs.getInt("SoLuongTonKho"));
        nv.setLoaiSanPham(rs.getString("LoaiSanPham"));
        return nv;
    }
}