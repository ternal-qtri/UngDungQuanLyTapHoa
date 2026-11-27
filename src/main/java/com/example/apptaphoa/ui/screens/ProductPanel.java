package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.SanPham;
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

/** Quản lý sản phẩm (chỉ quản lý). */
public class ProductPanel extends JPanel implements Screen {
    private final AppContext app;
    private final AppTable<SanPham> table;

    public ProductPanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<SanPham>("Mã", SanPham::getMaSanPham),
                new AppTable.Column<SanPham>("Tên sản phẩm", SanPham::getTenSanPham),
                new AppTable.Column<SanPham>("Đơn giá", sp -> Ui.money(sp.getDonGia())),
                new AppTable.Column<SanPham>("Tồn kho", SanPham::getSoLuongTonKho),
                new AppTable.Column<SanPham>("Loại", SanPham::getLoaiSanPham)));

        add(Ui.pageHeader("Sản phẩm", "Thêm, sửa, xóa mặt hàng và tồn kho"), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.GAP, 0));
        bar.setOpaque(false);
        JButton add = Ui.primaryButton("Thêm");
        add.addActionListener(e -> Ui.guard(this, this::create));
        JButton edit = Ui.secondaryButton("Sửa");
        edit.addActionListener(e -> Ui.guard(this, this::edit));
        JButton delete = Ui.dangerButton("Xóa");
        delete.addActionListener(e -> Ui.guard(this, this::delete));
        bar.add(add);
        bar.add(edit);
        bar.add(delete);
        add(bar, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Ui.guard(this, () -> table.setItems(app.products.list()));
    }

    private void create() {
        Forms.ask(this, "Thêm sản phẩm",
                new String[]{"*Tên sản phẩm", "*Đơn giá", "*Tồn kho", "Loại"}, null)
                .ifPresent(v -> {
                    app.products.create(v[0], Forms.parseNumber(v[1], "Đơn giá"),
                            Forms.parseInt(v[2], "Tồn kho"), v[3]);
                    refresh();
                });
    }

    private void edit() {
        SanPham sp = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một sản phẩm"));
        Forms.ask(this, "Sửa sản phẩm",
                new String[]{"*Tên sản phẩm", "*Đơn giá", "*Tồn kho", "Loại"},
                new String[]{sp.getTenSanPham(), String.valueOf(sp.getDonGia()),
                        String.valueOf(sp.getSoLuongTonKho()), sp.getLoaiSanPham()})
                .ifPresent(v -> {
                    app.products.update(sp.getMaSanPham(), v[0], Forms.parseNumber(v[1], "Đơn giá"),
                            Forms.parseInt(v[2], "Tồn kho"), v[3]);
                    refresh();
                });
    }

    private void delete() {
        SanPham sp = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một sản phẩm"));
        if (Ui.confirm(this, "Xóa sản phẩm \"" + sp.getTenSanPham() + "\"?")) {
            app.products.delete(sp.getMaSanPham());
            refresh();
        }
    }
}
