package Qly.view.panel;

import Qly.dao.HoaDonDao;
import Qly.model.HoaDon;
import Qly.model.InvoiceDetail;

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

// Hiển thị danh sách hóa đơn và cho phép lọc nhanh theo mã.
public class InvoiceListPanelBuilder {
    private static final String PLACEHOLDER = "Nhập mã hóa đơn";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Color cardBackground;
    private final Color headingColor;
    private final HoaDonDao hoaDonDao;

    public InvoiceListPanelBuilder(Color cardBackground, Color headingColor, HoaDonDao hoaDonDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.hoaDonDao = hoaDonDao;
    }

    // Lắp ráp bảng chính, ô tìm kiếm và nút xem chi tiết.
    public JPanel build() {
        List<HoaDon> allInvoices = hoaDonDao.findAll();
        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Mã HD", "Mã KH", "Người lập", "Ngày lập", "Tổng tiền"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        populateTable(model, allInvoices);

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("DANH SÁCH HÓA ĐƠN");
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
                    populateTable(model, allInvoices);
                    return;
                }
                List<HoaDon> filtered = allInvoices.stream()
                    .filter(hd -> hd.getMaHD() != null && hd.getMaHD().toLowerCase().contains(key))
                    .collect(Collectors.toList());
                populateTable(model, filtered);
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
        table.setPreferredScrollableViewportSize(new Dimension(500, 320));

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton detailButton = new JButton("Xem chi tiết");
        detailButton.addActionListener(e -> showInvoiceDetails(panel, table));
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(detailButton, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    // Đổ dữ liệu vào bảng theo nguồn truyền vào.
    private void populateTable(DefaultTableModel model, List<HoaDon> source) {
        model.setRowCount(0);
        for (HoaDon hd : source) {
            model.addRow(new Object[]{
                hd.getMaHD(),
                hd.getMaKH(),
                hd.getUserID(),
                hd.getNgayLap() == null ? "" : TIME_FORMAT.format(hd.getNgayLap().toLocalDateTime()),
                formatCurrency(hd.getTongTien())
            });
        }
    }

    private String formatCurrency(double value) {
        return String.format("%,.0f", value);
    }

    // Bật dialog chi tiết gồm các dòng sản phẩm của hóa đơn.
    private void showInvoiceDetails(JPanel parent, JTable table) {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn một hóa đơn", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maHD = table.getValueAt(selected, 0).toString();
        List<InvoiceDetail> details = hoaDonDao.findDetails(maHD);
        DefaultTableModel detailModel = new DefaultTableModel(new String[]{
            "Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"
        }, 0);
        for (var item : details) {
            detailModel.addRow(new Object[]{
                item.getMaSP(),
                item.getTenSP(),
                item.getSoLuong(),
                formatCurrency(item.getDonGia()),
                formatCurrency(item.getThanhTien())
            });
        }
        if (detailModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Hóa đơn chưa có chi tiết", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(26);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setPreferredSize(new Dimension(500, 260));
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(parent),
            scrollPane,
            "Chi tiết hóa đơn " + maHD,
            JOptionPane.INFORMATION_MESSAGE
        );
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
