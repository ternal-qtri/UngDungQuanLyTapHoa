package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.model.DoanhThu;
import com.example.apptaphoa.ui.Screen;
import com.example.apptaphoa.ui.components.AppTable;
import com.example.apptaphoa.ui.components.BarChart;
import com.example.apptaphoa.ui.components.DateField;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

/** Báo cáo doanh thu theo ngày trong khoảng chọn (chỉ quản lý). */
public class ReportPanel extends JPanel implements Screen {
    private final AppContext app;
    private final DateField from = new DateField(LocalDate.now().withDayOfMonth(1));
    private final DateField to = new DateField(LocalDate.now());
    private final BarChart chart = new BarChart();
    private final AppTable<DoanhThu> table;
    private final JLabel summary = new JLabel();

    public ReportPanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<DoanhThu>("Ngày", dt -> Ui.dateOf(dt.getNgay())),
                new AppTable.Column<DoanhThu>("Số hóa đơn", DoanhThu::getSoHoaDon),
                new AppTable.Column<DoanhThu>("Số sản phẩm", DoanhThu::getSoSanPham),
                new AppTable.Column<DoanhThu>("Doanh thu", dt -> Ui.money(dt.getTongTien()))));

        add(Ui.pageHeader("Doanh thu", "Tổng hợp theo ngày trong khoảng đã chọn"), BorderLayout.NORTH);

        JPanel bar = new JPanel();
        bar.setOpaque(false);
        bar.add(new JLabel("Từ:"));
        bar.add(from);
        bar.add(new JLabel("đến:"));
        bar.add(to);
        JButton view = Ui.primaryButton("Xem báo cáo");
        view.addActionListener(e -> Ui.guard(this, this::load));
        bar.add(view);

        JPanel center = new JPanel(new BorderLayout(0, AppTheme.GAP));
        center.setOpaque(false);
        center.add(bar, BorderLayout.NORTH);
        center.add(chart, BorderLayout.CENTER);
        center.add(table, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        add(summary, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Ui.guard(this, this::load);
    }

    private void load() {
        List<DoanhThu> rows = app.reports.revenueBetween(from.value(), to.value());
        table.setItems(rows);
        chart.setData(
                rows.stream().map(dt -> Ui.dateOf(dt.getNgay()).substring(0, 5)).toList(),
                rows.stream().map(DoanhThu::getTongTien).toList());
        double total = rows.stream().mapToDouble(DoanhThu::getTongTien).sum();
        int invoices = rows.stream().mapToInt(DoanhThu::getSoHoaDon).sum();
        summary.setText("Tổng doanh thu: " + Ui.money(total) + "  ·  " + invoices + " hóa đơn");
    }
}
