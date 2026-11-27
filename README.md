# 🏪 Phần Mềm Quản Lý Tạp Hóa

Ứng dụng Java Desktop (Swing) quản lý cửa hàng tạp hóa: bán hàng, hóa đơn, sản phẩm, nhà cung cấp, nhập hàng, công nợ nhà cung cấp, nhân viên và báo cáo doanh thu. Đồ án sinh viên, làm theo nhóm.

## ✨ Tính năng

| Vai trò | Chức năng |
|---------|-----------|
| Tất cả | Đăng nhập, đổi mật khẩu, quên mật khẩu (OTP gửi qua email), bán hàng, xem hóa đơn, xuất hóa đơn PDF |
| Quản lý | Quản lý sản phẩm, nhà cung cấp (thêm, sửa, xóa), lập phiếu nhập hàng, trả công nợ, quản lý nhân viên, báo cáo doanh thu |
| Nhân viên | Chỉ xem và lập hóa đơn của chính mình |

Các thao tác quan trọng (đăng nhập, bán hàng, nhập hàng, trả nợ, đổi và đặt lại mật khẩu) được ghi vào bảng `NhatKy`.

## 🛠 Công nghệ

- Java 21, Maven
- Swing + [FlatLaf](https://www.formdev.com/flatlaf/) (giao diện)
- JDBC + SQL Server 2019 trở lên (`mssql-jdbc`)
- iText 5 (xuất PDF), javax.mail (gửi OTP qua Gmail)
- JUnit 5 (kiểm thử)

## 🏗 Kiến trúc

Phân tầng một chiều: giao diện → service → repository → SQL Server.

- **ui**: màn hình Swing. Không chứa SQL hay quy tắc nghiệp vụ.
- **service**: toàn bộ quy tắc nghiệp vụ, kiểm tra quyền, transaction.
- **repository**: truy cập dữ liệu bằng JDBC (interface và bản cài đặt).
- **security**: băm mật khẩu PBKDF2, phiên đăng nhập, vai trò, OTP.
- **config/AppContext**: nơi duy nhất khởi tạo repository và service.

Quyền được kiểm tra trong service (`Session.requireRole`), không chỉ ẩn nút trên giao diện.

## 🔐 Bảo mật

- Không có mật khẩu trong mã nguồn. Cấu hình đọc từ biến môi trường.
- Mật khẩu nhân viên được băm PBKDF2-HMAC-SHA256 (310.000 vòng, salt 16 byte).
- OTP quên mật khẩu: 6 chữ số ngẫu nhiên, hết hạn sau 5 phút, tối đa 5 lần nhập sai, chỉ dùng một lần.
- Thông báo đăng nhập sai không tiết lộ tài khoản nào tồn tại.
- Bán hàng và nhập hàng chạy trong transaction. Trừ tồn kho có điều kiện `SoLuongTonKho >= ?`, nên không bán vượt tồn kho.

## 🚀 Cài đặt và chạy

### Yêu cầu

- JDK 21
- Maven 3.9 trở lên
- SQL Server 2019 trở lên (có thể chạy bằng Docker)

### 1. Tạo cơ sở dữ liệu

Chạy lần lượt hai file SQL trong thư mục `db/` (SSMS, `sqlcmd` hoặc Azure Data Studio):

```bash
sqlcmd -S localhost -U sa -C -i db/schema.sql
sqlcmd -S localhost -U sa -C -i db/seed.sql
```

- `schema.sql` tạo database `AppTapHoa`, các bảng, ràng buộc và bảng `NhatKy`.
- `seed.sql` thêm tài khoản demo và dữ liệu mẫu. Chỉ chạy một lần trên DB mới.

**Đã có DB từ phiên bản trước?** Không chạy lại `schema.sql` (sẽ lỗi vì database đã tồn tại). Chỉ chạy khối `CREATE TABLE NhatKy` ở cuối `schema.sql` trên database `AppTapHoa`.

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

| Biến | Bắt buộc | Mô tả |
|------|----------|-------|
| `TAPHOA_DB_USER` | Có | Tài khoản SQL (ví dụ `tapho_app`) |
| `TAPHOA_DB_PASSWORD` | Có | Mật khẩu tài khoản SQL |
| `TAPHOA_DB_URL` | Không | Mặc định `jdbc:sqlserver://localhost:1433;databaseName=AppTapHoa;encrypt=true;trustServerCertificate=true;` |
| `TAPHOA_MAIL_USER` | Khi dùng quên mật khẩu | Email Gmail gửi OTP |
| `TAPHOA_MAIL_PASSWORD` | Khi dùng quên mật khẩu | Gmail App Password (không phải mật khẩu đăng nhập) |

Linux/macOS:

```bash
export TAPHOA_DB_USER=tapho_app
export TAPHOA_DB_PASSWORD='<mật-khẩu-mạnh>'
```

Có thể đặt các biến trong file `.env` ở thư mục gốc (file này đã nằm trong `.gitignore`). Ứng dụng không tự đọc `.env`, cần nạp trước khi chạy:

```bash
set -a; . ./.env; set +a
```

Khi chạy từ IDE, khai báo các biến trong cấu hình chạy (Run Configuration → Environment variables).

### 4. Chạy ứng dụng

```bash
mvn compile exec:java
```

Hoặc chạy lớp `com.example.apptaphoa.Main` từ IDE (nhớ khai báo biến môi trường).

### 5. Kiểm thử

```bash
mvn verify
```

Bộ test gồm: băm mật khẩu, đăng nhập và OTP, bán hàng (tồn kho, quyền truy cập hóa đơn), nhập hàng và công nợ, nhà cung cấp (kiểm tra số điện thoại, giữ nguyên công nợ khi sửa). Test dùng repository giả trong bộ nhớ, không cần SQL Server.

## 🔑 Tài khoản demo

| Tên đăng nhập | Mật khẩu | Vai trò |
|---------------|----------|---------|
| `quanly` | `Quanly@123` | Quản lý |
| `nhanvien` | `Nhanvien@123` | Nhân viên |

Chỉ dùng cho môi trường thử nghiệm. Đổi hoặc xóa trước khi đưa vào dùng thật.

## 🗄 Cơ sở dữ liệu

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
    NhatKy { bigint MaNhatKy PK, datetime ThoiGian, string MaNV, string HanhDong, string ChiTiet }
```

`NhatKy` không có khóa ngoại tới `NhanVien`, để giữ lại nhật ký khi đăng nhập sai hoặc khi xóa nhân viên.

## 📁 Cấu trúc thư mục

```
db/                          schema.sql, seed.sql
src/main/java/com/example/apptaphoa/
  Main.java                  điểm khởi chạy
  config/                    đọc cấu hình từ biến môi trường, AppContext
  ui/                        màn hình Swing (LoginScreen, MainWindow, screens/)
  ui/components/, ui/theme/  thành phần dùng chung, màu sắc và khoảng cách
  service/                   quy tắc nghiệp vụ, kiểm tra quyền, transaction
  repository/                truy cập dữ liệu JDBC
  security/                  băm mật khẩu, phiên đăng nhập, vai trò, OTP
  report/                    xuất PDF hóa đơn
  mail/                      gửi OTP qua Gmail
  model/, exception/         đối tượng dữ liệu và ngoại lệ nghiệp vụ
src/main/resources/fonts/    font nhúng cho PDF tiếng Việt
src/test/java/               kiểm thử JUnit 5
```

## 👥 Thành viên

- Nguyễn Quốc Trí: nhóm trưởng, lập trình viên. Phân tích yêu cầu, thiết kế cơ sở dữ liệu, đăng nhập và phân quyền, quản lý sản phẩm, hóa đơn và bán hàng.

## 📊 Kết quả

- Điểm dự án: **8.3/10**

## 🔗 Liên kết

https://github.com/ternal-qtri/UngDungQuanLyTapHoa
