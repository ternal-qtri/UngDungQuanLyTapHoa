package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.repository.NhaCungCapRepository;
import com.example.apptaphoa.security.Role;
import com.example.apptaphoa.security.Session;

/** Trả công nợ cho nhà cung cấp. Chỉ quản lý được thực hiện. */
public class DebtService {
    private final NhaCungCapRepository suppliers;

    public DebtService(NhaCungCapRepository suppliers) {
        this.suppliers = suppliers;
    }

    public void payDebt(int maNCC, double amount) {
        Session.requireRole(Role.MANAGER);
        if (amount <= 0) {
            throw new BusinessException("Số tiền trả phải lớn hơn 0");
        }
        double money = Checks.money(amount);
        if (!suppliers.reduceDebt(maNCC, money)) {
            throw new BusinessException("Số tiền trả vượt quá công nợ hoặc nhà cung cấp không tồn tại");
        }
    }
}
