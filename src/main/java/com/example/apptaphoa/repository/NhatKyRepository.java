package com.example.apptaphoa.repository;

/** Ghi nhật ký thao tác. Chỉ ghi thêm, không sửa hay xóa. */
public interface NhatKyRepository {
    void insert(String maNV, String hanhDong, String chiTiet);
}
