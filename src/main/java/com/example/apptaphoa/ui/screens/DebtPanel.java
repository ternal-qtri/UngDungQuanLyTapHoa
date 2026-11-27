package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.ui.Screen;
import com.example.apptaphoa.ui.components.AppTable;
import com.example.apptaphoa.ui.components.Forms;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/** Công nợ nhà cung cấp: danh sách còn nợ và trả nợ (chỉ quản lý). */
public class DebtPanel extends JPanel implements Screen {
    private final AppContext app;
    private final AppTable<NhaCungCap> table;

    public DebtPanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<NhaCungCap>("Mã", NhaCungCap::getMaNCC),
                new AppTable.Column<NhaCungCap>("Nhà cung cấp", NhaCungCap::getHoTenNCC),
                new AppTable.Column<NhaCungCap>("Điện thoại", NhaCungCap::getSoDienThoai),
                new AppTable.Column<NhaCungCap>("Còn nợ", ncc -> Ui.money(ncc.getDuNo()))));

        add(Ui.pageHeader("Công nợ nhà cung cấp", "Chỉ hiển thị nhà cung cấp còn nợ"), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.GAP, 0));
        bar.setOpaque(false);
        JButton pay = Ui.primaryButton("Trả nợ");
        pay.addActionListener(e -> Ui.guard(this, this::pay));
        bar.add(pay);
        add(bar, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Ui.guard(this, () -> table.setItems(
                app.suppliers.list().stream().filter(s -> s.getDuNo() > 0).toList()));
    }

    private void pay() {
        NhaCungCap ncc = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một nhà cung cấp"));
        Forms.ask(this, "Trả nợ - " + ncc.getHoTenNCC(),
                        new String[]{"*Số tiền trả (đ)"}, new String[]{String.valueOf(ncc.getDuNo())})
                .ifPresent(v -> {
                    app.debts.payDebt(ncc.getMaNCC(), Forms.parseNumber(v[0], "Số tiền trả"));
                    Ui.info(this, "Đã ghi nhận thanh toán");
                    refresh();
                });
    }
}
