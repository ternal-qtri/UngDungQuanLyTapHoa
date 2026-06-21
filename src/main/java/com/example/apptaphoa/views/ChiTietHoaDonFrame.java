package com.example.apptaphoa.views;

import com.example.apptaphoa.dao.ChiTietHoaDonDAO;
import com.example.apptaphoa.model.ChiTietHoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChiTietHoaDonFrame extends JFrame {
    private JTable tblChiTiet;
    private JLabel lblTongTien;
    private ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();

    public ChiTietHoaDonFrame(int maHD) {
        setTitle("Chi tiết hóa đơn #" + maHD);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        );
        tblChiTiet = new JTable(model);
        add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        lblTongTien = new JLabel("Tổng tiền: 0 đ", SwingConstants.RIGHT);
        add(lblTongTien, BorderLayout.SOUTH);

        loadData(maHD, model);
    }

    private void loadData(int maHD, DefaultTableModel model) {
        List<ChiTietHoaDon> list = chiTietHoaDonDAO.findByHoaDon(maHD);

        model.setRowCount(0);
        double tongTien = 0;

        for (ChiTietHoaDon ct : list) {
            model.addRow(new Object[]{
                    ct.getMaSanPham(),
                    ct.getTenSanPham(),
                    ct.getSoLuong(),
                    ct.getDonGia(),
                    ct.getThanhTien()
            });
            tongTien += ct.getThanhTien();
        }

        lblTongTien.setText("Tổng tiền: " + formatVND(tongTien));
    }

    private String formatVND(double v) {
        var nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        return nf.format(v) + " đ";
    }
}
