package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.NhaCungCap;

import java.util.List;
import java.util.Optional;

public interface NhaCungCapRepository {
    List<NhaCungCap> findAll();

    Optional<NhaCungCap> findById(int maNCC);

    int insert(NhaCungCap ncc);

    void update(NhaCungCap ncc);

    void delete(int maNCC);

    /**
     * Giảm công nợ đi amount, chỉ khi công nợ hiện tại đủ lớn.
     * Trả về false nếu không đủ điều kiện (không có NCC hoặc trả vượt nợ).
     */
    boolean reduceDebt(int maNCC, double amount);
}
