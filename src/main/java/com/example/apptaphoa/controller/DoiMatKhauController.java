package com.example.apptaphoa.controller;

import com.example.apptaphoa.dao.NhanVienDAO;
import com.example.apptaphoa.model.NhanVien;

public class DoiMatKhauController {
	NhanVienDAO dao = new NhanVienDAO();

	public boolean checkEmail(String tenDangNhap, String email) {
		return dao.checkEmail(tenDangNhap, email);
	}

	public boolean resetPass(String tenDangNhap, String matKhauMoi, String email) {
		return dao.resetPass(tenDangNhap, matKhauMoi, email);
	}
}
