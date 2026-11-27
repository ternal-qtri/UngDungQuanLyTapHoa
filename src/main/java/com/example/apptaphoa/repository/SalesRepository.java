package com.example.apptaphoa.repository;

import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;

import java.time.LocalDate;
import java.util.List;

public interface SalesRepository {
    /** Hóa đơn trong khoảng ngày, mới nhất trước. maNV = null nghĩa là tất cả nhân viên. */
    List<HoaDon> findInvoices(LocalDate from, LocalDate to, String maNV);

    /** Các dòng của một hóa đơn, kèm tên sản phẩm. */
    List<ChiTietHoaDon> findLines(int maHD);

    /**
     * Lưu hóa đơn, chi tiết và trừ tồn kho trong MỘT transaction.
     * Nếu bất kỳ dòng nào không đủ tồn kho thì rollback toàn bộ và ném InsufficientStockException.
     * Trả về mã hóa đơn vừa tạo.
     */
    int saveInvoice(HoaDon invoice, List<ChiTietHoaDon> lines);
}
