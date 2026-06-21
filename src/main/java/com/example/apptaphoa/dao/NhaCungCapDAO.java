package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    // Lấy toàn bộ danh sách nhà cung cấp
    public List<NhaCungCap> selectAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getInt("MaNCC"));
                ncc.setHoTenNCC(rs.getString("HoTenNCC"));
                ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setDuNo(rs.getDouble("DuNo"));
                list.add(ncc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy nhà cung cấp theo mã
    public NhaCungCap selectById(int ma) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNCC = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getInt("MaNCC"));
                ncc.setHoTenNCC(rs.getString("HoTenNCC"));
                ncc.setSoDienThoai(rs.getString("SoDienThoai"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setDuNo(rs.getDouble("DuNo"));
                return ncc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm nhà cung cấp
    public int insert(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (HoTenNCC, SoDienThoai, DiaChi, DuNo) VALUES (?, ?, ?, ?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getHoTenNCC());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setDouble(4, ncc.getDuNo());

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Cập nhật nhà cung cấp
    public int update(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET HoTenNCC = ?, SoDienThoai = ?, DiaChi = ?, DuNo = ? WHERE MaNCC = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getHoTenNCC());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setDouble(4, ncc.getDuNo());
            ps.setInt(5, ncc.getMaNCC());

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa nhà cung cấp
    public int delete(int ma) {
        String sql = "DELETE FROM NhaCungCap WHERE MaNCC = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ma);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
