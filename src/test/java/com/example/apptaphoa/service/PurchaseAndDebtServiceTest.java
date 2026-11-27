package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.AccessDeniedException;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.security.Session;
import com.example.apptaphoa.service.PurchaseService.PurchaseLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseAndDebtServiceTest {
    private Fakes.Products products;
    private Fakes.Suppliers suppliers;
    private Fakes.Purchases purchases;
    private PurchaseService purchaseService;
    private DebtService debtService;

    @BeforeEach
    void setUp() {
        products = new Fakes.Products();
        products.insert(Fakes.product("Mì tôm", 5000, 0));   // mã 1
        products.insert(Fakes.product("Dầu ăn", 50000, 0));  // mã 2
        suppliers = new Fakes.Suppliers();
        NhaCungCap ncc = new NhaCungCap();
        ncc.setHoTenNCC("NCC A");
        suppliers.insert(ncc); // mã 1, công nợ 0
        purchases = new Fakes.Purchases(products, suppliers);
        AuditService audit = new AuditService(new Fakes.NhatKy());
        purchaseService = new PurchaseService(suppliers, purchases, audit);
        debtService = new DebtService(suppliers, audit);
    }

    @AfterEach
    void tearDown() {
        Session.end();
    }

    @Test
    void staffCannotCreateReceipt() {
        Session.start(Fakes.user("NV002", "nhanvien", "Nhân viên", "x"));
        assertThrows(AccessDeniedException.class,
                () -> purchaseService.createReceipt(1, 0, List.of(new PurchaseLine(1, 1, 1000))));
    }

    @Test
    void receiptAddsStockAndAddsUnpaidPartToDebt() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        // 10 * 5000 + 3 * 50000 = 200000; trả 50000 -> còn nợ 150000
        purchaseService.createReceipt(1, 50000,
                List.of(new PurchaseLine(1, 10, 5000), new PurchaseLine(2, 3, 50000)));

        assertEquals(10, products.byId.get(1).getSoLuongTonKho());
        assertEquals(3, products.byId.get(2).getSoLuongTonKho());
        assertEquals(150000, suppliers.byId.get(1).getDuNo(), 0.001);
    }

    @Test
    void debtAccumulatesAcrossReceipts() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        purchaseService.createReceipt(1, 0, List.of(new PurchaseLine(1, 1, 10000)));
        purchaseService.createReceipt(1, 0, List.of(new PurchaseLine(1, 1, 20000)));

        assertEquals(30000, suppliers.byId.get(1).getDuNo(), 0.001);
    }

    @Test
    void paidMoreThanTotalIsRejected() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        assertThrows(BusinessException.class,
                () -> purchaseService.createReceipt(1, 20000, List.of(new PurchaseLine(1, 1, 10000))));
    }

    @Test
    void receiptNeedsAtLeastOneLine() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        assertThrows(BusinessException.class, () -> purchaseService.createReceipt(1, 0, List.of()));
    }

    @Test
    void managerCanPayDebtUpToAmountOwed() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        suppliers.byId.get(1).setDuNo(50000);

        debtService.payDebt(1, 20000);

        assertEquals(30000, suppliers.byId.get(1).getDuNo(), 0.001);
    }

    @Test
    void payingMoreThanOwedIsRejected() {
        Session.start(Fakes.user("QL001", "quanly", "Quản lý", "x"));
        suppliers.byId.get(1).setDuNo(50000);

        assertThrows(BusinessException.class, () -> debtService.payDebt(1, 60000));
        assertEquals(50000, suppliers.byId.get(1).getDuNo(), 0.001);
    }

    @Test
    void staffCannotPayDebt() {
        Session.start(Fakes.user("NV002", "nhanvien", "Nhân viên", "x"));
        suppliers.byId.get(1).setDuNo(50000);

        assertThrows(AccessDeniedException.class, () -> debtService.payDebt(1, 10000));
    }
}
