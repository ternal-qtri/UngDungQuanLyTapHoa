package com.example.apptaphoa.ui.screens;

import com.example.apptaphoa.config.AppContext;
import com.example.apptaphoa.exception.BusinessException;
import com.example.apptaphoa.model.NhaCungCap;
import com.example.apptaphoa.model.SanPham;
import com.example.apptaphoa.service.PurchaseService.PurchaseLine;
import com.example.apptaphoa.ui.Screen;
import com.example.apptaphoa.ui.components.AppTable;
import com.example.apptaphoa.ui.components.Forms;
import com.example.apptaphoa.ui.components.Ui;
import com.example.apptaphoa.ui.theme.AppTheme;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Lập phiếu nhập hàng từ nhà cung cấp (chỉ quản lý). */
public class PurchasePanel extends JPanel implements Screen {

    /** Mục trong ô chọn: id thật và nhãn hiển thị. */
    private record Option(int id, String label) {
        @Override public String toString() { return label; }
    }

    /** Dòng phiếu nhập hiển thị trên bảng. */
    private record Line(int maSanPham, String tenSanPham, int soLuong, double donGiaNhap) {
        double thanhTien() { return soLuong * donGiaNhap; }
    }

    private final AppContext app;
    private final JComboBox<Option> supplierBox = new JComboBox<>();
    private final JComboBox<Option> productBox = new JComboBox<>();
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
    private final JTextField price = new JTextField(10);
    private final JTextField paid = new JTextField("0", 12);
    private final JLabel totalLabel = new JLabel();
    private final JLabel debtLabel = new JLabel();
    private final AppTable<Line> lines;
    private final List<Line> cart = new ArrayList<>();
    private List<SanPham> products = List.of();

    public PurchasePanel(AppContext app) {
        super(new BorderLayout(AppTheme.PAD, AppTheme.PAD));
        this.app = app;
        setOpaque(false);

        lines = new AppTable<>(List.of(
                new AppTable.Column<Line>("Sản phẩm", Line::tenSanPham),
                new AppTable.Column<Line>("SL", Line::soLuong),
                new AppTable.Column<Line>("Giá nhập", l -> Ui.money(l.donGiaNhap())),
                new AppTable.Column<Line>("Thành tiền", l -> Ui.money(l.thanhTien()))));

        add(Ui.pageHeader("Nhập hàng", "Phần chưa thanh toán sẽ được cộng vào công nợ nhà cung cấp"),
                BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, AppTheme.GAP, AppTheme.GAP));
        form.setOpaque(false);
        form.add(new JLabel("Nhà cung cấp:"));
        form.add(supplierBox);
        form.add(new JLabel("Sản phẩm:"));
        form.add(productBox);
        form.add(new JLabel("Số lượng:"));
        form.add(quantity);
        form.add(new JLabel("Giá nhập (đ):"));
        form.add(price);
        JButton addLine = Ui.secondaryButton("Thêm dòng");
        addLine.addActionListener(e -> Ui.guard(this, this::addLine));
        JButton removeLine = Ui.secondaryButton("Bỏ dòng đã chọn");
        removeLine.addActionListener(e -> removeLine());
        form.add(addLine);
        form.add(removeLine);

        productBox.addActionListener(e -> fillPriceFromProduct());

        JPanel right = new JPanel(new BorderLayout(0, AppTheme.GAP));
        right.setOpaque(false);
        right.add(form, BorderLayout.NORTH);
        right.add(lines, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 6));
        footer.setOpaque(false);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));
        footer.add(totalLabel);
        footer.add(debtLabel);
        JPanel paidRow = new JPanel();
        paidRow.setOpaque(false);
        paidRow.add(new JLabel("Đã thanh toán (đ):"));
        paidRow.add(paid);
        paid.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotals(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotals(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotals(); }
        });
        footer.add(paidRow);
        JButton save = Ui.primaryButton("Lập phiếu nhập");
        save.addActionListener(e -> Ui.guard(this, this::save));
        footer.add(save);
        right.add(footer, BorderLayout.SOUTH);

        add(right, BorderLayout.CENTER);
        updateTotals();
    }

    @Override
    public void refresh() {
        Ui.guard(this, () -> {
            Option selectedSupplier = (Option) supplierBox.getSelectedItem();
            List<NhaCungCap> suppliers = app.suppliers.list();
            supplierBox.setModel(new DefaultComboBoxModel<>(
                    suppliers.stream().map(s -> new Option(s.getMaNCC(), s.getHoTenNCC())).toArray(Option[]::new)));
            if (selectedSupplier != null) supplierBox.setSelectedItem(selectedSupplier);

            products = app.products.list();
            productBox.setModel(new DefaultComboBoxModel<>(
                    products.stream().map(p -> new Option(p.getMaSanPham(), p.getTenSanPham())).toArray(Option[]::new)));
            fillPriceFromProduct();
        });
    }

    private void fillPriceFromProduct() {
        Option selected = (Option) productBox.getSelectedItem();
        if (selected == null) return;
        products.stream().filter(p -> p.getMaSanPham() == selected.id()).findFirst()
                .ifPresent(p -> price.setText(String.valueOf(Math.round(p.getDonGia()))));
    }

    private void addLine() {
        Option product = (Option) productBox.getSelectedItem();
        if (product == null) throw new BusinessException("Chưa có sản phẩm để nhập");
        int qty = (Integer) quantity.getValue();
        double gia = Forms.parseNumber(price.getText(), "Giá nhập");
        if (gia < 0) throw new BusinessException("Giá nhập không được âm");
        cart.add(new Line(product.id(), product.label(), qty, gia));
        lines.setItems(cart);
        updateTotals();
    }

    private void removeLine() {
        lines.selected().ifPresent(l -> {
            cart.remove(l);
            lines.setItems(cart);
            updateTotals();
        });
    }

    private void save() {
        Option supplier = (Option) supplierBox.getSelectedItem();
        if (supplier == null) throw new BusinessException("Chưa chọn nhà cung cấp");
        double daTra = Forms.parseNumber(paid.getText(), "Số tiền đã thanh toán");
        List<PurchaseLine> items = cart.stream()
                .map(l -> new PurchaseLine(l.maSanPham(), l.soLuong(), l.donGiaNhap())).toList();
        int maPN = app.purchases.createReceipt(supplier.id(), daTra, items);
        Ui.info(this, "Đã lập phiếu nhập #" + maPN);
        cart.clear();
        lines.setItems(cart);
        paid.setText("0");
        updateTotals();
        refresh();
    }

    private void updateTotals() {
        double total = cart.stream().mapToDouble(Line::thanhTien).sum();
        double daTra = parseOr(paid.getText(), 0);
        totalLabel.setText("Tổng tiền phiếu: " + Ui.money(total));
        debtLabel.setText("Còn nợ nhà cung cấp: " + Ui.money(Math.max(0, total - daTra)));
    }

    private static double parseOr(String text, double fallback) {
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
