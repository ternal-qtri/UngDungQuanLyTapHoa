package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.exception.InsufficientStockException;
import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.repository.SalesRepository;
import com.example.apptaphoa.repository.SanPhamRepository;
import com.example.apptaphoa.security.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Bán hàng: tính tiền, kiểm tra tồn kho, lập hóa đơn. */
public class SalesService {

    /** Một dòng trong giỏ hàng: chỉ cần mã sản phẩm và số lượng, giá lấy từ DB. */
    public record CartLine(int maSanPham, int soLuong) {}

    private final SanPhamRepository products;
    private final SalesRepository sales;

    public SalesService(SanPhamRepository products, SalesRepository sales) {
        this.products = products;
        this.sales = sales;
    }

    /** Lập hóa đơn từ giỏ hàng. Trả về mã hóa đơn. */
    public int checkout(List<CartLine> cart, String paymentMethod) {
        NhanVien seller = Session.requireLogin();
        String payment = Checks.requireText(paymentMethod, "Hình thức thanh toán");
        if (cart == null || cart.isEmpty()) {
            throw new BusinessException("Giỏ hàng trống");
        }

        List<ChiTietHoaDon> lines = new ArrayList<>();
        double total = 0;
        for (CartLine item : cart) {
            Checks.requirePositive(item.soLuong(), "Số lượng");
            SanPham product = products.findById(item.maSanPham())
                    .orElseThrow(() -> new BusinessException("Sản phẩm mã " + item.maSanPham() + " không tồn tại"));
            if (product.getSoLuongTonKho() < item.soLuong()) {
                throw new InsufficientStockException(product.getTenSanPham());
            }
            double thanhTien = Checks.money(item.soLuong() * product.getDonGia());
            total += thanhTien;

            ChiTietHoaDon line = new ChiTietHoaDon();
            line.setMaSanPham(product.getMaSanPham());
            line.setTenSanPham(product.getTenSanPham());
            line.setSoLuong(item.soLuong());
            line.setDonGia(product.getDonGia());
            line.setThanhTien(thanhTien);
            lines.add(line);
        }

        HoaDon invoice = new HoaDon();
        invoice.setThoiGian(new Date());
        invoice.setTongTien(Checks.money(total));
        invoice.setHinhThucTT(payment);
        invoice.setMaNV(seller.getMaNV());
        return sales.saveInvoice(invoice, lines);
    }
}
