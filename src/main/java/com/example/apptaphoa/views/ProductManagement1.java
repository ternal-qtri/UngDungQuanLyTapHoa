package com.example.apptaphoa.views;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;

import com.example.apptaphoa.controller.ProductController;
import com.example.apptaphoa.dao.SanPhamDao_;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.utils.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Console;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class ProductManagement1 extends JFrame implements ProductController<SanPham> {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableProduct;
	private JTextField txtTenSP;
	private JTextField txtDonGia;
	private JTextField txtMaSP;
	private JTextField txtSoLuong;
	private JTextField txtLoaiSP;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProductManagement1 frame = new ProductManagement1();
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
	SanPhamDao_ spDAO = new SanPhamDao_();
	List<SanPham> items = new ArrayList<SanPham>();
	private JTextField txtSearch;

	public ProductManagement1() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {

				loadTableProduct();

				fillToTable();
			}
		});

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 852, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 10, 828, 428);
		contentPane.add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 200, 808, 218);
		panel.add(scrollPane);

		tableProduct = new JTable();
		tableProduct.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

					edit();

				}
			}
		});
		tableProduct.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		tableProduct.setBackground(Color.WHITE);
		scrollPane.setViewportView(tableProduct);

		JButton btnCreate = new JButton("THÊM");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				create();
			}
		});
		btnCreate.setForeground(Color.DARK_GRAY);
		btnCreate.setBackground(Color.BLUE);
		btnCreate.setBounds(322, 127, 109, 21);
		panel.add(btnCreate);

		txtTenSP = new JTextField();
		txtTenSP.setBounds(177, 84, 129, 19);
		panel.add(txtTenSP);
		txtTenSP.setColumns(10);

		txtDonGia = new JTextField();
		txtDonGia.setColumns(10);
		txtDonGia.setBounds(339, 84, 129, 19);
		panel.add(txtDonGia);

		txtMaSP = new JTextField();
		txtMaSP.setColumns(10);
		txtMaSP.setBounds(10, 84, 129, 19);
		panel.add(txtMaSP);

		txtSoLuong = new JTextField();
		txtSoLuong.setColumns(10);
		txtSoLuong.setBounds(663, 84, 129, 19);
		panel.add(txtSoLuong);

		txtLoaiSP = new JTextField();
		txtLoaiSP.setColumns(10);
		txtLoaiSP.setBounds(495, 84, 129, 19);
		panel.add(txtLoaiSP);

		JButton btnDelete = new JButton("XÓA");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		btnDelete.setForeground(Color.BLACK);
		btnDelete.setBackground(Color.RED);
		btnDelete.setBounds(683, 127, 107, 21);
		panel.add(btnDelete);

		JButton btnUpdate = new JButton("CẬP NHẬT");
		btnUpdate.setBackground(Color.ORANGE);
		btnUpdate.setForeground(Color.BLACK);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		btnUpdate.setBounds(441, 127, 109, 21);
		panel.add(btnUpdate);

		JButton btnNew = new JButton("TẠO MỚI");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		btnNew.setForeground(Color.BLACK);
		btnNew.setBackground(Color.GREEN);
		btnNew.setBounds(566, 127, 107, 21);
		panel.add(btnNew);

		JLabel lblNewLabel = new JLabel("Mã SP");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setForeground(new Color(70, 130, 180));
		lblNewLabel.setBounds(10, 61, 115, 13);
		panel.add(lblNewLabel);

		JLabel lblTnSp = new JLabel("Tên SP");
		lblTnSp.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTnSp.setForeground(new Color(70, 130, 180));
		lblTnSp.setBounds(177, 61, 115, 13);
		panel.add(lblTnSp);

		JLabel lblnGi = new JLabel("Đơn giá");
		lblnGi.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblnGi.setForeground(new Color(70, 130, 180));
		lblnGi.setBounds(339, 61, 115, 13);
		panel.add(lblnGi);

		JLabel lblSLng = new JLabel("Số lượng");
		lblSLng.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSLng.setForeground(new Color(70, 130, 180));
		lblSLng.setBounds(663, 61, 115, 13);
		panel.add(lblSLng);

		JLabel lblLoiSnPhm = new JLabel("Loại sản phẩm");
		lblLoiSnPhm.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLoiSnPhm.setForeground(new Color(70, 130, 180));
		lblLoiSnPhm.setBounds(495, 61, 115, 13);
		panel.add(lblLoiSnPhm);

		txtSearch = new JTextField();
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findID();
			}
		});
		txtSearch.setBounds(438, 171, 259, 19);
		panel.add(txtSearch);
		txtSearch.setColumns(10);

		JButton btnSearch = new JButton("Tìm");
		btnSearch.setBackground(Color.WHITE);
		btnSearch.setForeground(new Color(0, 191, 255));
		btnSearch.setIcon(new ImageIcon("D:\\AppTapHoa\\src\\main\\resources\\Images\\Search.png"));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findID();
			}
		});
		btnSearch.setBounds(707, 170, 85, 21);
		panel.add(btnSearch);

		JLabel lblNewLabel_1 = new JLabel("QUẢN LÝ SẢN PHẨM");
		lblNewLabel_1.setForeground(new Color(135, 206, 250));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(285, 10, 252, 29);
		panel.add(lblNewLabel_1);

		ImageIcon icon = new ImageIcon("src/main/resources/Images/Search.png");
		Image image = icon.getImage().getScaledInstance(30, 20, Image.SCALE_SMOOTH); // resize
		btnSearch.setIcon(new ImageIcon(image));

		JLabel lblTmKimId = new JLabel("Tìm kiếm id:");
		lblTmKimId.setForeground(new Color(70, 130, 180));
		lblTmKimId.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTmKimId.setBounds(306, 172, 112, 13);
		panel.add(lblTmKimId);

	}

	public void loadTableProduct() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Mã SP");
		model.addColumn("Tên SP");
		model.addColumn("Đơn giá");
		model.addColumn("Số lượng tồn kho");
		model.addColumn("Loại SP");

		tableProduct.setModel(model);

	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForm(SanPham entity) {
		// TODO Auto-generated method stub
		txtMaSP.setText(Integer.toString(entity.getMaSanPham()));
		txtTenSP.setText(entity.getTenSanPham());
		txtDonGia.setText(Double.toString(entity.getDonGia()));
		txtSoLuong.setText(Integer.toString(entity.getSoLuongTonKho()));
		txtLoaiSP.setText(entity.getLoaiSanPham());

	}

	@Override
	public SanPham getForm() {
		// TODO Auto-generated method stub
		SanPham sPham = new SanPham();
		sPham.setMaSanPham(Integer.parseInt(txtMaSP.getText()));
		sPham.setTenSanPham(txtTenSP.getText());
		sPham.setDonGia(Double.parseDouble(txtDonGia.getText()));
		sPham.setSoLuongTonKho(Integer.parseInt(txtSoLuong.getText()));
		sPham.setLoaiSanPham(txtLoaiSP.getText());
		return sPham;
	}

	@Override
	public void fillToTable() {
		// TODO Auto-generated method stub
		DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
		model.setRowCount(0);

		items = spDAO.findAll();
		// items được sử dụng các thuộc tính của SanPham vì kdl của nó là SP
		items.forEach(item -> {
			Object[] row = { item.getMaSanPham(), item.getTenSanPham(), item.getDonGia(), item.getSoLuongTonKho(),
					item.getLoaiSanPham() };
			model.addRow(row);

		});

	}

	@Override
	public void edit() {
		int index = tableProduct.getSelectedRow();

		SanPham sPham = items.get(index);
		setForm(sPham); // Đưa dữ liệu từ đối tượng vào JTextField
		setEditable(true); // Khóa mã SP (đúng)

	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		SanPham sPham = getForm();
		spDAO.create(sPham);
		fillToTable();

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		SanPham sPham = getForm();
		spDAO.update(sPham);
		fillToTable();
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

		int ma = Integer.parseInt(txtMaSP.getText());
		spDAO.delete(ma);
		this.fillToTable();

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		txtMaSP.setText("");
		txtTenSP.setText("");
		txtDonGia.setText("");
		txtSoLuong.setText("");
		txtLoaiSP.setText("");
		setEditable(false);

	}

	@Override
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		txtMaSP.setEnabled(!editable);
//		    btnCreate.setEnabled(!editable);
//		    btnDelete.setEnabled(editable);
//		    btnUpdate.setEnabled(editable);

		// int rowCount = tblSuppliers.getRowCount();
		// btnMoveFirst.setEnabled(editable && rowCount > 0);
		// btnMovePrevious.setEnabled(editable && rowCount > 0);
		// btnMoveNext.setEnabled(editable && rowCount > 0);
		// btnMoveLast.setEnabled(editable && rowCount > 0);
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
	public void moveFirst() {
		// TODO Auto-generated method stub

	}

	@Override
	public void movePrevious() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveNext() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveLast() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveTo(int rowIndex) {
		// TODO Auto-generated method stub

	}

	public void clearTonKho() {
		// TODO Auto-generated method stub

	}

	public void findID() {
		// TODO Auto-generated method stub
		try {
			DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
			model.setRowCount(0);

			int id = Integer.parseInt(txtSearch.getText());
			SanPham sPham = spDAO.findById(id);
			Object[] row = { sPham.getMaSanPham(), sPham.getTenSanPham(), sPham.getDonGia(), sPham.getSoLuongTonKho(),
					sPham.getLoaiSanPham(), };
			model.addRow(row);

		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Mã không tồn tại", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			fillToTable();
		}
	}
}
