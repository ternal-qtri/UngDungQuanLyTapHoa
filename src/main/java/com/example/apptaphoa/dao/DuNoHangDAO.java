package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.DuNoHang;
import com.example.apptaphoa.utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuNoHangDAO {
    public List<DuNoHang> getAll() {
        List<DuNoHang> list = new ArrayList<>();
        String sql = "SELECT MaDuNo, MaNCC, SoTienNo, NgayNo, MatHang, TrangThai FROM DuNoHang";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DuNoHang d = new DuNoHang();
                d.setMaDuNo(rs.getInt("MaDuNo"));
                d.setMaNCC(rs.getInt("MaNCC"));
                d.setSoTienNo(rs.getDouble("SoTienNo"));
                d.setNgayNo(rs.getString("NgayNo"));
                d.setMatHang(rs.getString("MatHang"));
                d.setTrangThai(rs.getString("TrangThai"));
                list.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy dư nợ theo mã
    public DuNoHang getById(int maDuNo) {
        String sql = "SELECT MaDuNo, MaNCC, SoTienNo, NgayNo, MatHang, TrangThai FROM DuNoHang WHERE MaDuNo = ?";
        DuNoHang d = null;

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maDuNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    d = new DuNoHang();
                    d.setMaDuNo(rs.getInt("MaDuNo"));
                    d.setMaNCC(rs.getInt("MaNCC"));
                    d.setSoTienNo(rs.getDouble("SoTienNo"));
                    d.setNgayNo(rs.getString("NgayNo"));
                    d.setMatHang(rs.getString("MatHang"));
                    d.setTrangThai(rs.getString("TrangThai"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return d;
    }

    // Thêm dư nợ mới
    public boolean insert(DuNoHang d) {
        String sql = "INSERT INTO DuNoHang (MaNCC, SoTienNo, NgayNo, MatHang, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, d.getMaNCC());
            ps.setDouble(2, d.getSoTienNo());
            ps.setString(3, d.getNgayNo());
            ps.setString(4, d.getMatHang());
            ps.setString(5, d.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật dư nợ
    public boolean update(DuNoHang d) {
        String sql = "UPDATE DuNoHang SET MaNCC = ?, SoTienNo = ?, NgayNo = ?, MatHang = ?, TrangThai = ? WHERE MaDuNo = ?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, d.getMaNCC());
            ps.setDouble(2, d.getSoTienNo());
            ps.setString(3, d.getNgayNo());
            ps.setString(4, d.getMatHang());
            ps.setString(5, d.getTrangThai());
            ps.setInt(6, d.getMaDuNo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa dư nợ
    public boolean delete(int maDuNo) {
        String sql = "DELETE FROM DuNoHang WHERE MaDuNo = ?";

        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maDuNo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Thanh toán dư nợ
    public boolean thanhToan(int maDuNo, double soTienThanhToan) {
        DuNoHang duNo = getById(maDuNo);
        if (duNo == null) {
            return false;
        }

        double soNoConLai = duNo.getSoTienNo() - soTienThanhToan;
        if (soNoConLai < 0) {
            return false; // Tránh trả dư
        }

        duNo.setSoTienNo(soNoConLai);
        if (soNoConLai == 0) {
            duNo.setTrangThai("Đã thanh toán");
        } else {
            duNo.setTrangThai("Còn nợ");
        }

        return update(duNo);
    }
}
