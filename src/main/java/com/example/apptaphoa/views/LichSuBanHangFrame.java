package com.example.apptaphoa.views;

import com.example.apptaphoa.dao.HoaDonDAO;
import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.utils.AuthUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class LichSuBanHangFrame extends JFrame {
    private JTable tblHoaDon;
    private JLabel lblTongDon, lblTongTien;
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public LichSuBanHangFrame() {
        setTitle("Lịch sử bán hàng hôm nay");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // === Bảng hóa đơn ===
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Mã HD", "Thời gian", "Tổng tiền", "Thanh toán"}, 0
        );
        tblHoaDon = new JTable(model);
        add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // === Panel hiển thị tổng kết ===
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongDon = new JLabel("Tổng đơn: 0");
        lblTongTien = new JLabel("Tổng tiền: 0 đ");
        panelBottom.add(lblTongDon);
        panelBottom.add(Box.createHorizontalStrut(20));
        panelBottom.add(lblTongTien);
        add(panelBottom, BorderLayout.SOUTH);

        // Thêm sự kiện double click
        tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblHoaDon.getSelectedRow();
                    if (row >= 0) {
                        int maHD = (int) tblHoaDon.getValueAt(row, 0);
                        new ChiTietHoaDonFrame(maHD).setVisible(true);
                    }
                }
            }
        });

        loadData(model);
    }

    private void loadData(DefaultTableModel model) {
        var user = AuthUtil.getUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập lại!");
            dispose();
            return;
        }

        List<HoaDon> list = hoaDonDAO.findTodayByNhanVien(user.getMaNV());

        model.setRowCount(0);
        double tongTien = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (HoaDon hd : list) {
            model.addRow(new Object[]{
                    hd.getMaHD(),
                    sdf.format(hd.getThoiGian()),
                    hd.getTongTien(),
                    hd.getHinhThucTT()
            });
            tongTien += hd.getTongTien();
        }

        lblTongDon.setText("Tổng đơn: " + list.size());
        lblTongTien.setText("Tổng tiền: " + formatVND(tongTien));
    }

    private String formatVND(double v) {
        var nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        return nf.format(v) + " đ";
    }
}
