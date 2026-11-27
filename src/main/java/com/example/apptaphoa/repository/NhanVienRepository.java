package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.NhanVien;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository {
    List<NhanVien> findAll();

    Optional<NhanVien> findByMaNV(String maNV);

    Optional<NhanVien> findByUsername(String username);

    Optional<NhanVien> findByUsernameAndEmail(String username, String email);

    void insert(NhanVien nv);

    /** Cập nhật thông tin, không đổi mật khẩu. */
    void update(NhanVien nv);

    void updatePasswordHash(String username, String passwordHash);

    void delete(String maNV);
}
