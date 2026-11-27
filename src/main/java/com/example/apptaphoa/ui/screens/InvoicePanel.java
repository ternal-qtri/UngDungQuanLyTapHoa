package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.ChiTietHoaDon;
import com.example.apptaphoa.model.HoaDon;
import com.example.apptaphoa.ui.Screen;
import com.example.apptaphoa.ui.components.AppTable;
import com.example.apptaphoa.ui.components.DateField;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.time.LocalDate;
import java.util.List;

/** Lịch sử hóa đơn: quản lý thấy tất cả, nhân viên chỉ thấy của mình. */
public class InvoicePanel extends JPanel implements Screen {
    private final AppContext app;
    private final DateField from = new DateField(LocalDate.now().minusDays(30));
    private final DateField to = new DateField(LocalDate.now());
    private final AppTable<HoaDon> table;

    public InvoicePanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<HoaDon>("Mã HĐ", HoaDon::getMaHD),
                new AppTable.Column<HoaDon>("Thời gian", hd -> Ui.dateTime(hd.getThoiGian())),
                new AppTable.Column<HoaDon>("Tổng tiền", hd -> Ui.money(hd.getTongTien())),
                new AppTable.Column<HoaDon>("Thanh toán", HoaDon::getHinhThucTT),
                new AppTable.Column<HoaDon>("Nhân viên", HoaDon::getMaNV)));

        add(Ui.pageHeader("Hóa đơn", "Lịch sử bán hàng theo khoảng ngày"), BorderLayout.NORTH);

        JPanel bar = new JPanel();
        bar.setOpaque(false);
        bar.add(new JLabel("Từ:"));
        bar.add(from);
        bar.add(new JLabel("đến:"));
        bar.add(to);
        JButton filter = Ui.primaryButton("Lọc");
        filter.addActionListener(e -> Ui.guard(this, this::load));
        JButton detail = Ui.secondaryButton("Xem chi tiết");
        detail.addActionListener(e -> Ui.guard(this, this::showDetail));
        bar.add(filter);
        bar.add(detail);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(bar, BorderLayout.NORTH);
        top.add(table, BorderLayout.CENTER);
        add(top, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        Ui.guard(this, this::load);
    }

    private void load() {
        table.setItems(app.sales.listInvoices(from.value(), to.value()));
    }

    private void showDetail() {
        HoaDon hd = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một hóa đơn"));
        List<ChiTietHoaDon> lines = app.sales.invoiceLines(hd.getMaHD());

        AppTable<ChiTietHoaDon> lineTable = new AppTable<>(List.of(
                new AppTable.Column<ChiTietHoaDon>("Sản phẩm", ChiTietHoaDon::getTenSanPham),
                new AppTable.Column<ChiTietHoaDon>("SL", ChiTietHoaDon::getSoLuong),
                new AppTable.Column<ChiTietHoaDon>("Đơn giá", l -> Ui.money(l.getDonGia())),
                new AppTable.Column<ChiTietHoaDon>("Thành tiền", l -> Ui.money(l.getThanhTien()))));
        lineTable.setItems(lines);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Hóa đơn #" + hd.getMaHD(),
                Dialog.ModalityType.APPLICATION_MODAL);
        JPanel body = new JPanel(new BorderLayout(0, AppTheme.GAP));
        body.setBorder(javax.swing.BorderFactory.createEmptyBorder(AppTheme.PAD, AppTheme.PAD, AppTheme.PAD, AppTheme.PAD));
        body.add(Ui.pageHeader("Hóa đơn #" + hd.getMaHD(),
                Ui.dateTime(hd.getThoiGian()) + " · " + hd.getHinhThucTT() + " · tổng " + Ui.money(hd.getTongTien())),
                BorderLayout.NORTH);
        body.add(lineTable, BorderLayout.CENTER);
        dialog.setContentPane(body);
        dialog.setSize(620, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
