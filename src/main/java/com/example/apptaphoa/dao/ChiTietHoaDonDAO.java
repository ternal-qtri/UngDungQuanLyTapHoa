package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    public void insert(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaSanPham, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cthd.getMaHD());
            ps.setInt(2, cthd.getMaSanPham());
            ps.setInt(3, cthd.getSoLuong());
            ps.setDouble(4, cthd.getDonGia());
            ps.setDouble(5, cthd.getThanhTien());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public List<ChiTietHoaDon> findByHoaDon(int maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = """
                SELECT cthd.MaSanPham, sp.TenSanPham, cthd.SoLuong, cthd.DonGia, cthd.ThanhTien
                FROM ChiTietHoaDon cthd
                JOIN SanPham sp ON cthd.MaSanPham = sp.MaSanPham
                WHERE cthd.MaHD = ?
                """;

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setMaSanPham(rs.getInt("MaSanPham"));
                    ct.setTenSanPham(rs.getString("TenSanPham"));
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGia(rs.getDouble("DonGia"));
                    ct.setThanhTien(rs.getDouble("ThanhTien"));
                    list.add(ct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
