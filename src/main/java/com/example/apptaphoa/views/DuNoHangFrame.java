package com.example.apptaphoa.views;

import com.example.apptaphoa.controller.DuNoHangController;
import com.example.apptaphoa.model.DuNoHang;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.utils.JDBCUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuNoHangFrame extends JFrame {
	private JTextField txtMaDuNo, txtMaNCC, txtSoTienNo, txtNgayNo, txtMatHang, txtTrangThai;
	private JTable table;
	private DuNoHangController controller;

	public DuNoHangFrame() {
		setTitle("Quản Lý Dư Nợ Hàng");
		setSize(950, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		// Khởi tạo controller
		controller = new DuNoHangController(JDBCUtil.getConnection());

		JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
		panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin dư nợ"));

		txtMaDuNo = new JTextField();
		txtMaNCC = new JTextField();
		txtSoTienNo = new JTextField();
		txtNgayNo = new JTextField();
		txtMatHang = new JTextField();
		txtTrangThai = new JTextField();

		panelForm.add(new JLabel("Mã Dư Nợ:"));
		panelForm.add(txtMaDuNo);
		panelForm.add(new JLabel("Mã NCC:"));
		panelForm.add(txtMaNCC);
		panelForm.add(new JLabel("Số Tiền Nợ:"));
		panelForm.add(txtSoTienNo);
		panelForm.add(new JLabel("Ngày Nợ:"));
		panelForm.add(txtNgayNo);
		panelForm.add(new JLabel("Mặt Hàng:"));
		panelForm.add(txtMatHang);
		panelForm.add(new JLabel("Trạng Thái:"));
		panelForm.add(txtTrangThai);

		// Bảng
		table = new JTable(new DefaultTableModel(
				new Object[] { "Mã Dư Nợ", "Mã NCC", "Số Tiền Nợ", "Ngày Nợ", "Mặt Hàng", "Trạng Thái" }, 0));
		JScrollPane scrollPane = new JScrollPane(table);

		// Panel nút
		JPanel panelButtons = new JPanel();

		JButton btnAdd = new JButton("Thêm");
		btnAdd.addActionListener(e -> controller.addDuNo(getDuNoFromForm(), table));

		JButton btnUpdate = new JButton("Sửa");
		btnUpdate.addActionListener(e -> controller.updateDuNo(getDuNoFromForm(), table));

		JButton btnDelete = new JButton("Xóa");
		btnDelete.addActionListener(e -> {
			try {
				int maDuNo = Integer.parseInt(txtMaDuNo.getText().trim());
				controller.deleteDuNo(maDuNo, table);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi hợp lệ!");
			}
		});

		JButton btnClear = new JButton("Làm mới");
		btnClear.addActionListener(e -> clearForm());

		// Nút Thanh Toán
		JButton btnPay = new JButton("Thanh Toán");
		btnPay.addActionListener(e -> {
			try {
				int maDuNo = Integer.parseInt(txtMaDuNo.getText().trim());
				String input = JOptionPane.showInputDialog(this, "Nhập số tiền thanh toán:");
				if (input != null && !input.trim().isEmpty()) {
					double soTienTra = Double.parseDouble(input.trim());
					controller.thanhToanDuNo(maDuNo, soTienTra, table);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
			}
		});

		panelButtons.add(btnAdd);
		panelButtons.add(btnUpdate);
		panelButtons.add(btnDelete);
		panelButtons.add(btnClear);
		panelButtons.add(btnPay);

		// Sự kiện click vào bảng
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					txtMaDuNo.setText(table.getValueAt(row, 0).toString());
					txtMaNCC.setText(table.getValueAt(row, 1).toString());
					txtSoTienNo.setText(table.getValueAt(row, 2).toString());
					txtNgayNo.setText(table.getValueAt(row, 3).toString());
					txtMatHang.setText(table.getValueAt(row, 4).toString());
					txtTrangThai.setText(table.getValueAt(row, 5).toString());
				}
			}
		});

		setLayout(new BorderLayout());
		add(panelForm, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(panelButtons, BorderLayout.SOUTH);

		controller.loadDataToTable(table); // Load dữ liệu từ controller
	}

	private DuNoHang getDuNoFromForm() {
		DuNoHang d = new DuNoHang();
		d.setMaDuNo(Integer.parseInt(txtMaDuNo.getText().trim()));
		d.setMaNCC(Integer.parseInt(txtMaNCC.getText().trim()));
		d.setSoTienNo(Double.parseDouble(txtSoTienNo.getText().trim()));
		d.setNgayNo(txtNgayNo.getText().trim());
		d.setMatHang(txtMatHang.getText().trim());
		d.setTrangThai(txtTrangThai.getText().trim());
		return d;
	}

	private void clearForm() {
		txtMaDuNo.setText("");
		txtMaNCC.setText("");
		txtSoTienNo.setText("");
		txtNgayNo.setText("");
		txtMatHang.setText("");
		txtTrangThai.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new DuNoHangFrame().setVisible(true));
	}
}
