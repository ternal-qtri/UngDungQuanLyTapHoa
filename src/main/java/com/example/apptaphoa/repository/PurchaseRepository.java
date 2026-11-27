package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.ChiTietPhieuNhap;
import com.example.apptaphoa.model.PhieuNhapHang;

import java.util.List;

public interface PurchaseRepository {
    /**
     * Lưu phiếu nhập, chi tiết, cộng tồn kho và cộng công nợ NCC trong MỘT transaction.
     * debtAdded là phần còn nợ của phiếu (tổng tiền - đã thanh toán).
     * Trả về mã phiếu nhập vừa tạo.
     */
    int savePurchase(PhieuNhapHang receipt, List<ChiTietPhieuNhap> lines, double debtAdded);
}
