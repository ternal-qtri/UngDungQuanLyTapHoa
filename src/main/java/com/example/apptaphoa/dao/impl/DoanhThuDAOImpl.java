package com.example.apptaphoa.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.apptaphoa.dao.DoanhThuDAO;
import com.example.apptaphoa.model.DoanhThu;
import com.example.apptaphoa.utils.XJDBC;

/**
 *
 * @author Boo6
 */
public class DoanhThuDAOImpl implements DoanhThuDAO {

    private final String sql = """
        SELECT 
                    cast(thoigian as date) AS Ngay,
                    COUNT(DISTINCT hd.MaHD) AS SoHoaDon,
                    SUM(ct.SoLuong) AS SoSanPham,
                    SUM(ct.SoLuong * ct.DonGia) AS TongTien
                FROM HoaDon hd
                JOIN ChiTietHoaDon ct ON hd.MaHD = ct.MaHD
                WHERE cast(thoigian as date) BETWEEN ? AND ?
                GROUP BY cast(thoigian as date)
                ORDER BY Ngay ASC
    """;
    private final String findbyTime ="""
        SELECT 
                    cast(thoigian as date) AS Ngay,
                    COUNT(DISTINCT hd.MaHD) AS SoHoaDon,
                    SUM(ct.SoLuong) AS SoSanPham,
                    SUM(ct.SoLuong * ct.DonGia) AS TongTien
                FROM HoaDon hd
                JOIN ChiTietHoaDon ct ON hd.MaHD = ct.MaHD
                WHERE cast(thoigian as date) =?
                GROUP BY cast(thoigian as date)
                ORDER BY Ngay ASC
    """;
    private final String findall="""
        SELECT 
                    cast(thoigian as date) AS Ngay,
                    COUNT(DISTINCT hd.MaHD) AS SoHoaDon,
                    SUM(ct.SoLuong) AS SoSanPham,
                    SUM(ct.SoLuong * ct.DonGia) AS TongTien
                FROM HoaDon hd
                JOIN ChiTietHoaDon ct ON hd.MaHD = ct.MaHD
                GROUP BY cast(thoigian as date)
                ORDER BY Ngay ASC
    """;
    @Override
    public List<DoanhThu> DoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        List<DoanhThu> list = new ArrayList<>();
        try (Connection con = XJDBC.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DoanhThu dt = new DoanhThu();
                dt.setNgay(rs.getDate("Ngay"));
                dt.setSoHoaDon(rs.getInt("SoHoaDon"));
                dt.setSoSanPham(rs.getInt("SoSanPham"));
                dt.setTongTien(rs.getFloat("TongTien"));
                list.add(dt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public DoanhThu findbyTime(Date ngay) {
        try (Connection conn = XJDBC.openConnection(); 
            PreparedStatement ps = conn.prepareStatement(findbyTime)) {
            ps.setDate(1, new java.sql.Date(ngay.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DoanhThu dt = new DoanhThu();
                dt.setNgay(rs.getDate("Ngay"));
                dt.setSoHoaDon(rs.getInt("SoHoaDon"));
                dt.setSoSanPham(rs.getInt("SoSanPham"));
                dt.setTongTien(rs.getFloat("TongTien"));
                return dt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DoanhThu> findAll() {
        List<DoanhThu> list = new ArrayList<>();
        try (Connection conn = XJDBC.openConnection(); 
            PreparedStatement ps = conn.prepareStatement(findall); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DoanhThu dt = new DoanhThu();
                dt.setNgay(rs.getDate("Ngay"));
                dt.setSoHoaDon(rs.getInt("SoHoaDon"));
                dt.setSoSanPham(rs.getInt("SoSanPham"));
                dt.setTongTien(rs.getFloat("TongTien"));
                list.add(dt);
            }
    } catch (Exception e) {
            e.printStackTrace();
        }
        return list;    }

}
