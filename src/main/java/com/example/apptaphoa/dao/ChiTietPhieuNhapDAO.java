package com.example.apptaphoa.dao;

import com.example.apptaphoa.utils.JDBCUtil;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.apptaphoa.model.ChiTietPhieuNhap;
import com.example.apptaphoa.model.SanPham;

//NOTE: IMPL
public class ChiTietPhieuNhapDAO {
    // Thêm nhân viên mới
    public boolean create(ChiTietPhieuNhap ctpn) {
        String sql = "INSERT INTO ChiTietPhieuNhap (MaPN, MaSanPham, SoLuong, DonGiaNhap,ThanhTien) VALUES (?, ?, ?, ?,?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ctpn.getMaPN());
            ps.setInt(2, ctpn.getMaSanPham());
            ps.setInt(3, ctpn.getSoLuong());
            ps.setDouble(4, ctpn.getDonGiaNhap());
            ps.setDouble(5, ctpn.getThanhTien());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Sửa thông tin nhân viên
    public boolean update(ChiTietPhieuNhap ctpn) {
        String sql = "UPDATE ChiTietPhieuNhap SET MaSanPham=?, SoLuong=?, DonGiaNhap=?, ThanhTien=?  WHERE MaPN=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	
        	ps.setInt(5, ctpn.getMaPN());
            ps.setInt(1, ctpn.getMaSanPham());
            ps.setInt(2, ctpn.getSoLuong());
            ps.setDouble(3, ctpn.getDonGiaNhap());
            ps.setDouble(4, ctpn.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Xóa nhân viên theo mã
    public boolean delete(int maPN) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE MaPN=?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    public boolean UpdateSoLuongTonKho(SanPham sPham) {
		String sql = "UPDATE SanPham SET SoLuongTonKho = ? WHERE MaSanPham=?";
		 try (Connection con = JDBCUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	        	ps.setInt(1, sPham.getSoLuongTonKho());
	        	ps.setInt(2, sPham.getMaSanPham());
	            return ps.executeUpdate() > 0;
	        } catch (Exception e) { e.printStackTrace(); }
		return false;
	}
    
    public static int getSoLuongTonKho(int maSP) {
        String sql = "SELECT SoLuongTonKho FROM SanPham WHERE MaSanPham = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoLuongTonKho");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Hoặc giá trị báo lỗi
    }

    public List<ChiTietPhieuNhap> findByMaPN(int maPN) {
        List<ChiTietPhieuNhap> list = new ArrayList<>();
        String sql = "SELECT MaPN, MaSanPham, SoLuong, DonGiaNhap, ThanhTien "
                + "FROM ChiTietPhieuNhap WHERE MaPN = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                ct.setMaPN(rs.getInt("MaPN"));
                ct.setMaSanPham(rs.getInt("MaSanPham"));
                ct.setSoLuong(rs.getInt("SoLuong"));
                ct.setDonGiaNhap(rs.getDouble("DonGiaNhap"));
                ct.setThanhTien(rs.getDouble("ThanhTien"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tất cả nhân viên
    public static List<ChiTietPhieuNhap> findAll() {
        List<ChiTietPhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap";
        try (Connection con = JDBCUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapChiTietPhieuNhap(rs));
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
    private static ChiTietPhieuNhap mapChiTietPhieuNhap(ResultSet rs) throws SQLException {
        ChiTietPhieuNhap pn = new ChiTietPhieuNhap();
        pn.setMaPN(rs.getInt("MaPN"));
        pn.setMaSanPham(rs.getInt("MaSanPham"));
        pn.setSoLuong(rs.getInt("SoLuong"));
        pn.setDonGiaNhap(rs.getDouble("DonGiaNhap"));
        pn.setThanhTien(rs.getDouble("ThanhTien"));
        return pn;
    }
}