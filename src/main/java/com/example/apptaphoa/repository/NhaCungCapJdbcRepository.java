package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;
import com.example.apptaphoa.model.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhaCungCapJdbcRepository implements NhaCungCapRepository {
    private static final String COLUMNS = "MaNCC, HoTenNCC, SoDienThoai, DiaChi, DuNo";

    @Override
    public List<NhaCungCap> findAll() {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM NhaCungCap ORDER BY HoTenNCC");
             ResultSet rs = ps.executeQuery()) {
            List<NhaCungCap> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được danh sách nhà cung cấp", e);
        }
    }

    @Override
    public Optional<NhaCungCap> findById(int maNCC) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM NhaCungCap WHERE MaNCC = ?")) {
            ps.setInt(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không đọc được nhà cung cấp", e);
        }
    }

    @Override
    public int insert(NhaCungCap ncc) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO NhaCungCap (HoTenNCC, SoDienThoai, DiaChi) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ncc.getHoTenNCC());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Không thêm được nhà cung cấp", e);
        }
    }

    @Override
    public void update(NhaCungCap ncc) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE NhaCungCap SET HoTenNCC = ?, SoDienThoai = ?, DiaChi = ? WHERE MaNCC = ?")) {
            ps.setString(1, ncc.getHoTenNCC());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setInt(4, ncc.getMaNCC());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không cập nhật được nhà cung cấp", e);
        }
    }

    @Override
    public void delete(int maNCC) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM NhaCungCap WHERE MaNCC = ?")) {
            ps.setInt(1, maNCC);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không xóa được nhà cung cấp (có thể đã có phiếu nhập)", e);
        }
    }

    @Override
    public boolean reduceDebt(int maNCC, double amount) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE NhaCungCap SET DuNo = DuNo - ? WHERE MaNCC = ? AND DuNo >= ?")) {
            ps.setDouble(1, amount);
            ps.setInt(2, maNCC);
            ps.setDouble(3, amount);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Không cập nhật được công nợ", e);
        }
    }

    private static NhaCungCap map(ResultSet rs) throws SQLException {
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNCC(rs.getInt("MaNCC"));
        ncc.setHoTenNCC(rs.getString("HoTenNCC"));
        ncc.setSoDienThoai(rs.getString("SoDienThoai"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setDuNo(rs.getDouble("DuNo"));
        return ncc;
    }
}
