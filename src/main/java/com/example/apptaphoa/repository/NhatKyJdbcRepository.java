package com.example.apptaphoa.repository;

import com.example.apptaphoa.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NhatKyJdbcRepository implements NhatKyRepository {

    @Override
    public void insert(String maNV, String hanhDong, String chiTiet) {
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO NhatKy (MaNV, HanhDong, ChiTiet) VALUES (?, ?, ?)")) {
            ps.setString(1, maNV);
            ps.setString(2, hanhDong);
            ps.setString(3, chiTiet);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Không ghi được nhật ký", e);
        }
    }
}
