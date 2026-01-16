package Qly.view.panel;

import Qly.dao.ProductDao;
import Qly.model.SanPham;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

// Panel tra cứu sản phẩm, lọc kết quả ngay khi người dùng gõ.
public class SearchPanelBuilder {
    private static final String PLACEHOLDER = "Nhập mã hoặc tên sản phẩm";

    private final Color cardBackground;
    private final Color headingColor;
    private final ProductDao productDao;

    public SearchPanelBuilder(Color cardBackground, Color headingColor, ProductDao productDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.productDao = productDao;
    }

    // Dựng ô tìm kiếm và bảng kết quả.
    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("TRA CỨU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(headingColor);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setText(PLACEHOLDER);
        searchField.addFocusListener(new PlaceholderFocusListener(searchField, PLACEHOLDER));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(searchField);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã SP", "Tên SP", "Số lượng", "Đơn vị tính", "Hạn sử dụng", "Trạng Thái", "Mã nhà cung cấp"}, 0
        );

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.setPreferredScrollableViewportSize(new Dimension(450, 300));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter(model, searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter(model, searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter(model, searchField.getText());
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // Lọc danh sách sản phẩm dựa trên từ khóa.
    private void filter(DefaultTableModel model, String rawKey) {
        String key = rawKey == null ? "" : rawKey.trim().toLowerCase();
        model.setRowCount(0);
        if (key.isEmpty() || PLACEHOLDER.equals(rawKey)) {
            return;
        }
        List<SanPham> products = productDao.findAll();
        for (SanPham sp : products) {
            if (matches(sp, key)) {
                model.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getSoLuong(),
                    sp.getDonViTinh(),
                    sp.getHanSuDung(),
                    sp.getTrangThai(),
                    sp.getSupplierID()
                });
            }
        }
    }

    private boolean matches(SanPham sp, String key) {
        return sp.getMaSP().toLowerCase().contains(key) || sp.getTenSP().toLowerCase().contains(key);
    }

    private static class PlaceholderFocusListener extends java.awt.event.FocusAdapter {
        private final JTextField field;
        private final String placeholder;

        PlaceholderFocusListener(JTextField field, String placeholder) {
            this.field = field;
            this.placeholder = placeholder;
        }

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (placeholder.equals(field.getText())) {
                field.setText("");
                field.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getText().trim().isEmpty()) {
                field.setText(placeholder);
                field.setForeground(Color.GRAY);
            }
        }
    }
}
