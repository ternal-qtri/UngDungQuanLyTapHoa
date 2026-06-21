package com.example.apptaphoa.dao;

import com.example.apptaphoa.utils.JDBCUtil;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.apptaphoa.model.PhieuNhapHang;
import com.example.apptaphoa.model.NhaCungCap;

//NOTE: IMPL
public class PhieuNhapHangDAO {
    // Thêm nhân viên mới
	public boolean create(PhieuNhapHang pn) {
        String sql = "INSERT INTO PhieuNhapHang (MaPN, NgayNhap, TongTien, DaThanhToan, MaNCC, MaNV) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pn.getMaPN());
            ps.setDate(2, new java.sql.Date(pn.getNgayNhap().getTime()));
            ps.setDouble(3, pn.getTongTien());
            ps.setDouble(4, pn.getDaThanhToan());
            ps.setInt(5, pn.getMaNCC());
            ps.setString(6, pn.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Sửa thông tin nhân viên
	public boolean update(PhieuNhapHang pn) {
        String sql = "UPDATE PhieuNhapHang SET NgayNhap=?, TongTien=?, DaThanhToan=?, MaNCC=?, MaNV=? " +
                     "WHERE MaPN=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

        	ps.setDate(2, new java.sql.Date(pn.getNgayNhap().getTime()));
            ps.setDouble(2, pn.getTongTien());
            ps.setDouble(3, pn.getDaThanhToan());
            ps.setInt(4, pn.getMaNCC());
            ps.setString(5, pn.getMaNV());                    // String
            ps.setInt(6, pn.getMaPN());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Xóa nhân viên theo mã
	public boolean delete(int maPN) {
        String sql = "DELETE FROM PhieuNhapHang WHERE MaPN=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    public boolean DuNo(NhaCungCap ncc) {
		String sql = "UPDATE NhaCungCap SET DuNo = ? WHERE MaNCC=?";
		 try (Connection con = JDBCUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	        	ps.setDouble(1, ncc.getDuNo());
	        	ps.setInt(2, ncc.getMaNCC());
	            return ps.executeUpdate() > 0;
	        } catch (Exception e) { e.printStackTrace(); }
		return false;
	}

    // Tìm nhân viên theo mã
    public PhieuNhapHang findById(int maPN) {
        String sql = "SELECT * FROM PhieuNhapHang WHERE MaPN=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPhieuNhapHang(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Lấy tất cả nhân viên
    public static List<PhieuNhapHang> findAll() {
        List<PhieuNhapHang> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang";
        try (Connection con = JDBCUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPhieuNhapHang(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

//    // Tìm sản phẩm theo tên sản phẩm và mã sản phẩm (login)
//    public PhieuNhapHang findByUsernameAndPassword(String tenSanPham, Integer maSanPham) {
//        String sql = "SELECT * FROM SanPham WHERE TenSanPham = ? AND MaSanPham = ?";
//        try (Connection con = JDBCUtil.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setString(1, tenSanPham);
//            ps.setInt(2, maSanPham);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) return mapSanPham(rs);
//        } catch (Exception e) { e.printStackTrace(); }
//        return null;
//    }

    // Hàm chuyển ResultSet thành object NhanVien
    private static PhieuNhapHang mapPhieuNhapHang(ResultSet rs) throws SQLException {
        PhieuNhapHang pn = new PhieuNhapHang();
        pn.setMaPN(rs.getInt("MaPN"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setTongTien(rs.getDouble("TongTien"));
        pn.setDaThanhToan(rs.getDouble("DaThanhToan"));
        pn.setMaNCC(rs.getInt("MaNCC"));
        pn.setMaNV(rs.getString("MaNV"));
        return pn;
    }
}