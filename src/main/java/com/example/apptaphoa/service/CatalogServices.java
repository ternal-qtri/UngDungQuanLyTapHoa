package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.repository.NhaCungCapRepository;
import com.example.apptaphoa.repository.NhanVienRepository;
import com.example.apptaphoa.repository.SanPhamRepository;
import com.example.apptaphoa.security.PasswordHasher;
import com.example.apptaphoa.security.Role;
import com.example.apptaphoa.security.Session;

import java.util.List;

/**
 * Quản lý danh mục: sản phẩm, nhà cung cấp, nhân viên.
 * Xem danh sách: người đã đăng nhập. Thêm/sửa/xóa: chỉ quản lý.
 */
public final class CatalogServices {
    private CatalogServices() {}

    /** Sản phẩm. */
    public static class ProductService {
        private final SanPhamRepository products;

        public ProductService(SanPhamRepository products) {
            this.products = products;
        }

        public List<SanPham> list() {
            Session.requireLogin();
            return products.findAll();
        }

        public int create(String ten, double donGia, int tonKho, String loai) {
            Session.requireRole(Role.MANAGER);
            SanPham sp = build(0, ten, donGia, tonKho, loai);
            return products.insert(sp);
        }

        public void update(int maSanPham, String ten, double donGia, int tonKho, String loai) {
            Session.requireRole(Role.MANAGER);
            products.findById(maSanPham)
                    .orElseThrow(() -> new BusinessException("Sản phẩm không tồn tại"));
            products.update(build(maSanPham, ten, donGia, tonKho, loai));
        }

        public void delete(int maSanPham) {
            Session.requireRole(Role.MANAGER);
            products.delete(maSanPham);
        }

        private static SanPham build(int ma, String ten, double donGia, int tonKho, String loai) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(ma);
            sp.setTenSanPham(Checks.requireText(ten, "Tên sản phẩm"));
            Checks.requireNonNegative(donGia, "Đơn giá");
            Checks.requireNonNegative(tonKho, "Số lượng tồn kho");
            sp.setDonGia(donGia);
            sp.setSoLuongTonKho(tonKho);
            sp.setLoaiSanPham(loai == null ? "" : loai.trim());
            return sp;
        }
    }

    /** Nhà cung cấp. */
    public static class SupplierService {
        private final NhaCungCapRepository suppliers;

        public SupplierService(NhaCungCapRepository suppliers) {
            this.suppliers = suppliers;
        }

        public List<NhaCungCap> list() {
            Session.requireLogin();
            return suppliers.findAll();
        }

        public int create(String ten, String sdt, String diaChi) {
            Session.requireRole(Role.MANAGER);
            NhaCungCap ncc = new NhaCungCap();
            ncc.setHoTenNCC(Checks.requireText(ten, "Tên nhà cung cấp"));
            ncc.setSoDienThoai(Checks.optionalPhone(sdt, "Số điện thoại"));
            ncc.setDiaChi(diaChi == null ? null : diaChi.trim());
            return suppliers.insert(ncc);
        }

        /** Sửa thông tin liên hệ. Công nợ không đổi: lấy bản ghi hiện tại rồi chỉ thay các trường nhập. */
        public void update(int maNCC, String ten, String sdt, String diaChi) {
            Session.requireRole(Role.MANAGER);
            NhaCungCap ncc = suppliers.findById(maNCC)
                    .orElseThrow(() -> new BusinessException("Nhà cung cấp không tồn tại"));
            ncc.setHoTenNCC(Checks.requireText(ten, "Tên nhà cung cấp"));
            ncc.setSoDienThoai(Checks.optionalPhone(sdt, "Số điện thoại"));
            ncc.setDiaChi(diaChi == null ? null : diaChi.trim());
            suppliers.update(ncc);
        }

        public void delete(int maNCC) {
            Session.requireRole(Role.MANAGER);
            suppliers.delete(maNCC);
        }
    }

    /** Nhân viên. Mật khẩu luôn được băm trước khi lưu. */
    public static class EmployeeService {
        private final NhanVienRepository employees;

        public EmployeeService(NhanVienRepository employees) {
            this.employees = employees;
        }

        public List<NhanVien> list() {
            Session.requireRole(Role.MANAGER);
            return employees.findAll();
        }

        public void create(NhanVien nv, String matKhauTho) {
            Session.requireRole(Role.MANAGER);
            Checks.requireText(nv.getMaNV(), "Mã nhân viên");
            Checks.requireText(nv.getTenDangNhap(), "Tên đăng nhập");
            Checks.requireText(matKhauTho, "Mật khẩu");
            Role.fromDbValue(nv.getVaiTro()); // ném lỗi nếu vai trò không hợp lệ
            nv.setEmail(Checks.optionalEmail(nv.getEmail(), "Email"));
            nv.setMatKhau(PasswordHasher.hash(matKhauTho));
            employees.insert(nv);
        }

        public void update(NhanVien nv) {
            Session.requireRole(Role.MANAGER);
            Checks.requireText(nv.getMaNV(), "Mã nhân viên");
            Checks.requireText(nv.getTenDangNhap(), "Tên đăng nhập");
            Role.fromDbValue(nv.getVaiTro());
            nv.setEmail(Checks.optionalEmail(nv.getEmail(), "Email"));
            employees.update(nv);
        }

        public void delete(String maNV) {
            Session.requireRole(Role.MANAGER);
            employees.delete(maNV);
        }
    }
}
