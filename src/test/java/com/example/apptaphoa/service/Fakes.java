package com.example.apptaphoa.service;

import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.ChiTietPhieuNhap;
import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.model.PhieuNhapHang;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.exception.InsufficientStockException;
import com.example.apptaphoa.repository.NhaCungCapRepository;
import com.example.apptaphoa.repository.NhanVienRepository;
import com.example.apptaphoa.repository.PurchaseRepository;
import com.example.apptaphoa.repository.SalesRepository;
import com.example.apptaphoa.repository.SanPhamRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repository giả trong bộ nhớ, giữ đúng ngữ nghĩa điều kiện của bản JDBC. */
final class Fakes {
    private Fakes() {}

    static final class Users implements NhanVienRepository {
        final Map<String, NhanVien> byUsername = new HashMap<>();

        @Override public List<NhanVien> findAll() { return new ArrayList<>(byUsername.values()); }
        @Override public Optional<NhanVien> findByMaNV(String maNV) {
            return byUsername.values().stream().filter(u -> u.getMaNV().equals(maNV)).findFirst();
        }
        @Override public Optional<NhanVien> findByUsername(String username) {
            return Optional.ofNullable(byUsername.get(username));
        }
        @Override public Optional<NhanVien> findByUsernameAndEmail(String username, String email) {
            return findByUsername(username).filter(u -> email.equals(u.getEmail()));
        }
        @Override public void insert(NhanVien nv) { byUsername.put(nv.getTenDangNhap(), nv); }
        @Override public void update(NhanVien nv) { byUsername.put(nv.getTenDangNhap(), nv); }
        @Override public void updatePasswordHash(String username, String hash) {
            byUsername.get(username).setMatKhau(hash);
        }
        @Override public void delete(String maNV) { byUsername.values().removeIf(u -> u.getMaNV().equals(maNV)); }
    }

    static final class Products implements SanPhamRepository {
        final Map<Integer, SanPham> byId = new HashMap<>();
        private int nextId = 1;

        @Override public List<SanPham> findAll() { return new ArrayList<>(byId.values()); }
        @Override public Optional<SanPham> findById(int id) { return Optional.ofNullable(byId.get(id)); }
        @Override public int insert(SanPham sp) {
            sp.setMaSanPham(nextId++);
            byId.put(sp.getMaSanPham(), sp);
            return sp.getMaSanPham();
        }
        @Override public void update(SanPham sp) { byId.put(sp.getMaSanPham(), sp); }
        @Override public void delete(int id) { byId.remove(id); }
    }

    static final class Suppliers implements NhaCungCapRepository {
        final Map<Integer, NhaCungCap> byId = new HashMap<>();
        private int nextId = 1;

        @Override public List<NhaCungCap> findAll() { return new ArrayList<>(byId.values()); }
        @Override public Optional<NhaCungCap> findById(int id) { return Optional.ofNullable(byId.get(id)); }
        @Override public int insert(NhaCungCap ncc) {
            ncc.setMaNCC(nextId++);
            byId.put(ncc.getMaNCC(), ncc);
            return ncc.getMaNCC();
        }
        @Override public void update(NhaCungCap ncc) { byId.put(ncc.getMaNCC(), ncc); }
        @Override public void delete(int id) { byId.remove(id); }
        @Override public boolean reduceDebt(int id, double amount) {
            NhaCungCap ncc = byId.get(id);
            if (ncc == null || ncc.getDuNo() < amount) return false;
            ncc.setDuNo(ncc.getDuNo() - amount);
            return true;
        }
    }

    /** Mô phỏng transaction: kiểm tra toàn bộ trước, chỉ ghi khi đủ tồn kho. */
    static final class Sales implements SalesRepository {
        final Products products;
        final List<HoaDon> invoices = new ArrayList<>();

        Sales(Products products) { this.products = products; }

        @Override
        public List<HoaDon> findInvoices(java.time.LocalDate from, java.time.LocalDate to, String maNV) {
            return invoices.stream().filter(hd -> maNV == null || maNV.equals(hd.getMaNV())).toList();
        }

        @Override
        public List<ChiTietHoaDon> findLines(int maHD) {
            return List.of();
        }

        @Override
        public int saveInvoice(HoaDon invoice, List<ChiTietHoaDon> lines) {
            for (ChiTietHoaDon line : lines) {
                SanPham sp = products.byId.get(line.getMaSanPham());
                if (sp.getSoLuongTonKho() < line.getSoLuong()) {
                    throw new InsufficientStockException(sp.getTenSanPham());
                }
            }
            for (ChiTietHoaDon line : lines) {
                SanPham sp = products.byId.get(line.getMaSanPham());
                sp.setSoLuongTonKho(sp.getSoLuongTonKho() - line.getSoLuong());
            }
            invoice.setMaHD(invoices.size() + 1);
            invoices.add(invoice);
            return invoice.getMaHD();
        }
    }

    static final class Purchases implements PurchaseRepository {
        final Products products;
        final Suppliers suppliers;
        final List<PhieuNhapHang> receipts = new ArrayList<>();
        double lastDebtAdded;

        Purchases(Products products, Suppliers suppliers) {
            this.products = products;
            this.suppliers = suppliers;
        }

        @Override
        public int savePurchase(PhieuNhapHang receipt, List<ChiTietPhieuNhap> lines, double debtAdded) {
            for (ChiTietPhieuNhap line : lines) {
                SanPham sp = products.byId.get(line.getMaSanPham());
                sp.setSoLuongTonKho(sp.getSoLuongTonKho() + line.getSoLuong());
            }
            NhaCungCap ncc = suppliers.byId.get(receipt.getMaNCC());
            ncc.setDuNo(ncc.getDuNo() + debtAdded);
            lastDebtAdded = debtAdded;
            receipt.setMaPN(receipts.size() + 1);
            receipts.add(receipt);
            return receipt.getMaPN();
        }
    }

    /** Nhật ký trong bộ nhớ: mỗi phần tử là "MaNV|HanhDong|ChiTiet". */
    static final class NhatKy implements com.example.apptaphoa.repository.NhatKyRepository {
        final List<String> entries = new ArrayList<>();

        @Override public void insert(String maNV, String hanhDong, String chiTiet) {
            entries.add(maNV + "|" + hanhDong + "|" + chiTiet);
        }
    }

    static NhanVien user(String ma, String username, String vaiTro, String passwordHash) {
        NhanVien nv = new NhanVien();
        nv.setMaNV(ma);
        nv.setHoTen(username);
        nv.setTenDangNhap(username);
        nv.setMatKhau(passwordHash);
        nv.setEmail(username + "@example.com");
        nv.setVaiTro(vaiTro);
        return nv;
    }

    static SanPham product(String ten, double donGia, int ton) {
        SanPham sp = new SanPham();
        sp.setTenSanPham(ten);
        sp.setDonGia(donGia);
        sp.setSoLuongTonKho(ton);
        return sp;
    }
}
