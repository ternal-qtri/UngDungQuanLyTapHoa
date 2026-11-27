package com.example.apptaphoa.ui.components;

import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/** Biểu đồ cột đơn giản để so sánh doanh thu theo ngày. Không cần thư viện ngoài. */
public final class BarChart extends JPanel {
    private List<String> labels = List.of();
    private List<Double> values = List.of();

    public BarChart() {
        setBackground(AppTheme.CARD);
        setPreferredSize(new Dimension(400, 180));
    }

    public void setData(List<String> labels, List<Double> values) {
        this.labels = List.copyOf(labels);
        this.values = List.copyOf(values);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int bottom = h - 24, top = 12;

        if (values.isEmpty()) {
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.drawString("Chưa có dữ liệu", 12, h / 2);
            g2.dispose();
            return;
        }
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (max <= 0) max = 1;
        int n = values.size();
        int slot = Math.max(1, (w - 16) / n);
        int barW = Math.max(6, slot - 12);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        for (int i = 0; i < n; i++) {
            int barH = (int) ((bottom - top) * (values.get(i) / max));
            int x = 8 + i * slot + (slot - barW) / 2;
            g2.setColor(AppTheme.PRIMARY);
            g2.fillRoundRect(x, bottom - barH, barW, barH, 6, 6);
            g2.setColor(AppTheme.TEXT_MUTED);
            String label = labels.get(i);
            g2.drawString(label, x + barW / 2 - g2.getFontMetrics().stringWidth(label) / 2, h - 6);
        }
        g2.dispose();
    }
}
