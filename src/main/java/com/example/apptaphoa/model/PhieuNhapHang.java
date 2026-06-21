package com.example.apptaphoa.model;

import java.util.Date;

public class PhieuNhapHang {
	private int maPN;
	private Date ngayNhap;
	private double tongTien;
	private double daThanhToan;
	private int maNCC;
	private String maNV;

	public PhieuNhapHang() {
		// TODO Auto-generated constructor stub
	}

	public int getMaPN() {
		return maPN;
	}

	public void setMaPN(int maPN) {
		this.maPN = maPN;
	}

	public Date getNgayNhap() {
		return ngayNhap;
	}

	public void setNgayNhap(Date ngayNhap) {
		this.ngayNhap = ngayNhap;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public double getDaThanhToan() {
		return daThanhToan;
	}

	public void setDaThanhToan(double daThanhToan) {
		this.daThanhToan = daThanhToan;
	}

	public int getMaNCC() {
		return maNCC;
	}

	public void setMaNCC(int maNCC) {
		this.maNCC = maNCC;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

}
