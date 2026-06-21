package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.utils.JDBCUtil;

import java.sql.*;
import java.util.*;

public class SanPhamDAO {
    public List<SanPham> getAllSanPham() {
        List<SanPham> list = new ArrayList<>();
        try (Connection con = JDBCUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM SanPham")) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getInt("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));
                sp.setDonGia(rs.getDouble("DonGia"));
                sp.setSoLuongTonKho(rs.getInt("SoLuongTonKho"));
                sp.setLoaiSanPham(rs.getString("LoaiSanPham"));
                list.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public SanPham getSanPhamById(int maSP) {
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM SanPham WHERE MaSanPham = ?")) {
            ps.setInt(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(maSP);
                sp.setTenSanPham(rs.getString("TenSanPham"));
                sp.setDonGia(rs.getDouble("DonGia"));
                sp.setSoLuongTonKho(rs.getInt("SoLuongTonKho"));
                sp.setLoaiSanPham(rs.getString("LoaiSanPham"));
                return sp;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<String> getAllLoaiSanPham() {
        List<String> list = new ArrayList<>();
        try (Connection con = JDBCUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT LoaiSanPham FROM SanPham")) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<SanPham> getSanPhamByLoai(String loai) {
        List<SanPham> list = new ArrayList<>();
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM SanPham WHERE LoaiSanPham = ?")) {
            ps.setString(1, loai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getInt("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));
                sp.setDonGia(rs.getDouble("DonGia"));
                sp.setSoLuongTonKho(rs.getInt("SoLuongTonKho"));
                sp.setLoaiSanPham(rs.getString("LoaiSanPham"));
                list.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getSoLuongTonKhoByMaSP(int maSP) {
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT SoLuongTonKho FROM SanPham WHERE MaSanPham = ?")) {
            ps.setInt(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public void giamSoLuongTonKho(int maSP, int soLuongBan) {
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "UPDATE SanPham SET SoLuongTonKho = SoLuongTonKho - ? WHERE MaSanPham = ?")) {
            ps.setInt(1, soLuongBan);
            ps.setInt(2, maSP);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public SanPham findById(int id) {
        String sql = "SELECT MaSanPham, TenSanPham, DonGia FROM SanPham WHERE MaSanPham=?";
        try (var c = JDBCUtil.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(rs.getInt(1));
                    sp.setTenSanPham(rs.getString(2));
                    sp.setDonGia(rs.getDouble(3));
                    return sp;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public SanPham findFirstByNameLike(String keyword) {
        String sql = "SELECT TOP 1 MaSanPham, TenSanPham, DonGia " +
                     "FROM SanPham WHERE TenSanPham LIKE ? ORDER BY TenSanPham";
        try (var c = JDBCUtil.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(rs.getInt(1));
                    sp.setTenSanPham(rs.getString(2));
                    sp.setDonGia(rs.getDouble(3));
                    return sp;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
