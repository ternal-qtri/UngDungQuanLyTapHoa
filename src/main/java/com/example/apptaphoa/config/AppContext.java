package com.example.apptaphoa.config;

import com.example.apptaphoa.mail.GmailOtpSender;
import com.example.apptaphoa.repository.DoanhThuJdbcRepository;
import com.example.apptaphoa.repository.NhaCungCapJdbcRepository;
import com.example.apptaphoa.repository.NhanVienJdbcRepository;
import com.example.apptaphoa.repository.PurchaseJdbcRepository;
import com.example.apptaphoa.repository.SalesJdbcRepository;
import com.example.apptaphoa.repository.SanPhamJdbcRepository;
import com.example.apptaphoa.service.AuthService;
import com.example.apptaphoa.service.CatalogServices;
import com.example.apptaphoa.service.DebtService;
import com.example.apptaphoa.service.PurchaseService;
import com.example.apptaphoa.service.ReportService;
import com.example.apptaphoa.service.SalesService;

import java.time.Clock;

/** Nơi duy nhất khởi tạo repository và service (composition root). */
public final class AppContext {
    public final AuthService auth;
    public final SalesService sales;
    public final PurchaseService purchases;
    public final DebtService debts;
    public final ReportService reports;
    public final CatalogServices.ProductService products;
    public final CatalogServices.SupplierService suppliers;
    public final CatalogServices.EmployeeService employees;

    public AppContext() {
        var nhanVien = new NhanVienJdbcRepository();
        var sanPham = new SanPhamJdbcRepository();
        var nhaCungCap = new NhaCungCapJdbcRepository();
        var sales = new SalesJdbcRepository();
        var purchase = new PurchaseJdbcRepository();

        this.auth = new AuthService(nhanVien, new GmailOtpSender(), Clock.systemDefaultZone());
        this.sales = new SalesService(sanPham, sales);
        this.purchases = new PurchaseService(nhaCungCap, purchase);
        this.debts = new DebtService(nhaCungCap);
        this.reports = new ReportService(new DoanhThuJdbcRepository());
        this.products = new CatalogServices.ProductService(sanPham);
        this.suppliers = new CatalogServices.SupplierService(nhaCungCap);
        this.employees = new CatalogServices.EmployeeService(nhanVien);
    }
}
