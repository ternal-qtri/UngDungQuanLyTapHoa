package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.ChiTietPhieuNhap;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.model.PhieuNhapHang;
import com.example.apptaphoa.repository.NhaCungCapRepository;
import com.example.apptaphoa.repository.PurchaseRepository;
import com.example.apptaphoa.security.Role;
import com.example.apptaphoa.security.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Nhập hàng từ nhà cung cấp. Chỉ quản lý được thực hiện. Công nợ được cộng dồn. */
public class PurchaseService {

    /** Một dòng phiếu nhập: mã sản phẩm, số lượng và giá nhập. */
    public record PurchaseLine(int maSanPham, int soLuong, double donGiaNhap) {}

    private final NhaCungCapRepository suppliers;
    private final PurchaseRepository purchases;
    private final AuditService audit;

    public PurchaseService(NhaCungCapRepository suppliers, PurchaseRepository purchases, AuditService audit) {
        this.suppliers = suppliers;
        this.purchases = purchases;
        this.audit = audit;
    }

    /**
     * Lập phiếu nhập. Phần chưa thanh toán (tổng - daThanhToan) được cộng vào công nợ NCC.
     * Trả về mã phiếu nhập.
     */
    public int createReceipt(int maNCC, double daThanhToan, List<PurchaseLine> lines) {
        NhanVien manager = Session.requireRole(Role.MANAGER);
        suppliers.findById(maNCC)
                .orElseThrow(() -> new BusinessException("Nhà cung cấp mã " + maNCC + " không tồn tại"));
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException("Phiếu nhập chưa có sản phẩm nào");
        }

        List<ChiTietPhieuNhap> details = new ArrayList<>();
        double total = 0;
        for (PurchaseLine item : lines) {
            Checks.requirePositive(item.soLuong(), "Số lượng nhập");
            Checks.requireNonNegative(item.donGiaNhap(), "Đơn giá nhập");
            double thanhTien = Checks.money(item.soLuong() * item.donGiaNhap());
            total += thanhTien;

            ChiTietPhieuNhap detail = new ChiTietPhieuNhap();
            detail.setMaSanPham(item.maSanPham());
            detail.setSoLuong(item.soLuong());
            detail.setDonGiaNhap(item.donGiaNhap());
            detail.setThanhTien(thanhTien);
            details.add(detail);
        }
        total = Checks.money(total);

        Checks.requireNonNegative(daThanhToan, "Số tiền đã thanh toán");
        if (daThanhToan > total) {
            throw new BusinessException("Số tiền đã thanh toán lớn hơn tổng tiền phiếu nhập");
        }
        double debtAdded = Checks.money(total - daThanhToan);

        PhieuNhapHang receipt = new PhieuNhapHang();
        receipt.setNgayNhap(new Date());
        receipt.setTongTien(total);
        receipt.setDaThanhToan(Checks.money(daThanhToan));
        receipt.setMaNCC(maNCC);
        receipt.setMaNV(manager.getMaNV());
        int maPN = purchases.savePurchase(receipt, details, debtAdded);
        audit.record("NHAP_HANG", "PN #" + maPN + ", NCC " + maNCC + ", tổng " + total + ", còn nợ " + debtAdded);
        return maPN;
    }
}
