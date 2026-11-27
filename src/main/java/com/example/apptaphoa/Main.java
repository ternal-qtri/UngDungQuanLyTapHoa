package com.example.apptaphoa;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.ui.LoginScreen;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.SwingUtilities;

/** Điểm vào của ứng dụng: cài giao diện, tạo AppContext và mở màn hình đăng nhập. */
public class Main {
    public static void main(String[] args) {
        AppTheme.install();
        SwingUtilities.invokeLater(() -> new LoginScreen(new AppContext()).setVisible(true));
    }
}
