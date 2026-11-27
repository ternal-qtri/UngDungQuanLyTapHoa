package com.example.apptaphoa.service;

import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.repository.NhatKyRepository;
import com.example.apptaphoa.security.Session;

/**
 * Ghi nhật ký thao tác quan trọng. Người thực hiện lấy từ phiên đăng nhập, "-" nếu chưa đăng nhập.
 * Lỗi ghi nhật ký không làm hỏng thao tác nghiệp vụ đã thành công; chỉ in ra lỗi.
 */
public class AuditService {
    private static final int MAX_DETAIL = 400;

    private final NhatKyRepository log;

    public AuditService(NhatKyRepository log) {
        this.log = log;
    }

    public void record(String hanhDong, String chiTiet) {
        String maNV = Session.current().map(NhanVien::getMaNV).orElse("-");
        String detail = chiTiet == null ? null
                : chiTiet.length() > MAX_DETAIL ? chiTiet.substring(0, MAX_DETAIL) : chiTiet;
        try {
            log.insert(maNV, hanhDong, detail);
        } catch (RuntimeException e) {
            System.err.println("Không ghi được nhật ký " + hanhDong + ": " + e.getMessage());
        }
    }
}
