package com.example.apptaphoa.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.example.apptaphoa.dao.ChiTietPhieuNhapDAO;
import com.example.apptaphoa.model.ChiTietPhieuNhap;

public class ChiTietPhieuNhapFrame extends JFrame {
    private JTable tblChiTiet;

    public ChiTietPhieuNhapFrame(int maPN) {
        setTitle("Chi tiết Phiếu Nhập - Mã PN: " + maPN);
        setSize(600, 400);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Mã SP", "Số lượng", "Đơn giá nhập", "Thành tiền"}
        );
        tblChiTiet = new JTable(model);
        add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        loadData(maPN);
    }

    private void loadData(int maPN) {
        ChiTietPhieuNhapDAO dao = new ChiTietPhieuNhapDAO();
        List<ChiTietPhieuNhap> list = dao.findByMaPN(maPN);
        DefaultTableModel model = (DefaultTableModel) tblChiTiet.getModel();
        model.setRowCount(0);
        for (ChiTietPhieuNhap ct : list) {
            model.addRow(new Object[]{
                ct.getMaSanPham(), ct.getSoLuong(),
                ct.getDonGiaNhap(), ct.getThanhTien()
            });
        }
    }
}
