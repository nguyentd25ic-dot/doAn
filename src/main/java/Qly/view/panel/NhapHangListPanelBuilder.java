package Qly.view.panel;

import Qly.dao.NhapHangDao;
import Qly.model.NhapHang;
import Qly.model.NhapHangDetail;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// Hiển thị lịch sử nhập hàng và lọc nhanh theo mã phiếu.
public class NhapHangListPanelBuilder {
    private static final String PLACEHOLDER = "Nhập mã nhập";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color cardBackground;
    private final Color headingColor;
    private final NhapHangDao nhapHangDao;

    public NhapHangListPanelBuilder(Color cardBackground, Color headingColor, NhapHangDao nhapHangDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.nhapHangDao = nhapHangDao;
    }

    // Dựng bảng chính, ô tìm kiếm và nút xem chi tiết.
    public JPanel build() {
        List<NhapHang> allEntries = nhapHangDao.findAll();
        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Mã nhập", "Nhà cung cấp", "Người lập", "Ngày nhập"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        populate(model, allEntries);

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("DANH SÁCH NHẬP HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(headingColor);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setText(PLACEHOLDER);
        searchField.addFocusListener(new PlaceholderFocusListener(searchField, PLACEHOLDER));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String raw = searchField.getText();
                String key = raw == null ? "" : raw.trim().toLowerCase();
                if (key.isEmpty() || PLACEHOLDER.equals(raw)) {
                    populate(model, allEntries);
                    return;
                }
                List<NhapHang> filtered = allEntries.stream()
                    .filter(item -> item.getMaNhap() != null && item.getMaNhap().toLowerCase().contains(key))
                    .collect(Collectors.toList());
                populate(model, filtered);
            }
        });

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(10));
        header.add(searchField);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setPreferredScrollableViewportSize(new Dimension(520, 320));

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton detailButton = new JButton("Xem chi tiết");
        detailButton.addActionListener(e -> showDetails(panel, table));
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(detailButton, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    // Đổ dữ liệu vào bảng từ danh sách truyền vào.
    private void populate(DefaultTableModel model, List<NhapHang> source) {
        model.setRowCount(0);
        for (NhapHang nh : source) {
            model.addRow(new Object[]{
                nh.getMaNhap(),
                nh.getSupplierID(),
                nh.getUserID(),
                nh.getNgayNhap() == null ? "" : DATE_FORMAT.format(nh.getNgayNhap().toLocalDate())
            });
        }
    }

    // Mở dialog chi tiết các mặt hàng trong phiếu nhập.
    private void showDetails(JPanel parent, JTable table) {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn phiếu nhập", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maNhap = table.getValueAt(selected, 0).toString();
        List<NhapHangDetail> details = nhapHangDao.findDetails(maNhap);
        DefaultTableModel detailModel = new DefaultTableModel(new String[]{
            "Mã SP", "Tên SP", "Số lượng", "Đơn giá nhập", "Thành tiền"
        }, 0);
        for (NhapHangDetail item : details) {
            detailModel.addRow(new Object[]{
                item.getMaSP(),
                item.getTenSP(),
                item.getSoLuong(),
                formatCurrency(item.getDonGiaNhap()),
                formatCurrency(item.getThanhTien())
            });
        }
        if (detailModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Phiếu nhập chưa có chi tiết", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(26);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setPreferredSize(new Dimension(520, 260));
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(parent),
            scrollPane,
            "Chi tiết nhập hàng " + maNhap,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String formatCurrency(double value) {
        return String.format("%,.0f", value);
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
