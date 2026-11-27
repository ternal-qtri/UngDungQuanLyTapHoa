package com.example.apptaphoa.ui;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.ui.components.Ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Quên mật khẩu: bước 1 gửi OTP tới email, bước 2 nhập OTP và mật khẩu mới. */
public class ForgotPasswordDialog extends JDialog {
    private final AppContext app;
    private final JTextField username = new JTextField(20);
    private final JTextField email = new JTextField(20);
    private final JTextField otp = new JTextField(20);
    private final JPasswordField newPassword = new JPasswordField(20);
    private final JPasswordField confirm = new JPasswordField(20);

    public ForgotPasswordDialog(Frame owner, AppContext app) {
        super(owner, "Quên mật khẩu", true);
        this.app = app;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        row(form, c, 0, "Tên đăng nhập", username);
        row(form, c, 1, "Email đã đăng ký", email);
        JButton sendOtp = Ui.secondaryButton("Gửi mã OTP");
        sendOtp.addActionListener(e -> sendOtp());
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        form.add(sendOtp, c);

        row(form, c, 3, "Mã OTP (6 số)", otp);
        row(form, c, 4, "Mật khẩu mới", newPassword);
        row(form, c, 5, "Nhập lại mật khẩu", confirm);

        JButton reset = Ui.primaryButton("Đặt lại mật khẩu");
        reset.addActionListener(e -> resetPassword());
        c.gridx = 1; c.gridy = 6;
        form.add(reset, c);

        setContentPane(form);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void sendOtp() {
        Ui.guard(this, () -> {
            app.auth.requestOtp(username.getText(), email.getText());
            Ui.info(this, "Đã gửi mã OTP tới email của bạn. Mã có hiệu lực trong 5 phút.");
        });
    }

    private void resetPassword() {
        Ui.guard(this, () -> {
            String pw = new String(newPassword.getPassword());
            if (!pw.equals(new String(confirm.getPassword()))) {
                Ui.error(this, "Hai mật khẩu mới không khớp");
                return;
            }
            app.auth.resetPassword(username.getText(), otp.getText().trim(), pw);
            Ui.info(this, "Đã đặt lại mật khẩu. Hãy đăng nhập lại.");
            dispose();
        });
    }

    private static void row(JPanel form, GridBagConstraints c, int y, String label, java.awt.Component field) {
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        form.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        form.add(field, c);
    }
}
