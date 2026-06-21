package com.example.apptaphoa.views;

import com.example.apptaphoa.controller.HoaDonController;
import com.example.apptaphoa.dao.HoaDonDAOItf;
import com.example.apptaphoa.dao.impl.HoaDonDAOImpl;
import com.example.apptaphoa.model.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class HoaDonFrame extends JDialog implements HoaDonController {

	private JTable tblHoaDon;
	private JTextField txtMaHD, txtTuNgay, txtDenNgay;
	private JComboBox<String> cboNgay;
	private final HoaDonDAOItf dao = new HoaDonDAOImpl();
	private List<HoaDon> items = new ArrayList<>();

	private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat INPUT_SDF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public HoaDonFrame(Frame parent, boolean modal) {
		super(parent, modal);
		initUI();
		tblHoaDon.setDefaultEditor(Object.class, null);
	}

	private void initUI() {
		setTitle("Lịch Sử Hoá Đơn");
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JLabel title = new JLabel("Hoá Đơn");
		title.setFont(new Font("Segoe UI", Font.BOLD, 32));
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(title);
		add(header, BorderLayout.NORTH);

		tblHoaDon = new JTable(new DefaultTableModel(new Object[][] {},
				new String[] { "Mã HD", "Thời Gian", "Tổng Tiền", "Hình Thức TT", "Mã NV" }));
		add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

		// Filter panel
		JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT));
		range.add(new JLabel("Từ ngày:"));
		txtTuNgay = new JTextField(16);
		range.add(txtTuNgay);
		range.add(new JLabel("Đến ngày:"));
		txtDenNgay = new JTextField(16);
		range.add(txtDenNgay);

		JButton btnLoc = new JButton("Lọc");
		btnLoc.addActionListener(e -> {
			txtMaHD.setText("");
			try {
				Date tu = INPUT_SDF.parse(txtTuNgay.getText().trim());
				Date den = INPUT_SDF.parse(txtDenNgay.getText().trim());
				filterByDate(tu, den);
			} catch (ParseException ex) {
				JOptionPane.showMessageDialog(this, "Ngày không đúng định dạng dd-MM-yyyy HH:mm:ss");
			}
		});
		range.add(btnLoc);

		cboNgay = new JComboBox<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Năm nay" });
		cboNgay.addActionListener(e -> applyQuickRange());
		range.add(cboNgay);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.LEFT));
		right.add(new JLabel("Mã Hoá Đơn"));
		txtMaHD = new JTextField(10);
		right.add(txtMaHD);
		JButton btnSearch = new JButton("Tìm Kiếm");
		btnSearch.addActionListener(e -> {
			String s = txtMaHD.getText().trim();
			if (s.isEmpty()) {
				load();
				return;
			}
			try {
				int ma = Integer.parseInt(s);
				findById(ma);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Mã hoá đơn phải là số!");
			}
		});
		right.add(btnSearch);

		filter.add(range);
		filter.add(right);
		add(filter, BorderLayout.SOUTH);

		tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2 && tblHoaDon.getSelectedRow() != -1) {
					int row = tblHoaDon.getSelectedRow();
					int maHD = Integer.parseInt(tblHoaDon.getValueAt(row, 0).toString());
					ChiTietHoaDonFrame dialog = new ChiTietHoaDonFrame(maHD);
					dialog.setVisible(true);
				}
			}
		});

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				open();
			}
		});
	}

	private void applyQuickRange() {
		String selected = Objects.toString(cboNgay.getSelectedItem(), "");
		LocalDateTime now = LocalDateTime.now();

		if (selected.equals("Hôm nay")) {
			txtTuNgay
					.setText(LocalDate.now().atStartOfDay().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
			txtDenNgay.setText(
					LocalDate.now().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		} else if (selected.equals("Tuần này")) {
			LocalDateTime start = now.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
			LocalDateTime end = now.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
			txtTuNgay.setText(start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
			txtDenNgay.setText(end.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		} else if (selected.equals("Tháng này")) {
			LocalDate d = LocalDate.now();
			LocalDateTime start = d.withDayOfMonth(1).atStartOfDay();
			LocalDateTime end = d.withDayOfMonth(d.lengthOfMonth()).atTime(23, 59, 59);
			txtTuNgay.setText(start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
			txtDenNgay.setText(end.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		} else if (selected.equals("Năm nay")) {
			LocalDate d = LocalDate.now();
			LocalDateTime start = d.withDayOfYear(1).atStartOfDay();
			LocalDateTime end = d.withDayOfYear(d.lengthOfYear()).atTime(23, 59, 59);
			txtTuNgay.setText(start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
			txtDenNgay.setText(end.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		}
	}

	// HoaDonController
	@Override
	public void open() {
		setLocationRelativeTo(null);
		load();
	}

	@Override
	public void fillToTable(List<HoaDon> list) {
		DefaultTableModel model = (DefaultTableModel) tblHoaDon.getModel();
		model.setRowCount(0);
		list.forEach(item -> {
			String timeStr = item.getThoiGian() == null ? ""
					: ((java.sql.Timestamp) item.getThoiGian()).toLocalDateTime().format(DISPLAY_FMT);
			model.addRow(
					new Object[] { item.getMaHD(), timeStr, item.getTongTien(), item.getHinhThucTT(), item.getMaNV() });
		});
	}

	@Override
	public void findById(int maHD) {
		if (maHD == 0) {
			load();
			return;
		}
		HoaDon hd = dao.findById(maHD);
		List<HoaDon> rs = new ArrayList<>();
		if (hd != null)
			rs.add(hd);
		fillToTable(rs);
	}

	@Override
	public void filterByDate(Date tuNgay, Date denNgay) {
		fillToTable(dao.findByDateRange(tuNgay, denNgay));
	}

	@Override
	public void load() {
		items = dao.findAll();
		fillToTable(items);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new HoaDonFrame(new JFrame(), true).setVisible(true));
	}
}
