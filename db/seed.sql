-- ============================================================
-- Dữ liệu mẫu cho môi trường demo.
-- Chạy SAU schema.sql, chỉ một lần trên DB mới.
--
-- Tài khoản demo (chỉ dùng để thử nghiệm):
--   quanly    / Quanly@123     (Quản lý)
--   nhanvien  / Nhanvien@123   (Nhân viên)
-- Mật khẩu được lưu dưới dạng băm PBKDF2, không phải plaintext.
-- ============================================================

USE AppTapHoa;
GO

INSERT INTO NhanVien (MaNV, HoTen, TenDangNhap, MatKhau, GioiTinh, Email, VaiTro) VALUES
(N'NV001', N'Nguyễn Quản Lý', N'quanly',
 N'pbkdf2$310000$KniATcCrHXqBnFyz43QNzA==$ME1x70e0uZtxfgzopRe3y7n6zalt3D9P6ilbbWW3Tb8=',
 N'Nam', N'quanly@example.com', N'Quản lý'),
(N'NV002', N'Trần Nhân Viên', N'nhanvien',
 N'pbkdf2$310000$eHeU7qEA+PR0cOz8jNdxwg==$VOChjnQnWvBzFUG1u1m1PUmsfMhno5R20FQmZr/Hqnc=',
 N'Nữ', N'nhanvien@example.com', N'Nhân viên');
GO

INSERT INTO SanPham (TenSanPham, DonGia, SoLuongTonKho, LoaiSanPham) VALUES
(N'Gạo ST25 túi 5kg',     150000, 40, N'Lương thực'),
(N'Mì tôm Hảo Hảo',         5000, 200, N'Lương thực'),
(N'Dầu ăn Neptune 1L',     52000, 30, N'Gia vị'),
(N'Nước mắm Phú Quốc 500ml', 45000, 25, N'Gia vị'),
(N'Sữa tươi Vinamilk 1L',  32000, 50, N'Sữa'),
(N'Bánh quy Cosy',         12000, 60, N'Bánh kẹo'),
(N'Nước suối Aquafina 500ml', 6000, 120, N'Đồ uống'),
(N'Bột giặt Omo 800g',     65000, 18, N'Gia dụng');
GO

INSERT INTO NhaCungCap (HoTenNCC, SoDienThoai, DiaChi, DuNo) VALUES
(N'Công ty Lương thực Miền Nam', N'0281234567', N'Quận 1, TP.HCM', 0),
(N'Đại lý Gia vị Hòa Phát',     N'0909000111', N'Bình Dương', 0);
GO
