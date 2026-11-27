package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.SanPham;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository {
    List<SanPham> findAll();

    Optional<SanPham> findById(int maSanPham);

    /** Trả về mã sản phẩm vừa tạo (IDENTITY). */
    int insert(SanPham sp);

    void update(SanPham sp);

    void delete(int maSanPham);
}
