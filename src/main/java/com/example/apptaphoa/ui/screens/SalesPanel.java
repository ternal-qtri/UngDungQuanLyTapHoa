package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.service.SalesService.CartLine;
import com.example.apptaphoa.ui.Screen;
import com.example.apptaphoa.ui.components.AppTable;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Bán hàng: chọn sản phẩm, thêm vào giỏ, thanh toán. */
public class SalesPanel extends JPanel implements Screen {

    /** Dòng trong giỏ hàng. */
    public record CartItem(int maSanPham, String tenSanPham, double donGia, int soLuong) {
        double thanhTien() {
            return donGia * soLuong;
        }
    }

    private final AppContext app;
    private final AppTable<SanPham> productTable;
    private final List<CartItem> cart = new ArrayList<>();
    private final AppTable<CartItem> cartTable;
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JComboBox<String> payment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
    private final JLabel totalLabel = new JLabel();

    public SalesPanel(AppContext app) {
        super(new BorderLayout(AppTheme.PAD, AppTheme.PAD));
        this.app = app;
        setOpaque(false);

        productTable = new AppTable<>(List.of(
                new AppTable.Column<SanPham>("Mã", SanPham::getMaSanPham),
                new AppTable.Column<SanPham>("Sản phẩm", SanPham::getTenSanPham),
                new AppTable.Column<SanPham>("Đơn giá", sp -> Ui.money(sp.getDonGia())),
                new AppTable.Column<SanPham>("Tồn kho", SanPham::getSoLuongTonKho)));
        cartTable = new AppTable<>(List.of(
                new AppTable.Column<CartItem>("Sản phẩm", CartItem::tenSanPham),
                new AppTable.Column<CartItem>("SL", CartItem::soLuong),
                new AppTable.Column<CartItem>("Thành tiền", item -> Ui.money(item.thanhTien()))));

        add(Ui.pageHeader("Bán hàng", "Chọn sản phẩm, nhập số lượng rồi thêm vào giỏ"), BorderLayout.NORTH);

        JPanel left = new JPanel(new BorderLayout(0, AppTheme.GAP));
        left.add(productTable, BorderLayout.CENTER);
        JPanel addRow = new JPanel();
        addRow.setOpaque(false);
        addRow.add(new JLabel("Số lượng:"));
        addRow.add(quantity);
        JButton addBtn = Ui.primaryButton("Thêm vào giỏ");
        addBtn.addActionListener(e -> Ui.guard(this, this::addToCart));
        addRow.add(addBtn);
        left.add(addRow, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(0, AppTheme.GAP));
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        right.add(cartTable, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(3, 1, 0, 6));
        bottom.setOpaque(false);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 18f));
        bottom.add(totalLabel);
        bottom.add(payment);
        JPanel buttons = new JPanel(new GridLayout(1, 2, AppTheme.GAP, 0));
        buttons.setOpaque(false);
        JButton remove = Ui.secondaryButton("Bỏ dòng đã chọn");
        remove.addActionListener(e -> removeSelected());
        JButton checkout = Ui.primaryButton("Thanh toán");
        checkout.addActionListener(e -> Ui.guard(this, this::checkout));
        buttons.add(remove);
        buttons.add(checkout);
        bottom.add(buttons);
        right.add(bottom, BorderLayout.SOUTH);

        JPanel body = new JPanel(new GridLayout(1, 2, AppTheme.PAD, 0));
        body.setOpaque(false);
        body.add(left);
        body.add(right);
        add(body, BorderLayout.CENTER);
        refreshTotal();
    }

    @Override
    public void refresh() {
        productTable.setItems(app.products.list());
    }

    private void addToCart() {
        SanPham product = productTable.selected()
                .orElseThrow(() -> new BusinessException("Hãy chọn một sản phẩm trong danh sách"));
        int qty = (Integer) quantity.getValue();
        int inCart = cart.stream().filter(i -> i.maSanPham() == product.getMaSanPham())
                .mapToInt(CartItem::soLuong).sum();
        if (inCart + qty > product.getSoLuongTonKho()) {
            throw new BusinessException("Chỉ còn " + product.getSoLuongTonKho() + " sản phẩm trong kho");
        }
        cart.removeIf(i -> i.maSanPham() == product.getMaSanPham());
        cart.add(new CartItem(product.getMaSanPham(), product.getTenSanPham(), product.getDonGia(), inCart + qty));
        refreshCart();
    }

    private void removeSelected() {
        cartTable.selected().ifPresent(item -> {
            cart.remove(item);
            refreshCart();
        });
    }

    private void checkout() {
        if (cart.isEmpty()) throw new BusinessException("Giỏ hàng trống");
        List<CartLine> lines = cart.stream().map(i -> new CartLine(i.maSanPham(), i.soLuong())).toList();
        int maHD = app.sales.checkout(lines, (String) payment.getSelectedItem());
        Ui.info(this, "Đã lập hóa đơn #" + maHD + " - tổng " + Ui.money(total()));
        cart.clear();
        refreshCart();
        refresh();
    }

    private double total() {
        return cart.stream().mapToDouble(CartItem::thanhTien).sum();
    }

    private void refreshCart() {
        cartTable.setItems(cart);
        refreshTotal();
    }

    private void refreshTotal() {
        totalLabel.setText("Tổng cộng: " + Ui.money(total()));
    }
}
