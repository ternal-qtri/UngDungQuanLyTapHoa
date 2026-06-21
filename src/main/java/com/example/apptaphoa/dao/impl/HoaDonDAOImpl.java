package com.example.apptaphoa.dao.impl;    

import com.example.apptaphoa.dao.HoaDonDAOItf;
import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.utils.XJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonDAOImpl implements HoaDonDAOItf {

    private final String createSql       = "INSERT INTO HoaDon(ThoiGian, TongTien, HinhThucTT, MaNV) VALUES (?, ?, ?, ?)";
    private final String findAllSql      = "SELECT * FROM HoaDon ORDER BY MaHD DESC";
    private final String findByIdSql     = "SELECT * FROM HoaDon WHERE MaHD=?";
    private final String findByDateRange = "SELECT * FROM HoaDon WHERE ThoiGian BETWEEN ? AND ? ORDER BY ThoiGian DESC";

    @Override
    public HoaDon create(HoaDon entity) {
        Object[] values = {
            new java.sql.Timestamp(entity.getThoiGian().getTime()),
            entity.getTongTien(),
            entity.getHinhThucTT(),
            entity.getMaNV()
        };
        XJDBC.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(HoaDon entity) { throw new UnsupportedOperationException(); }

    @Override
    public void deleteById(String id) { throw new UnsupportedOperationException(); }

    @Override
    public List<HoaDon> findAll() {
        List<HoaDon> list = new ArrayList<>();
        try (Connection conn = XJDBC.openConnection();
             PreparedStatement ps = conn.prepareStatement(findAllSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getInt("MaHD"));
                hd.setThoiGian(rs.getTimestamp("ThoiGian")); // giữ HH:mm:ss
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setHinhThucTT(rs.getString("HinhThucTT"));
                hd.setMaNV(rs.getString("MaNV"));
                list.add(hd);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public HoaDon findById(int id) {
        try (Connection conn = XJDBC.openConnection();
             PreparedStatement ps = conn.prepareStatement(findByIdSql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getInt("MaHD"));
                hd.setThoiGian(rs.getTimestamp("ThoiGian"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setHinhThucTT(rs.getString("HinhThucTT"));
                hd.setMaNV(rs.getString("MaNV"));
                return hd;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<HoaDon> findByDateRange(Date tuNgay, Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        try (Connection conn = XJDBC.openConnection();
             PreparedStatement ps = conn.prepareStatement(findByDateRange)) {

            // DÙNG Timestamp để không mất HH:mm:ss
            ps.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getInt("MaHD"));
                hd.setThoiGian(rs.getTimestamp("ThoiGian"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setHinhThucTT(rs.getString("HinhThucTT"));
                hd.setMaNV(rs.getString("MaNV"));
                list.add(hd);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public HoaDon findById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
