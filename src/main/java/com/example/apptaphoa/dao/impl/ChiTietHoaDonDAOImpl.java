package com.example.apptaphoa.dao.impl;

import com.example.apptaphoa.dao.ChiTietHoaDonDAOItf;
import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.utils.XJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAOImpl implements ChiTietHoaDonDAOItf {

    private final String insertSql   = "INSERT INTO ChiTietHoaDon (MaHD, MaSanPham, SoLuong, DonGia, ThanhTien) VALUES (?,?,?,?,?)";
    private final String findByHdSql = "SELECT * FROM ChiTietHoaDon WHERE MaHD=?";

    @Override
    public void insert(ChiTietHoaDon entity) {
        XJDBC.executeUpdate(insertSql,
                entity.getMaHD(),
                entity.getMaSanPham(),
                entity.getSoLuong(),
                entity.getDonGia(),
                entity.getThanhTien());
    }

    @Override
    public List<ChiTietHoaDon> findByMaHD(int maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        try (Connection conn = XJDBC.openConnection();
             PreparedStatement ps = conn.prepareStatement(findByHdSql)) {
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon c = new ChiTietHoaDon();
                c.setMaHD(rs.getInt("MaHD"));
                c.setMaSanPham(rs.getInt("MaSanPham"));
                c.setSoLuong(rs.getInt("SoLuong"));
                c.setDonGia(rs.getDouble("DonGia"));
                c.setThanhTien(rs.getDouble("ThanhTien"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
