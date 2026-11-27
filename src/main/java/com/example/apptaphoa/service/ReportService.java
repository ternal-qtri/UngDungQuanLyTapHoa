package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.DoanhThu;
import com.example.apptaphoa.repository.DoanhThuRepository;
import com.example.apptaphoa.security.Role;
import com.example.apptaphoa.security.Session;

import java.time.LocalDate;
import java.util.List;

/** Báo cáo doanh thu. Chỉ quản lý được xem. */
public class ReportService {
    private final DoanhThuRepository revenue;

    public ReportService(DoanhThuRepository revenue) {
        this.revenue = revenue;
    }

    public List<DoanhThu> revenueBetween(LocalDate from, LocalDate to) {
        Session.requireRole(Role.MANAGER);
        if (from == null || to == null || to.isBefore(from)) {
            throw new BusinessException("Khoảng ngày không hợp lệ");
        }
        return revenue.findByDateRange(from, to);
    }
}
