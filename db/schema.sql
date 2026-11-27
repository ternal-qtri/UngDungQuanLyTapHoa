-- ============================================================
-- Schema: Ứng dụng Quản lý Tạp Hóa
-- Chạy file này trước seed.sql. Yêu cầu SQL Server 2019 trở lên.
-- ============================================================

IF DB_ID(N'AppTapHoa') IS NULL
    CREATE DATABASE AppTapHoa;
GO

USE AppTapHoa;
GO

-- Nhân viên và quản lý. VaiTro: 'Quản lý' hoặc 'Nhân viên'.
-- MatKhau lưu chuỗi băm PBKDF2 (xem security/PasswordHasher), không lưu plaintext.
CREATE TABLE NhanVien (
    MaNV         NVARCHAR(10)  NOT NULL PRIMARY KEY,
    HoTen        NVARCHAR(100) NOT NULL,
    TenDangNhap  NVARCHAR(50)  NOT NULL UNIQUE,
    MatKhau      NVARCHAR(200) NOT NULL,
    GioiTinh     NVARCHAR(10)  NULL,
    Email        NVARCHAR(100) NULL,
    VaiTro       NVARCHAR(20)  NOT NULL
        CONSTRAINT CK_NhanVien_VaiTro CHECK (VaiTro IN (N'Quản lý', N'Nhân viên'))
);
GO

CREATE TABLE SanPham (
    MaSanPham     INT           IDENTITY(1,1) PRIMARY KEY,
    TenSanPham    NVARCHAR(150) NOT NULL,
    DonGia        DECIMAL(18,2) NOT NULL CONSTRAINT CK_SanPham_DonGia CHECK (DonGia >= 0),
    SoLuongTonKho INT           NOT NULL CONSTRAINT DF_SanPham_Ton DEFAULT 0
                                CONSTRAINT CK_SanPham_Ton CHECK (SoLuongTonKho >= 0),
    LoaiSanPham   NVARCHAR(100) NULL
);
GO

CREATE TABLE NhaCungCap (
    MaNCC       INT           IDENTITY(1,1) PRIMARY KEY,
    HoTenNCC    NVARCHAR(150) NOT NULL,
    SoDienThoai NVARCHAR(20)  NULL,
    DiaChi      NVARCHAR(255) NULL,
    DuNo        DECIMAL(18,2) NOT NULL CONSTRAINT DF_NCC_DuNo DEFAULT 0
                              CONSTRAINT CK_NCC_DuNo CHECK (DuNo >= 0)
);
GO

-- Hóa đơn bán hàng
CREATE TABLE HoaDon (
    MaHD       INT           IDENTITY(1,1) PRIMARY KEY,
    ThoiGian   DATETIME2(0)  NOT NULL CONSTRAINT DF_HoaDon_ThoiGian DEFAULT SYSDATETIME(),
    TongTien   DECIMAL(18,2) NOT NULL CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
    HinhThucTT NVARCHAR(50)  NOT NULL,
    MaNV       NVARCHAR(10)  NOT NULL REFERENCES NhanVien(MaNV)
);
GO

CREATE TABLE ChiTietHoaDon (
    MaHD      INT           NOT NULL REFERENCES HoaDon(MaHD),
    MaSanPham INT           NOT NULL REFERENCES SanPham(MaSanPham),
    SoLuong   INT           NOT NULL CONSTRAINT CK_CTHD_SoLuong CHECK (SoLuong > 0),
    DonGia    DECIMAL(18,2) NOT NULL CONSTRAINT CK_CTHD_DonGia CHECK (DonGia >= 0),
    ThanhTien DECIMAL(18,2) NOT NULL CONSTRAINT CK_CTHD_ThanhTien CHECK (ThanhTien >= 0),
    PRIMARY KEY (MaHD, MaSanPham)
);
GO

-- Phiếu nhập hàng từ nhà cung cấp
CREATE TABLE PhieuNhapHang (
    MaPN        INT           IDENTITY(1,1) PRIMARY KEY,
    NgayNhap    DATETIME2(0)  NOT NULL CONSTRAINT DF_PN_NgayNhap DEFAULT SYSDATETIME(),
    TongTien    DECIMAL(18,2) NOT NULL CONSTRAINT CK_PN_TongTien CHECK (TongTien >= 0),
    DaThanhToan DECIMAL(18,2) NOT NULL CONSTRAINT DF_PN_DaTT DEFAULT 0
                              CONSTRAINT CK_PN_DaTT CHECK (DaThanhToan >= 0),
    MaNCC       INT           NOT NULL REFERENCES NhaCungCap(MaNCC),
    MaNV        NVARCHAR(10)  NOT NULL REFERENCES NhanVien(MaNV)
);
GO

CREATE TABLE ChiTietPhieuNhap (
    MaPN      INT           NOT NULL REFERENCES PhieuNhapHang(MaPN),
    MaSanPham INT           NOT NULL REFERENCES SanPham(MaSanPham),
    SoLuong   INT           NOT NULL CONSTRAINT CK_CTPN_SoLuong CHECK (SoLuong > 0),
    DonGiaNhap DECIMAL(18,2) NOT NULL CONSTRAINT CK_CTPN_DonGia CHECK (DonGiaNhap >= 0),
    ThanhTien DECIMAL(18,2) NOT NULL CONSTRAINT CK_CTPN_ThanhTien CHECK (ThanhTien >= 0),
    PRIMARY KEY (MaPN, MaSanPham)
);
GO

-- Công nợ nhà cung cấp theo từng khoản hàng
CREATE TABLE DuNoHang (
    MaDuNo   INT           IDENTITY(1,1) PRIMARY KEY,
    MaNCC    INT           NOT NULL REFERENCES NhaCungCap(MaNCC),
    SoTienNo DECIMAL(18,2) NOT NULL CONSTRAINT CK_DuNo_So CHECK (SoTienNo >= 0),
    NgayNo   DATE          NOT NULL CONSTRAINT DF_DuNo_Ngay DEFAULT CAST(SYSDATETIME() AS DATE),
    MatHang  NVARCHAR(150) NULL,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_DuNo_TT DEFAULT N'Còn nợ'
);
GO

-- Chỉ mục cho truy vấn theo ngày (báo cáo doanh thu, lịch sử hóa đơn)
CREATE INDEX IX_HoaDon_ThoiGian ON HoaDon(ThoiGian);
GO
