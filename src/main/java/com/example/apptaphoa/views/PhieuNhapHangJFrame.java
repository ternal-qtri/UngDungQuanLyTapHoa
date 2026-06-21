package com.example.apptaphoa.views;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;

import com.example.apptaphoa.controller.PhieuNhapHangController;
import com.example.apptaphoa.dao.SanPhamDao_;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.model.PhieuNhapHang;
import com.example.apptaphoa.model.SanPham;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.event.ActionEvent;

import com.example.apptaphoa.dao.PhieuNhapHangDAO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PhieuNhapHangJFrame extends JFrame implements PhieuNhapHangController<PhieuNhapHang> {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblPhieuNhap;
	private JTextField txtMaPN;
	private JTextField txtNgayNhap;
	private JTextField txtTongTien;
	private JTextField txtDaThanhToan;
	private JTextField txtMaNhaCungCap;
	private JTextField txtMaNV;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhieuNhapHangJFrame frame = new PhieuNhapHangJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PhieuNhapHangJFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 862, 791);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setUndecorated(true);
		setLocationRelativeTo(null);

		JLabel lblNewLabel = new JLabel("PHIẾU NHẬP HÀNG");
		lblNewLabel.setForeground(new Color(0, 191, 255));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(285, 10, 309, 64);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		scrollPane.setBounds(10, 443, 828, 301);
		contentPane.add(scrollPane);

		tblPhieuNhap = new JTable();
		tblPhieuNhap.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            int row = tblPhieuNhap.getSelectedRow();
		            if (row >= 0) {
		                // 1. Đưa dữ liệu lên form để edit
		                edit();

		                // 2. Mở chi tiết phiếu nhập
		                int maPN = (int) tblPhieuNhap.getValueAt(row, 0);
		                new ChiTietPhieuNhapFrame(maPN).setVisible(true);
		            }
		        }
		    }
		});

		scrollPane.setViewportView(tblPhieuNhap);

		txtMaPN = new JTextField();
		txtMaPN.setBounds(153, 117, 139, 19);
		contentPane.add(txtMaPN);
		txtMaPN.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Mã Phiếu Nhập:");
		lblNewLabel_1.setBounds(10, 120, 111, 16);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Ngày Nhập:");
		lblNewLabel_1_1.setBounds(10, 169, 111, 16);
		contentPane.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("Tổng Tiền:");
		lblNewLabel_1_2.setBounds(10, 226, 111, 16);
		contentPane.add(lblNewLabel_1_2);

		JLabel lblNewLabel_1_3 = new JLabel("Đã Thanh Toán:");
		lblNewLabel_1_3.setBounds(10, 279, 111, 16);
		contentPane.add(lblNewLabel_1_3);

		txtNgayNhap = new JTextField();
		txtNgayNhap.setColumns(10);
		txtNgayNhap.setBounds(153, 168, 139, 19);
		contentPane.add(txtNgayNhap);

		txtTongTien = new JTextField();
		txtTongTien.setColumns(10);
		txtTongTien.setBounds(153, 225, 139, 19);
		contentPane.add(txtTongTien);

		txtDaThanhToan = new JTextField();
		txtDaThanhToan.setColumns(10);
		txtDaThanhToan.setBounds(153, 278, 139, 19);
		contentPane.add(txtDaThanhToan);

		JLabel lblNewLabel_1_3_1 = new JLabel("Mã Nhà Cung Cấp:");
		lblNewLabel_1_3_1.setBounds(356, 118, 111, 16);
		contentPane.add(lblNewLabel_1_3_1);

		JLabel lblNewLabel_1_3_2 = new JLabel("Mã Nhân Viên:");
		lblNewLabel_1_3_2.setBounds(356, 169, 111, 16);
		contentPane.add(lblNewLabel_1_3_2);

		txtMaNhaCungCap = new JTextField();
		txtMaNhaCungCap.setColumns(10);
		txtMaNhaCungCap.setBounds(485, 117, 139, 19);
		contentPane.add(txtMaNhaCungCap);

		txtMaNV = new JTextField();
		txtMaNV.setColumns(10);
		txtMaNV.setBounds(485, 168, 139, 19);
		contentPane.add(txtMaNV);

		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				create();

			}
		});
		btnCreate.setBackground(Color.ORANGE);
		btnCreate.setBounds(719, 116, 85, 21);
		contentPane.add(btnCreate);

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBackground(Color.RED);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		btnDelete.setBounds(719, 167, 85, 21);
		contentPane.add(btnDelete);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		btnUpdate.setBackground(Color.YELLOW);
		btnUpdate.setBounds(719, 224, 85, 21);
		contentPane.add(btnUpdate);

		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		btnNew.setBackground(Color.GREEN);
		btnNew.setBounds(719, 279, 85, 21);
		contentPane.add(btnNew);

		txtSearch = new JTextField();
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findID();
			}
		});
		txtSearch.setBounds(238, 414, 426, 19);
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);

		JButton btnSearch = new JButton("New button");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findID();
			}
		});
		btnSearch.setBounds(719, 413, 85, 21);
		contentPane.add(btnSearch);

		JButton btnThoat = new JButton("X");
		btnThoat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnThoat.setBackground(new Color(237, 51, 59));
		btnThoat.setFont(new Font("Dialog", Font.ITALIC, 11));
		btnThoat.setBounds(784, 32, 54, 29);
		contentPane.add(btnThoat);
		btnCreate.setFocusPainted(false);
		btnCreate.setBorderPainted(false);
		btnCreate.setContentAreaFilled(true);

		btnDelete.setFocusPainted(false);
		btnUpdate.setFocusPainted(false);
		btnNew.setFocusPainted(false);
		btnSearch.setFocusPainted(false);
		btnThoat.setFocusPainted(false);

		loadTable();
		fillToTable();
	}

	public void loadTable() {

		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Mã Phiếu Nhập");
		model.addColumn("Ngày Nhập");
		model.addColumn("Tổng Tiền");
		model.addColumn("Đã Thanh Toán");
		model.addColumn("Mã Nhà Cung Cấp");
		model.addColumn("Mã Nhân Viên");

		tblPhieuNhap.setModel(model);
	}

	PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();
	List<PhieuNhapHang> items = new ArrayList<PhieuNhapHang>();
	private JTextField txtSearch;

	@Override
	public void setForm(PhieuNhapHang entity) {
		// TODO Auto-generated method stub
		txtMaPN.setText(Integer.toString(entity.getMaPN()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // định dạng ngày
		String ngayNhapStr = sdf.format(entity.getNgayNhap());
		txtNgayNhap.setText(ngayNhapStr);
		txtTongTien.setText(Double.toString(entity.getTongTien()));
		txtDaThanhToan.setText(Double.toString(entity.getDaThanhToan()));
		txtMaNhaCungCap.setText(Integer.toString(entity.getMaNCC()));
		txtMaNV.setText(entity.getMaNV());
	}

	@Override
	public PhieuNhapHang getForm() {
		PhieuNhapHang pNhapHang = new PhieuNhapHang();
		try {
			pNhapHang.setMaPN(Integer.parseInt(txtMaPN.getText()));

			// Parse ngày từ String sang Date (nếu ngày là kiểu Date)
			String ngayStr = txtNgayNhap.getText();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date ngay = sdf.parse(ngayStr);
			pNhapHang.setNgayNhap(ngay); // Nếu setNgayNhap nhận Date

			pNhapHang.setTongTien(Double.parseDouble(txtTongTien.getText()));
			pNhapHang.setDaThanhToan(Double.parseDouble(txtDaThanhToan.getText()));
			pNhapHang.setMaNCC(Integer.parseInt(txtMaNhaCungCap.getText()));
			pNhapHang.setMaNV(txtMaNV.getText());
		} catch (NumberFormatException e) {
			// xử lý nếu nhập sai kiểu số
			e.printStackTrace();
			// Bạn có thể hiện thông báo lỗi cho người dùng ở đây
		} catch (ParseException e) {
			// xử lý nếu định dạng ngày sai
			e.printStackTrace();
		}
		return pNhapHang;
	}

	@Override
	public void fillToTable() {
		// TODO Auto-generated method stub
		DefaultTableModel model = (DefaultTableModel) tblPhieuNhap.getModel();
		model.setRowCount(0);

		items = pnDAO.findAll();
		// items được sử dụng các thuộc tính của SanPham vì kdl của nó là SP
		items.forEach(item -> {
			Object[] row = { item.getMaPN(), item.getNgayNhap(), item.getTongTien(), item.getDaThanhToan(),
					item.getMaNCC(), item.getMaNV() };
			model.addRow(row);

		});
	}

	@Override
	public void edit() {
		// TODO Auto-generated method stub
		int index = tblPhieuNhap.getSelectedRow();

		PhieuNhapHang pn = items.get(index);
		setForm(pn); // Đưa dữ liệu từ đối tượng vào JTextField
		setEditable(true); // Khóa mã SP (đúng)

	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		PhieuNhapHang pn = getForm();
		pnDAO.create(pn);
		fillToTable();
		// CÁI TỔNG CỘNG VỚI CÁI ĐÃ THANH TOÁN TRỪ VỚI NHAU
		float tongtien = Float.parseFloat(txtTongTien.getText());
		float dathanhtoan = Float.parseFloat(txtDaThanhToan.getText());

		float duno = tongtien - dathanhtoan;
		NhaCungCap ncc = new NhaCungCap();
		ncc.setMaNCC(Integer.parseInt(txtMaNhaCungCap.getText()));
		ncc.setDuNo(duno);
		pnDAO.DuNo(ncc);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		PhieuNhapHang pn = getForm();
		pnDAO.update(pn);
		fillToTable();

		float tongtien = Float.parseFloat(txtTongTien.getText());
		float dathanhtoan = Float.parseFloat(txtDaThanhToan.getText());

		float duno = tongtien - dathanhtoan;
		NhaCungCap ncc = new NhaCungCap();
		ncc.setMaNCC(Integer.parseInt(txtMaNhaCungCap.getText()));
		ncc.setDuNo(duno);
		pnDAO.DuNo(ncc);
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		int ma = Integer.parseInt(txtMaPN.getText());
		pnDAO.delete(ma);
		this.fillToTable();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		txtMaPN.setText("");
		txtNgayNhap.setText("");
		txtTongTien.setText("");
		txtDaThanhToan.setText("");
		txtMaNhaCungCap.setText("");
		txtMaNV.setText("");
		setEditable(false);
	}

	@Override
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		txtMaPN.setEnabled(!editable);
	}

	@Override
	public void checkAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uncheckAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteCheckedItems() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTonKho() {
		// TODO Auto-generated method stub

	}

	@Override
	public void findID() {
		// TODO Auto-generated method stub
		try {
			DefaultTableModel model = (DefaultTableModel) tblPhieuNhap.getModel();
			model.setRowCount(0);

			int id = Integer.parseInt(txtSearch.getText());
			PhieuNhapHang pn = pnDAO.findById(id);
			Object[] row = { pn.getMaPN(), pn.getNgayNhap(), pn.getTongTien(), pn.getDaThanhToan(), pn.getMaNCC(),
					pn.getMaNV() };
			model.addRow(row);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Mã không tồn tại", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			fillToTable();
		}

	}
}
