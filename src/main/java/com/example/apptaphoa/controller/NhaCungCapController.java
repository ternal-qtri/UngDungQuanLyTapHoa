package com.example.apptaphoa.controller;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.example.apptaphoa.dao.NhaCungCapDAO;
import com.example.apptaphoa.model.NhaCungCap;

import java.util.List;

public class NhaCungCapController {

    private NhaCungCapDAO dao;

    public NhaCungCapController() {
        dao = new NhaCungCapDAO();
    }

    // Thêm nhà cung cấp
    public void addNhaCungCap(JTextField txtTen, JTextField txtSDT, JTextField txtDiaChi, JTextField txtDuNo, JTable table) {
        try {
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            double duNo = Double.parseDouble(txtDuNo.getText().trim());

            if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            NhaCungCap ncc = new NhaCungCap();
            ncc.setHoTenNCC(ten);
            ncc.setSoDienThoai(sdt);
            ncc.setDiaChi(diaChi);
            ncc.setDuNo(duNo);

            dao.insert(ncc);

            JOptionPane.showMessageDialog(null, "Thêm nhà cung cấp thành công!");
            loadDataToTable(table);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Dư nợ phải là số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhà cung cấp: " + e.getMessage());
        }
    }

    // Sửa nhà cung cấp
    public void updateNhaCungCap(JTextField txtMa, JTextField txtTen, JTextField txtSDT, JTextField txtDiaChi, JTextField txtDuNo, JTable table) {
        try {
            int ma = Integer.parseInt(txtMa.getText().trim());
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            double duNo = Double.parseDouble(txtDuNo.getText().trim());

            if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            NhaCungCap ncc = new NhaCungCap();
            ncc.setMaNCC(ma);
            ncc.setHoTenNCC(ten);
            ncc.setSoDienThoai(sdt);
            ncc.setDiaChi(diaChi);
            ncc.setDuNo(duNo);

            dao.update(ncc);

            JOptionPane.showMessageDialog(null, "Cập nhật thành công!");
            loadDataToTable(table);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Mã nhà cung cấp và dư nợ phải là số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    // Xóa nhà cung cấp
    public void deleteNhaCungCap(String maStr, JTable table) {
        try {
            int ma = Integer.parseInt(maStr.trim());
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.delete(ma);
                JOptionPane.showMessageDialog(null, "Xóa thành công!");
                loadDataToTable(table);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Mã nhà cung cấp phải là số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    // Load dữ liệu lên bảng
    public void loadDataToTable(JTable table) {
        try {
            List<NhaCungCap> list = dao.selectAll();
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0); // Xóa dữ liệu cũ
            for (NhaCungCap ncc : list) {
                model.addRow(new Object[]{
                    ncc.getMaNCC(),
                    ncc.getHoTenNCC(),
                    ncc.getSoDienThoai(),
                    ncc.getDiaChi(),
                    ncc.getDuNo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
}
