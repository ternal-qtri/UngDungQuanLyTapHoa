package com.example.apptaphoa.model;

public class DuNoHang {
    private int maDuNo;
    private int maNCC;
    private double soTienNo;
    private String ngayNo;
    private String matHang;
    private String trangThai;
    private NhaCungCap nhaCungCap;

    public DuNoHang() {
    }

    public DuNoHang(int maDuNo, int maNCC, double soTienNo, String ngayNo, String matHang, String trangThai) {
        this.maDuNo = maDuNo;
        this.maNCC = maNCC;
        this.soTienNo = soTienNo;
        this.ngayNo = ngayNo;
        this.matHang = matHang;
        this.trangThai = trangThai;
    }

    public int getMaDuNo() {
        return maDuNo;
    }

    public void setMaDuNo(int maDuNo) {
        this.maDuNo = maDuNo;
    }

    public int getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }

    public double getSoTienNo() {
        return soTienNo;
    }

    public void setSoTienNo(double soTienNo) {
        this.soTienNo = soTienNo;
    }

    public String getNgayNo() {
        return ngayNo;
    }

    public void setNgayNo(String ngayNo) {
        this.ngayNo = ngayNo;
    }

    public String getMatHang() {
        return matHang;
    }

    public void setMatHang(String matHang) {
        this.matHang = matHang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    // Hàm thanh toán dư nợ
    public boolean thanhToan(double soTienThanhToan) {
        if (soTienThanhToan <= 0 || soTienThanhToan > this.soTienNo) {
            return false;
        }
        this.soTienNo -= soTienThanhToan;
        this.trangThai = (this.soTienNo == 0) ? "Đã thanh toán" : "Còn nợ";
        return true;
    }

    @Override
    public String toString() {
        return "DuNoHang{" +
                "maDuNo=" + maDuNo +
                ", maNCC=" + maNCC +
                ", soTienNo=" + soTienNo +
                ", ngayNo='" + ngayNo + '\'' +
                ", matHang='" + matHang + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", nhaCungCap=" + (nhaCungCap != null ? nhaCungCap.getHoTenNCC() : "null") +
                '}';
    }
}
