package com.example.apptaphoa.ui;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.security.Role;
import com.example.apptaphoa.security.Session;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.screens.DebtPanel;
import com.example.apptaphoa.ui.screens.EmployeePanel;
import com.example.apptaphoa.ui.screens.InvoicePanel;
import com.example.apptaphoa.ui.screens.ProductPanel;
import com.example.apptaphoa.ui.screens.PurchasePanel;
import com.example.apptaphoa.ui.screens.ReportPanel;
import com.example.apptaphoa.ui.screens.SalesPanel;
import com.example.apptaphoa.ui.screens.SupplierPanel;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

/** Khung chính: sidebar theo vai trò và vùng nội dung. Mỗi mục là một màn hình. */
public class MainWindow extends JFrame {
    private final AppContext app;
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, Screen> screens = new LinkedHashMap<>();

    public MainWindow(AppContext app) {
        super("Quản lý Tạp Hóa");
        this.app = app;
        NhanVien user = Session.requireLogin();
        boolean manager = Role.fromDbValue(user.getVaiTro()) == Role.MANAGER;

        register("sales", "Bán hàng", new SalesPanel(app));
        register("invoices", "Hóa đơn", new InvoicePanel(app));
        if (manager) {
            register("products", "Sản phẩm", new ProductPanel(app));
            register("suppliers", "Nhà cung cấp", new SupplierPanel(app));
            register("purchase", "Nhập hàng", new PurchasePanel(app));
            register("debt", "Công nợ", new DebtPanel(app));
            register("employees", "Nhân viên", new EmployeePanel(app));
            register("report", "Doanh thu", new ReportPanel(app));
        }

        setLayout(new BorderLayout());
        add(buildSidebar(user, manager), BorderLayout.WEST);
        content.setBackground(AppTheme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(AppTheme.PAD, AppTheme.PAD, AppTheme.PAD, AppTheme.PAD));
        add(content, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 700);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
        show("sales");
    }

    private void register(String key, String title, Screen screen) {
        screens.put(key, screen);
        content.add((Component) screen, key);
    }

    private void show(String key) {
        cards.show(content, key);
        screens.get(key).refresh();
    }

    private JPanel buildSidebar(NhanVien user, boolean manager) {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(AppTheme.SIDEBAR);
        side.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));
        side.setPreferredSize(new Dimension(230, 0));

        JLabel brand = new JLabel("Tạp Hóa");
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 20f));
        brand.setForeground(java.awt.Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);

        JLabel who = new JLabel(user.getHoTen() + " · " + user.getVaiTro());
        who.setForeground(AppTheme.TEXT_MUTED);
        who.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(who);
        side.add(Box.createVerticalStrut(24));

        for (Map.Entry<String, Screen> e : screens.entrySet()) {
            String key = e.getKey();
            String title = titleOf(key);
            side.add(navButton(title, () -> show(key)));
            side.add(Box.createVerticalStrut(4));
        }

        side.add(Box.createVerticalGlue());
        side.add(navButton("Đổi mật khẩu", () -> new ChangePasswordDialog(this, app).setVisible(true)));
        side.add(Box.createVerticalStrut(4));
        side.add(navButton("Đăng xuất", this::logout));
        return side;
    }

    private static JButton navButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(JButton.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setBackground(AppTheme.SIDEBAR);
        b.setForeground(AppTheme.SIDEBAR_TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.addActionListener(e -> action.run());
        return b;
    }

    private String titleOf(String key) {
        return switch (key) {
            case "sales" -> "Bán hàng";
            case "invoices" -> "Hóa đơn";
            case "products" -> "Sản phẩm";
            case "suppliers" -> "Nhà cung cấp";
            case "purchase" -> "Nhập hàng";
            case "debt" -> "Công nợ";
            case "employees" -> "Nhân viên";
            case "report" -> "Doanh thu";
            default -> key;
        };
    }

    private void logout() {
        if (!Ui.confirm(this, "Bạn có chắc muốn đăng xuất?")) return;
        app.auth.logout();
        new LoginScreen(app).setVisible(true);
        dispose();
    }
}
