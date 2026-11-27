package com.example.apptaphoa.ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;

/** Màu sắc, khoảng cách và cài đặt giao diện dùng chung. Không đặt màu rải rác trong các màn hình. */
public final class AppTheme {
    public static final Color PRIMARY = new Color(0x2563EB);
    public static final Color PRIMARY_DARK = new Color(0x1D4ED8);
    public static final Color SIDEBAR = new Color(0x0F172A);
    public static final Color SIDEBAR_HOVER = new Color(0x1E293B);
    public static final Color SIDEBAR_TEXT = new Color(0xE2E8F0);
    public static final Color SURFACE = new Color(0xF8FAFC);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(0x64748B);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color SUCCESS = new Color(0x16A34A);

    public static final int PAD = 16;
    public static final int GAP = 8;

    private AppTheme() {}

    /** Gọi một lần trước khi tạo cửa sổ đầu tiên. */
    public static void install() {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
    }
}
