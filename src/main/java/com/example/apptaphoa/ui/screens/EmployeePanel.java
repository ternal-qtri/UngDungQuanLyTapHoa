package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhanVien;
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

/** Quản lý nhân viên (chỉ quản lý). Mật khẩu chỉ đặt khi tạo mới, đổi mật khẩu ở màn hình riêng. */
public class EmployeePanel extends JPanel implements Screen {
    private static final String[] ROLES_HINT = {"Quản lý", "Nhân viên"};

    private final AppContext app;
    private final AppTable<NhanVien> table;

    public EmployeePanel(AppContext app) {
        super(new BorderLayout(0, AppTheme.GAP));
        this.app = app;
        setOpaque(false);

        table = new AppTable<>(List.of(
                new AppTable.Column<NhanVien>("Mã NV", NhanVien::getMaNV),
                new AppTable.Column<NhanVien>("Họ tên", NhanVien::getHoTen),
                new AppTable.Column<NhanVien>("Tên đăng nhập", NhanVien::getTenDangNhap),
                new AppTable.Column<NhanVien>("Giới tính", NhanVien::getGioiTinh),
                new AppTable.Column<NhanVien>("Email", NhanVien::getEmail),
                new AppTable.Column<NhanVien>("Vai trò", NhanVien::getVaiTro)));

        add(Ui.pageHeader("Nhân viên", "Tài khoản đăng nhập và vai trò"), BorderLayout.NORTH);
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
        Ui.guard(this, () -> table.setItems(app.employees.list()));
    }

    private void create() {
        Forms.ask(this, "Thêm nhân viên", new String[]{
                "*Mã NV", "*Họ tên", "*Tên đăng nhập", "Giới tính", "Email",
                "*Vai trò (" + ROLES_HINT[0] + " / " + ROLES_HINT[1] + ")", "*Mật khẩu ban đầu"}, null)
                .ifPresent(v -> {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(v[0]);
                    nv.setHoTen(v[1]);
                    nv.setTenDangNhap(v[2]);
                    nv.setGioiTinh(v[3]);
                    nv.setEmail(v[4]);
                    nv.setVaiTro(v[5]);
                    app.employees.create(nv, v[6]);
                    refresh();
                });
    }

    private void edit() {
        NhanVien nv = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một nhân viên"));
        Forms.ask(this, "Sửa nhân viên - " + nv.getMaNV(), new String[]{
                "*Họ tên", "*Tên đăng nhập", "Giới tính", "Email",
                "*Vai trò (" + ROLES_HINT[0] + " / " + ROLES_HINT[1] + ")"},
                new String[]{nv.getHoTen(), nv.getTenDangNhap(), nv.getGioiTinh(), nv.getEmail(), nv.getVaiTro()})
                .ifPresent(v -> {
                    nv.setHoTen(v[0]);
                    nv.setTenDangNhap(v[1]);
                    nv.setGioiTinh(v[2]);
                    nv.setEmail(v[3]);
                    nv.setVaiTro(v[4]);
                    app.employees.update(nv);
                    refresh();
                });
    }

    private void delete() {
        NhanVien nv = table.selected().orElseThrow(() -> new BusinessException("Hãy chọn một nhân viên"));
        if (Ui.confirm(this, "Xóa nhân viên \"" + nv.getHoTen() + "\"?")) {
            app.employees.delete(nv.getMaNV());
            refresh();
        }
    }
}
