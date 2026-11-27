package com.example.apptaphoa.ui;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.ui.components.Forms;
import com.example.apptaphoa.ui.components.Ui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Đổi mật khẩu cho người đang đăng nhập. */
public class ChangePasswordDialog extends JDialog {
    private final AppContext app;
    private final JPasswordField current = new JPasswordField(20);
    private final JPasswordField next = new JPasswordField(20);
    private final JPasswordField confirm = new JPasswordField(20);

    public ChangePasswordDialog(Frame owner, AppContext app) {
        super(owner, "Đổi mật khẩu", true);
        this.app = app;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        row(form, c, 0, "Mật khẩu hiện tại", current);
        row(form, c, 1, "Mật khẩu mới", next);
        row(form, c, 2, "Nhập lại mật khẩu mới", confirm);

        var save = Ui.primaryButton("Lưu");
        save.addActionListener(e -> save());
        c.gridx = 1; c.gridy = 3; c.weightx = 1;
        form.add(save, c);

        setContentPane(form);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void save() {
        Ui.guard(this, () -> {
            String pw = new String(next.getPassword());
            if (!pw.equals(new String(confirm.getPassword()))) {
                Ui.error(this, "Hai mật khẩu mới không khớp");
                return;
            }
            app.auth.changePassword(new String(current.getPassword()), pw);
            Ui.info(this, "Đã đổi mật khẩu");
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
