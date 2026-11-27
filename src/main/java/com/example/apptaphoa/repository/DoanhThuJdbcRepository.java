package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.model.DoanhThu;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoanhThuJdbcRepository implements DoanhThuRepository {
    private static final String SQL = """
            SELECT CAST(hd.ThoiGian AS date) AS Ngay,
                   COUNT(DISTINCT hd.MaHD)   AS SoHoaDon,
                   SUM(ct.SoLuong)           AS SoSanPham,
                   SUM(ct.ThanhTien)         AS TongTien
            FROM HoaDon hd
            JOIN ChiTietHoaDon ct ON ct.MaHD = hd.MaHD
            WHERE CAST(hd.ThoiGian AS date) BETWEEN ? AND ?
            GROUP BY CAST(hd.ThoiGian AS date)
            ORDER BY Ngay
            """;

    @Override
    public List<DoanhThu> findByDateRange(LocalDate from, LocalDate to) {
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<DoanhThu> list = new ArrayList<>();
                while (rs.next()) {
                    DoanhThu dt = new DoanhThu();
                    dt.setNgay(rs.getDate("Ngay"));
                    dt.setSoHoaDon(rs.getInt("SoHoaDon"));
                    dt.setSoSanPham(rs.getInt("SoSanPham"));
                    dt.setTongTien(rs.getDouble("TongTien"));
                    list.add(dt);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được báo cáo doanh thu", e);
        }
    }
}
