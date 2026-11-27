package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.DoanhThu;

import java.time.LocalDate;
import java.util.List;

public interface DoanhThuRepository {
    /** Doanh thu từng ngày trong khoảng [from, to], sắp xếp theo ngày tăng dần. */
    List<DoanhThu> findByDateRange(LocalDate from, LocalDate to);
}
