package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.utils.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    public int insertAndReturnId(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (ThoiGian, TongTien, HinhThucTT, MaNV) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new java.sql.Timestamp(hd.getThoiGian().getTime()));
            ps.setDouble(2, hd.getTongTien());
            ps.setString(3, hd.getHinhThucTT());
            ps.setString(4, hd.getMaNV());
            int aff = ps.executeUpdate();
            if (aff > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int countTodayByNhanVien(String maNV) {
        String sql = """
            SELECT COUNT(*)
            FROM HoaDon
            WHERE MaNV = ?
              AND ThoiGian >= CAST(GETDATE() AS date)
              AND ThoiGian <  DATEADD(day, 1, CAST(GETDATE() AS date))
        """;
        try (var con = JDBCUtil.getConnection();
             var ps  = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double sumTodayByNhanVien(String maNV) {
        String sql = """
            SELECT COALESCE(SUM(TongTien), 0)
            FROM HoaDon
            WHERE MaNV = ?
              AND ThoiGian >= CAST(GETDATE() AS date)
              AND ThoiGian <  DATEADD(day, 1, CAST(GETDATE() AS date))
        """;
        try (var con = JDBCUtil.getConnection();
             var ps  = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0d;
    }
    
    public List<HoaDon> findTodayByNhanVien(String maNV) {
        String sql = """
            SELECT MaHD, ThoiGian, TongTien, HinhThucTT, MaNV
            FROM HoaDon
            WHERE MaNV = ?
              AND ThoiGian >= CAST(GETDATE() AS date)
              AND ThoiGian <  DATEADD(day, 1, CAST(GETDATE() AS date))
            ORDER BY ThoiGian DESC
        """;
        List<HoaDon> list = new ArrayList<>();
        try (var con = JDBCUtil.getConnection();
             var ps  = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getInt("MaHD"));
                    hd.setThoiGian(rs.getTimestamp("ThoiGian"));
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setHinhThucTT(rs.getString("HinhThucTT"));
                    hd.setMaNV(rs.getString("MaNV"));
                    list.add(hd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
