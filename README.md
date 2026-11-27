# 🏪 Phần Mềm Quản Lý Tạp Hóa

Ứng dụng Java Desktop (Swing) quản lý cửa hàng tạp hóa: sản phẩm, nhà cung cấp, phiếu nhập, bán hàng, hóa đơn, công nợ, nhân viên và báo cáo doanh thu. Đồ án sinh viên, làm theo nhóm.

## 🛠 Công nghệ

- Java 21, Maven
- Swing + [FlatLaf](https://www.formdev.com/flatlaf/) (giao diện)
- JDBC + SQL Server 2019 trở lên (`mssql-jdbc`)
- iText 5 (xuất PDF)
- JUnit 5 (kiểm thử)

## 🚀 Cài đặt và chạy

### Yêu cầu

- JDK 21
- Maven 3.9 trở lên
- SQL Server 2019 trở lên (có thể chạy bằng Docker, xem bên dưới)

### 1. Tạo cơ sở dữ liệu

Chạy lần lượt hai file SQL trong thư mục `db/` (SSMS, `sqlcmd` hoặc Azure Data Studio):

```bash
sqlcmd -S localhost -U sa -C -i db/schema.sql
sqlcmd -S localhost -U sa -C -i db/seed.sql
```

- `schema.sql` tạo database `AppTapHoa` và các bảng.
- `seed.sql` thêm tài khoản demo và dữ liệu mẫu. Chạy một lần trên DB mới.

### 2. Tạo tài khoản SQL cho ứng dụng

Không dùng `sa` trong ứng dụng. Tạo login có quyền tối thiểu:

```sql
CREATE LOGIN tapho_app WITH PASSWORD = N'<mật-khẩu-mạnh>', CHECK_POLICY = ON;
USE AppTapHoa;
CREATE USER tapho_app FOR LOGIN tapho_app;
ALTER ROLE db_datareader ADD MEMBER tapho_app;
ALTER ROLE db_datawriter ADD MEMBER tapho_app;
```

### 3. Cấu hình biến môi trường

Ứng dụng không chứa mật khẩu trong mã nguồn. Đặt các biến sau trước khi chạy:

| Biến | Bắt buộc | Mô tả |
|------|----------|-------|
| `TAPHOA_DB_USER` | Có | Tài khoản SQL (ví dụ `tapho_app`) |
| `TAPHOA_DB_PASSWORD` | Có | Mật khẩu tài khoản SQL |
| `TAPHOA_DB_URL` | Không | Mặc định `jdbc:sqlserver://localhost:1433;databaseName=AppTapHoa;...` |
| `TAPHOA_MAIL_USER` | Có (khi dùng quên mật khẩu) | Email Gmail gửi OTP |
| `TAPHOA_MAIL_PASSWORD` | Có (khi dùng quên mật khẩu) | Gmail App Password |

Ví dụ (Linux/macOS):

```bash
export TAPHOA_DB_USER=tapho_app
export TAPHOA_DB_PASSWORD='<mật-khẩu-mạnh>'
```

### 4. Chạy ứng dụng

```bash
mvn compile exec:java
```

Hoặc chạy lớp `com.example.apptaphoa.Main` từ IDE (nhớ đặt biến môi trường trong cấu hình chạy).

### 5. Kiểm thử

```bash
mvn verify
```

## 🔑 Tài khoản demo

| Tên đăng nhập | Mật khẩu | Vai trò |
|---------------|----------|---------|
| `quanly` | `Quanly@123` | Quản lý |
| `nhanvien` | `Nhanvien@123` | Nhân viên |

Chỉ dùng cho môi trường thử nghiệm. Đổi hoặc xóa trước khi đưa vào dùng thật.

## 🗄 Cơ sở dữ liệu

Sơ đồ quan hệ (rút gọn):

```mermaid
erDiagram
    NhanVien ||--o{ HoaDon : "lập"
    HoaDon ||--|{ ChiTietHoaDon : "gồm"
    SanPham ||--o{ ChiTietHoaDon : "được bán trong"
    NhanVien ||--o{ PhieuNhapHang : "lập"
    NhaCungCap ||--o{ PhieuNhapHang : "cung cấp"
    PhieuNhapHang ||--|{ ChiTietPhieuNhap : "gồm"
    SanPham ||--o{ ChiTietPhieuNhap : "được nhập trong"
    NhaCungCap ||--o{ DuNoHang : "có khoản nợ"

    NhanVien { string MaNV PK, string TenDangNhap, string MatKhau, string VaiTro }
    SanPham { int MaSanPham PK, string TenSanPham, decimal DonGia, int SoLuongTonKho }
    NhaCungCap { int MaNCC PK, string HoTenNCC, decimal DuNo }
    HoaDon { int MaHD PK, datetime ThoiGian, decimal TongTien, string MaNV FK }
    ChiTietHoaDon { int MaHD PK,FK, int MaSanPham PK,FK, int SoLuong, decimal DonGia }
    PhieuNhapHang { int MaPN PK, datetime NgayNhap, decimal TongTien, int MaNCC FK, string MaNV FK }
    ChiTietPhieuNhap { int MaPN PK,FK, int MaSanPham PK,FK, int SoLuong, decimal DonGiaNhap }
    DuNoHang { int MaDuNo PK, int MaNCC FK, decimal SoTienNo, date NgayNo, string TrangThai }
```

## 📁 Cấu trúc thư mục

```
db/                 schema.sql, seed.sql
src/main/java/com/example/apptaphoa/
  config/           cấu hình đọc từ biến môi trường
  repository/       truy cập dữ liệu (JDBC)
  security/         băm mật khẩu PBKDF2
  controller/, views/, dao/, utils/   mã Swing cũ (sẽ được thay dần)
src/test/java/      kiểm thử JUnit 5
```

## 👥 Thành viên

- Nguyễn Quốc Trí: nhóm trưởng, lập trình viên. Phân tích yêu cầu, thiết kế cơ sở dữ liệu, đăng nhập và phân quyền, quản lý sản phẩm, hóa đơn và bán hàng.

## 📊 Kết quả

- Điểm dự án: **8.3/10** (bản đầu)

## 🔗 Liên kết

https://github.com/ternal-qtri/UngDungQuanLyTapHoa
