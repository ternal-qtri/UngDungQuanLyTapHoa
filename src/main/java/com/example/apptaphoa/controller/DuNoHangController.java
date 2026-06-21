package com.example.apptaphoa.controller;

import com.example.apptaphoa.dao.DuNoHangDAO;
import com.example.apptaphoa.model.DuNoHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;

public class DuNoHangController {

    private DuNoHangDAO dao;

    public DuNoHangController(Connection conn) {
        dao = new DuNoHangDAO();
    }

    // Lấy danh sách dư nợ
    public List<DuNoHang> getDanhSachDuNo() {
        return dao.getAll();
    }

    // Load dữ liệu lên bảng
    public void loadDataToTable(JTable table) {
        List<DuNoHang> list = dao.getAll();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (DuNoHang d : list) {
            model.addRow(new Object[]{
                d.getMaDuNo(),
                d.getMaNCC(),
                d.getSoTienNo(),
                d.getNgayNo(),
                d.getMatHang(),
                d.getTrangThai()
            });
        }
    }

    // Thêm dư nợ
    public void addDuNo(DuNoHang d, JTable table) {
        if (dao.insert(d)) {
            JOptionPane.showMessageDialog(null, "Thêm dư nợ thành công!");
            loadDataToTable(table);
        } else {
            JOptionPane.showMessageDialog(null, "Thêm dư nợ thất bại!");
        }
    }

    // Sửa dư nợ
    public void updateDuNo(DuNoHang d, JTable table) {
        if (dao.update(d)) {
            JOptionPane.showMessageDialog(null, "Cập nhật dư nợ thành công!");
            loadDataToTable(table);
        } else {
            JOptionPane.showMessageDialog(null, "Cập nhật dư nợ thất bại!");
        }
    }

    // Xóa dư nợ
    public void deleteDuNo(int maDuNo, JTable table) {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa dư nợ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(maDuNo)) {
                JOptionPane.showMessageDialog(null, "Xóa dư nợ thành công!");
                loadDataToTable(table);
            } else {
                JOptionPane.showMessageDialog(null, "Xóa dư nợ thất bại!");
            }
        }
    }

    // Thanh toán dư nợ
    public void thanhToanDuNo(int maDuNo, double soTienThanhToan, JTable table) {
        DuNoHang duNo = dao.getById(maDuNo); // Lấy dư nợ hiện tại
        if (duNo == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy dư nợ!");
            return;
        }

        double soNoConLai = duNo.getSoTienNo() - soTienThanhToan;
        if (soTienThanhToan <= 0) {
            JOptionPane.showMessageDialog(null, "Số tiền thanh toán phải lớn hơn 0!");
            return;
        }
        if (soNoConLai < 0) {
            JOptionPane.showMessageDialog(null, "Số tiền thanh toán vượt quá số nợ!");
            return;
        }

        // Cập nhật số nợ
        duNo.setSoTienNo(soNoConLai);

        // Nếu hết nợ thì đổi trạng thái thành "Đã thanh toán"
        if (soNoConLai == 0) {
            duNo.setTrangThai("Đã thanh toán");
        } else {
            duNo.setTrangThai("Còn nợ");
        }

        // Cập nhật vào DB
        if (dao.update(duNo)) {
            JOptionPane.showMessageDialog(null, "Thanh toán thành công!");
            loadDataToTable(table);
        } else {
            JOptionPane.showMessageDialog(null, "Thanh toán thất bại!");
        }
    }
}
