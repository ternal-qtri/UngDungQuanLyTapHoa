package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;

import java.util.List;

public interface SalesRepository {
    /**
     * Lưu hóa đơn, chi tiết và trừ tồn kho trong MỘT transaction.
     * Nếu bất kỳ dòng nào không đủ tồn kho thì rollback toàn bộ và ném InsufficientStockException.
     * Trả về mã hóa đơn vừa tạo.
     */
    int saveInvoice(HoaDon invoice, List<ChiTietHoaDon> lines);
}
