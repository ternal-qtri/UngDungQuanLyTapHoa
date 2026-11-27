package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.AccessDeniedException;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.exception.InsufficientStockException;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.security.Session;
import com.example.apptaphoa.service.SalesService.CartLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalesServiceTest {
    private Fakes.Products products;
    private Fakes.Sales sales;
    private SalesService service;
    private NhanVien staff;

    @BeforeEach
    void setUp() {
        products = new Fakes.Products();
        products.insert(Fakes.product("Mì tôm", 5000, 5));     // mã 1
        products.insert(Fakes.product("Gạo 5kg", 150000, 1)); // mã 2
        sales = new Fakes.Sales(products);
        service = new SalesService(products, sales);
        staff = Fakes.user("NV002", "nhanvien", "Nhân viên", "x");
        Session.start(staff);
    }

    @AfterEach
    void tearDown() {
        Session.end();
    }

    @Test
    void checkoutComputesTotalAndDecrementsStock() {
        int maHD = service.checkout(List.of(new CartLine(1, 2)), "Tiền mặt");

        assertEquals(1, maHD);
        assertEquals(10000, sales.invoices.get(0).getTongTien(), 0.001);
        assertEquals(3, products.byId.get(1).getSoLuongTonKho());
    }

    @Test
    void outOfStockRejectsSaleAndKeepsStock() {
        assertThrows(InsufficientStockException.class,
                () -> service.checkout(List.of(new CartLine(1, 1), new CartLine(2, 2)), "Tiền mặt"));

        assertEquals(5, products.byId.get(1).getSoLuongTonKho());
        assertEquals(1, products.byId.get(2).getSoLuongTonKho());
        assertTrue(sales.invoices.isEmpty());
    }

    @Test
    void emptyCartIsRejected() {
        assertThrows(BusinessException.class, () -> service.checkout(List.of(), "Tiền mặt"));
    }

    @Test
    void zeroQuantityIsRejected() {
        assertThrows(BusinessException.class, () -> service.checkout(List.of(new CartLine(1, 0)), "Tiền mặt"));
    }

    @Test
    void checkoutRequiresLogin() {
        Session.end();
        assertThrows(AccessDeniedException.class, () -> service.checkout(List.of(new CartLine(1, 1)), "Tiền mặt"));
    }

    @Test
    void invoiceRecordsSellerAndPaymentMethod() {
        service.checkout(List.of(new CartLine(1, 1)), "Chuyển khoản");

        assertEquals("NV002", sales.invoices.get(0).getMaNV());
        assertEquals("Chuyển khoản", sales.invoices.get(0).getHinhThucTT());
    }
}
