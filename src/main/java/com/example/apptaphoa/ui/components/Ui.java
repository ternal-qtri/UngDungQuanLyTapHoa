package com.example.apptaphoa.ui.components;

import com.example.apptaphoa.exception.AccessDeniedException;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/** Hàm tiện ích giao diện: nút, tiêu đề, hộp thoại, định dạng số và ngày. */
public final class Ui {
    private static final NumberFormat MONEY = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Ui() {}

    public static JButton primaryButton(String text) {
        JButton b = button(text, AppTheme.PRIMARY, java.awt.Color.WHITE);
        return b;
    }

    public static JButton dangerButton(String text) {
        return button(text, AppTheme.DANGER, java.awt.Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, AppTheme.CARD, AppTheme.PRIMARY);
    }

    private static JButton button(String text, java.awt.Color bg, java.awt.Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    /** Tiêu đề trang: chữ lớn, phụ đề màu xám. */
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 20f));
        p.add(t, BorderLayout.NORTH);
        if (subtitle != null && !subtitle.isBlank()) {
            JLabel s = new JLabel(subtitle);
            s.setForeground(AppTheme.TEXT_MUTED);
            p.add(s, BorderLayout.SOUTH);
        }
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, AppTheme.PAD, 0));
        return p;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text, SwingConstants.LEFT);
        l.setForeground(AppTheme.TEXT_MUTED);
        return l;
    }

    /** Chạy một thao tác của giao diện, hiển thị lỗi nghiệp vụ thay vì làm sập chương trình. */
    public static void guard(Component parent, Runnable action) {
        try {
            action.run();
        } catch (BusinessException | AccessDeniedException e) {
            error(parent, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            error(parent, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static String money(double value) {
        return MONEY.format(Math.round(value)) + " đ";
    }

    /** Dùng getTime() thay vì toInstant(): java.sql.Date không hỗ trợ toInstant(). */
    public static String dateTime(Date date) {
        return date == null ? "" : toLocalDateTime(date).format(DATE_TIME);
    }

    public static String date(LocalDate date) {
        return date == null ? "" : date.format(DATE);
    }

    public static String dateOf(Date date) {
        return date == null ? "" : toLocalDateTime(date).toLocalDate().format(DATE);
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
