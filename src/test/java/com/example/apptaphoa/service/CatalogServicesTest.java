package com.example.apptaphoa.service;

import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.security.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogServicesTest {
    private Fakes.Suppliers suppliers;
    private CatalogServices.SupplierService service;

    @BeforeEach
    void setUp() {
        suppliers = new Fakes.Suppliers();
        service = new CatalogServices.SupplierService(suppliers);
        Session.start(Fakes.user("NV001", "quanly", "Quản lý", "x"));
    }

    @AfterEach
    void tearDown() {
        Session.end();
    }

    @Test
    void rejectsMalformedPhone() {
        assertThrows(BusinessException.class, () -> service.create("Công ty A", "abc", ""));
    }

    @Test
    void blankPhoneIsStoredAsNull() {
        int id = service.create("Công ty A", "  ", "Hà Nội");
        assertNull(suppliers.findById(id).orElseThrow().getSoDienThoai());
    }

    @Test
    void updateKeepsDebt() {
        int id = service.create("Công ty A", "0912345678", "Hà Nội");
        suppliers.findById(id).orElseThrow().setDuNo(500);

        service.update(id, "Công ty B", "0987654321", "Đà Nẵng");

        NhaCungCap ncc = suppliers.findById(id).orElseThrow();
        assertEquals("Công ty B", ncc.getHoTenNCC());
        assertEquals("Đà Nẵng", ncc.getDiaChi());
        assertEquals(500, ncc.getDuNo(), 0.001);
    }
}
