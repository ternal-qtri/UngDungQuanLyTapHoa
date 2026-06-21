package com.example.apptaphoa.model;

public class ChiTietSP {
	private int maSP;
	private int soLuong;
	private double donGia;

	public ChiTietSP() {
	}

	public ChiTietSP(int maSP, int soLuong, double donGia) {
		this.maSP = maSP;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}

	public int getMaSP() {
		return maSP;
	}

	public void setMaSP(int maSP) {
		this.maSP = maSP;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(Double donGia) {
		this.donGia = donGia;
	}
}