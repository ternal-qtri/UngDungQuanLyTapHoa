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

/** Quản lý nhà cung cấp (chỉ quản lý). Công nợ được xem và trả ở màn Công nợ. */
public class SupplierPanel extends JPanel implements Screen {
    private final AppContext app;
    private final AppTable<NhaCungCap> table;

    public SupplierPanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<NhaCungCap>("Mã", NhaCungCap::getMaNCC),
                new AppTable.Column<NhaCungCap>("Tên nhà cung cấp", NhaCungCap::getHoTenNCC),
                new AppTable.Column<NhaCungCap>("Điện thoại", NhaCungCap::getSoDienThoai),
                new AppTable.Column<NhaCungCap>("Địa chỉ", NhaCungCap::getDiaChi),
                new AppTable.Column<NhaCungCap>("Công nợ", ncc -> Ui.money(ncc.getDuNo()))));

        add(Ui.pageHeader("Nhà cung cấp", "Danh sách đối tác cung cấp hàng"), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.GAP, 0));
        bar.setOpaque(false);
        JButton add = Ui.primaryButton("Thêm");
        add.addActionListener(e -> Ui.guard(this, this::create));
        JButton delete = Ui.dangerButton("Xóa");
        delete.addActionListener(e -> Ui.guard(this, this::delete));
        bar.add(add);
        bar.add(delete);
        add(bar, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Ui.guard(this, () -> table.setItems(app.suppliers.list()));
    }

    private void create() {
        Forms.ask(this, "Thêm nhà cung cấp",
                new String[]{"*Tên nhà cung cấp", "Điện thoại", "Địa chỉ"}, null)
                .ifPresent(v -> {
                    app.suppliers.create(v[0], v[1], v[2]);
                    refresh();
                });
    }

    private void delete() {
        NhaCungCap ncc = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một nhà cung cấp"));
        if (Ui.confirm(this, "Xóa nhà cung cấp \"" + ncc.getHoTenNCC() + "\"?")) {
            app.suppliers.delete(ncc.getMaNCC());
            refresh();
        }
    }
}
