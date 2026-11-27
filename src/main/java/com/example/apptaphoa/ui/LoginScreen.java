package com.example.apptaphoa.ui;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.model.NhanVien;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/** Màn hình đăng nhập. Thành công thì mở khung chính. */
public class LoginScreen extends JFrame {
    private final AppContext app;
    private final JTextField username = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);

    public LoginScreen(AppContext app) {
        super("Đăng nhập - Quản lý Tạp Hóa");
        this.app = app;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel title = new JLabel("Quản lý Tạp Hóa");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        JLabel sub = Ui.muted("Đăng nhập để tiếp tục");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sub);
        card.add(gap(20));

        card.add(field("Tên đăng nhập", username));
        card.add(gap(10));
        card.add(field("Mật khẩu", password));
        card.add(gap(20));

        JButton login = Ui.primaryButton("Đăng nhập");
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        login.addActionListener(e -> doLogin());
        card.add(login);
        card.add(gap(8));

        JButton forgot = Ui.secondaryButton("Quên mật khẩu?");
        forgot.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgot.addActionListener(e -> new ForgotPasswordDialog(this, app).setVisible(true));
        card.add(forgot);

        JPanel root = new JPanel(new GridLayout(1, 1));
        root.setBackground(AppTheme.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));
        root.add(card);
        setContentPane(root);
        getRootPane().setDefaultButton(login);

        setSize(480, 520);
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        Ui.guard(this, () -> {
            NhanVien user = app.auth.login(username.getText(), new String(password.getPassword()));
            Ui.info(this, "Xin chào " + user.getHoTen());
            new MainWindow(app).setVisible(true);
            dispose();
        });
    }

    private static JPanel field(String label, Component input) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 4));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(new JLabel(label));
        p.add(input);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        return p;
    }

    private static Component gap(int h) {
        return Box.createVerticalStrut(h);
    }
}
