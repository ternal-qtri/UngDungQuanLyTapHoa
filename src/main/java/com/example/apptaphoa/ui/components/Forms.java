package com.example.apptaphoa.ui.components;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

/** Hộp thoại nhập nhiều ô văn bản. */
public final class Forms {
    private Forms() {}

    /**
     * Hiện form với các nhãn và giá trị mặc định. Trả về giá trị đã nhập, hoặc rỗng nếu người dùng hủy.
     * Ô có nhãn bắt đầu bằng '*' là bắt buộc nhập.
     */
    public static Optional<String[]> ask(Component parent, String title, String[] labels, String[] initial) {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField[] fields = new JTextField[labels.length];
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 0;
            panel.add(new JLabel(labels[i]), c);
            fields[i] = new JTextField(initial != null && initial[i] != null ? initial[i] : "", 24);
            c.gridx = 1;
            c.weightx = 1;
            panel.add(fields[i], c);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return Optional.empty();

        String[] values = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            String value = fields[i].getText().trim();
            if (labels[i].startsWith("*") && value.isEmpty()) {
                throw new com.example.apptaphoa.exception.BusinessException(
                        labels[i].substring(1).trim() + " không được để trống");
            }
            values[i] = value;
        }
        return Optional.of(values);
    }

    /** Đọc số nguyên hoặc số thực từ ô nhập, ném BusinessException nếu sai định dạng. */
    public static double parseNumber(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new com.example.apptaphoa.exception.BusinessException(fieldName + " phải là số");
        }
    }

    public static int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new com.example.apptaphoa.exception.BusinessException(fieldName + " phải là số nguyên");
        }
    }
}
