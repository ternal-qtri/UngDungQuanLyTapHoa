package com.example.apptaphoa.views;

import com.example.apptaphoa.controller.NhaCungCapController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NhaCungCapFrame extends JFrame {

	private JTextField txtMa, txtTen, txtSDT, txtDiaChi, txtDuNo;
	private JTable table;
	private NhaCungCapController controller;

	public NhaCungCapFrame() {
		controller = new NhaCungCapController();
		initUI();
		controller.loadDataToTable(table);
	}

	private void initUI() {
		setTitle("Quản Lý Nhà Cung Cấp");
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// Panel nhập liệu
		JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));

		panelForm.add(new JLabel("Mã NCC:"));
		txtMa = new JTextField();
		txtMa.setEditable(false); // Auto tăng
		panelForm.add(txtMa);

		panelForm.add(new JLabel("Tên NCC:"));
		txtTen = new JTextField();
		panelForm.add(txtTen);

		panelForm.add(new JLabel("SĐT:"));
		txtSDT = new JTextField();
		panelForm.add(txtSDT);

		panelForm.add(new JLabel("Địa chỉ:"));
		txtDiaChi = new JTextField();
		panelForm.add(txtDiaChi);

		panelForm.add(new JLabel("Dư nợ:"));
		txtDuNo = new JTextField("0");
		panelForm.add(txtDuNo);

		add(panelForm, BorderLayout.NORTH);

		// Bảng hiển thị
		String[] columns = { "Mã NCC", "Tên NCC", "SĐT", "Địa chỉ", "Dư nợ" };
		table = new JTable(new DefaultTableModel(columns, 0));
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		// Panel nút bấm
		JPanel panelButtons = new JPanel();

		JButton btnAdd = new JButton("Thêm");
		btnAdd.addActionListener(e -> controller.addNhaCungCap(txtTen, txtSDT, txtDiaChi, txtDuNo, table));
		panelButtons.add(btnAdd);

		JButton btnUpdate = new JButton("Sửa");
		btnUpdate.addActionListener(e -> controller.updateNhaCungCap(txtMa, txtTen, txtSDT, txtDiaChi, txtDuNo, table));
		panelButtons.add(btnUpdate);

		JButton btnDelete = new JButton("Xóa");
		btnDelete.addActionListener(e -> controller.deleteNhaCungCap(txtMa.getText(), table));
		panelButtons.add(btnDelete);

		JButton btnRefresh = new JButton("Làm mới");
		btnRefresh.addActionListener(e -> {
			clearForm();
			controller.loadDataToTable(table);
		});
		panelButtons.add(btnRefresh);

		add(panelButtons, BorderLayout.SOUTH);

		// Sự kiện click vào bảng để chọn NCC
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					txtMa.setText(table.getValueAt(row, 0).toString());
					txtTen.setText(table.getValueAt(row, 1).toString());
					txtSDT.setText(table.getValueAt(row, 2).toString());
					txtDiaChi.setText(table.getValueAt(row, 3).toString());
					txtDuNo.setText(table.getValueAt(row, 4).toString());
				}
			}
		});
	}

	private void clearForm() {
		txtMa.setText("");
		txtTen.setText("");
		txtSDT.setText("");
		txtDiaChi.setText("");
		txtDuNo.setText("0");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new NhaCungCapFrame().setVisible(true));
	}
}
