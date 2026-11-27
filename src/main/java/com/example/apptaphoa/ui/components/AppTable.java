package com.example.apptaphoa.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Bảng dữ liệu có ô tìm kiếm và sắp xếp cột.
 * Mỗi cột là một Column: tiêu đề và hàm lấy giá trị từ đối tượng T.
 */
public final class AppTable<T> extends JPanel {

    public record Column<T>(String title, Function<T, ?> value) {}

    private final List<Column<T>> columns;
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField search = new JTextField(20);
    private List<T> items = List.of();

    public AppTable(List<Column<T>> columns) {
        super(new BorderLayout(0, 8));
        this.columns = columns;
        String[] titles = columns.stream().map(Column::title).toArray(String[]::new);
        this.model = new DefaultTableModel(titles, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.table = new JTable(model);
        this.table.setRowHeight(26);
        this.table.setAutoCreateRowSorter(false);
        this.sorter = new TableRowSorter<>(model);
        this.table.setRowSorter(sorter);
        this.table.getTableHeader().setReorderingAllowed(false);

        search.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(search, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        add(scroll, BorderLayout.CENTER);
    }

    public void setItems(List<T> newItems) {
        this.items = List.copyOf(newItems);
        model.setRowCount(0);
        for (T item : items) {
            Object[] row = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                row[i] = columns.get(i).value().apply(item);
            }
            model.addRow(row);
        }
    }

    /** Phần tử đang chọn (đã tính sắp xếp và lọc). */
    public Optional<T> selected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return Optional.empty();
        return Optional.of(items.get(table.convertRowIndexToModel(viewRow)));
    }

    public List<T> items() {
        return items;
    }

    public JTable table() {
        return table;
    }

    private void applyFilter() {
        String text = search.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }
}
