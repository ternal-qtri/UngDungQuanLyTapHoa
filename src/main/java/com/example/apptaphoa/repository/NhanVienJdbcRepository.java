package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.model.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienJdbcRepository implements NhanVienRepository {
    private static final String COLUMNS = "MaNV, HoTen, TenDangNhap, MatKhau, GioiTinh, Email, VaiTro";

    @Override
    public List<NhanVien> findAll() {
        return query("SELECT " + COLUMNS + " FROM NhanVien ORDER BY MaNV");
    }

    @Override
    public Optional<NhanVien> findByMaNV(String maNV) {
        return queryOne("SELECT " + COLUMNS + " FROM NhanVien WHERE MaNV = ?", maNV);
    }

    @Override
    public Optional<NhanVien> findByUsername(String username) {
        return queryOne("SELECT " + COLUMNS + " FROM NhanVien WHERE TenDangNhap = ?", username);
    }

    @Override
    public Optional<NhanVien> findByUsernameAndEmail(String username, String email) {
        return queryOne("SELECT " + COLUMNS + " FROM NhanVien WHERE TenDangNhap = ? AND Email = ?",
                username, email);
    }

    @Override
    public void insert(NhanVien nv) {
        execute("INSERT INTO NhanVien (MaNV, HoTen, TenDangNhap, MatKhau, GioiTinh, Email, VaiTro) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                nv.getMaNV(), nv.getHoTen(), nv.getTenDangNhap(), nv.getMatKhau(),
                nv.getGioiTinh(), nv.getEmail(), nv.getVaiTro());
    }

    @Override
    public void update(NhanVien nv) {
        execute("UPDATE NhanVien SET HoTen = ?, TenDangNhap = ?, GioiTinh = ?, Email = ?, VaiTro = ? "
                        + "WHERE MaNV = ?",
                nv.getHoTen(), nv.getTenDangNhap(), nv.getGioiTinh(), nv.getEmail(), nv.getVaiTro(),
                nv.getMaNV());
    }

    @Override
    public void updatePasswordHash(String username, String passwordHash) {
        execute("UPDATE NhanVien SET MatKhau = ? WHERE TenDangNhap = ?", passwordHash, username);
    }

    @Override
    public void delete(String maNV) {
        execute("DELETE FROM NhanVien WHERE MaNV = ?", maNV);
    }

    private List<NhanVien> query(String sql, Object... params) {
        try (Connection c = Db.open(); PreparedStatement ps = prepare(c, sql, params); ResultSet rs = ps.executeQuery()) {
            List<NhanVien> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được danh sách nhân viên", e);
        }
    }

    private Optional<NhanVien> queryOne(String sql, Object... params) {
        List<NhanVien> list = query(sql, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    private void execute(String sql, Object... params) {
        try (Connection c = Db.open(); PreparedStatement ps = prepare(c, sql, params)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không ghi được dữ liệu nhân viên", e);
        }
    }

    static PreparedStatement prepare(Connection c, String sql, Object... params) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
        return ps;
    }

    private static NhanVien map(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("MaNV"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setTenDangNhap(rs.getString("TenDangNhap"));
        nv.setMatKhau(rs.getString("MatKhau"));
        nv.setGioiTinh(rs.getString("GioiTinh"));
        nv.setEmail(rs.getString("Email"));
        nv.setVaiTro(rs.getString("VaiTro"));
        return nv;
    }
}
