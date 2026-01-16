package Qly.view.panel;

import Qly.dao.HoaDonDao;
import Qly.dao.ProductDao;
import Qly.model.ChiTietHoaDon;
import Qly.model.HoaDon;
import Qly.model.SanPham;
import Qly.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Dựng màn hình "Lập hóa đơn" với sinh mã tự động và danh sách sản phẩm.
public class InvoicePanelBuilder {
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Color cardBackground;
    private final Color headingColor;
    private final HoaDonDao hoaDonDao;
    private final ProductDao productDao;
    private final User currentUser;

    public InvoicePanelBuilder(Color cardBackground,
                               Color headingColor,
                               HoaDonDao hoaDonDao,
                               ProductDao productDao,
                               User currentUser) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.hoaDonDao = hoaDonDao;
        this.productDao = productDao;
        this.currentUser = currentUser;
    }

    public JPanel build() {
        return new InvoicePanel().getRootPanel();
    }

    private class InvoicePanel {
        private final JPanel root;
        private final JLabel invoiceIdValue;
        private final JLabel timestampValue;
        private final JLabel createdByValue;
        private final JTextField customerField;
        private final JTextField productField;
        private final JTextField quantityField;
        private final JLabel totalValue;
        private final JLabel messageLabel;
        private final DefaultTableModel tableModel;
        private final List<ChiTietHoaDon> lineItems = new ArrayList<>();

        private String currentInvoiceId;
        private Timestamp currentTimestamp;

        InvoicePanel() {
            root = new JPanel(new BorderLayout(20, 20));
            root.setBackground(cardBackground);
            root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            JLabel title = new JLabel("LẬP HÓA ĐƠN");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(headingColor);

            // Khối thông tin chung của hóa đơn.
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new java.awt.Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            invoiceIdValue = new JLabel();
            timestampValue = new JLabel();
            createdByValue = new JLabel(currentUser.getUsername());
            customerField = new JTextField();

            addInfoField(infoPanel, gbc, 0, 0, "Mã hóa đơn", invoiceIdValue);
            addInfoField(infoPanel, gbc, 2, 0, "Thời gian", timestampValue);
            addInfoField(infoPanel, gbc, 0, 1, "Người lập", createdByValue);
            addInfoField(infoPanel, gbc, 2, 1, "Mã khách hàng", customerField);

            // Hàng nhập mã sản phẩm + số lượng.
            JPanel productInput = new JPanel(new GridLayout(1, 5, 12, 0));
            productInput.setOpaque(false);
            productField = new JTextField();
            quantityField = new JTextField();
            JButton btnAddProduct = new JButton("Thêm sản phẩm");
            stylePrimaryButton(btnAddProduct, new Color(40, 167, 69));
            btnAddProduct.addActionListener(e -> addProduct());

            productInput.add(new JLabel("Mã sản phẩm:"));
            productInput.add(productField);
            productInput.add(new JLabel("Số lượng:"));
            productInput.add(quantityField);
            productInput.add(btnAddProduct);

            tableModel = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"
            }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(tableModel);
            table.setRowHeight(28);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            // Phần tổng tiền và nút tạo hóa đơn.
            JPanel summaryPanel = new JPanel();
            summaryPanel.setOpaque(false);
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
            totalValue = new JLabel();
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
            totalValue.setForeground(new Color(40, 167, 69));
            summaryPanel.add(totalValue);
            summaryPanel.add(Box.createHorizontalGlue());

            JButton btnCreateInvoice = new JButton("Tạo hóa đơn");
            stylePrimaryButton(btnCreateInvoice, new Color(44, 182, 125));
            btnCreateInvoice.addActionListener(e -> submitInvoice());
            summaryPanel.add(btnCreateInvoice);

            messageLabel = new JLabel();
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            messageLabel.setVisible(false);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.add(infoPanel);
            center.add(Box.createVerticalStrut(20));
            center.add(productInput);
            center.add(Box.createVerticalStrut(20));
            center.add(new JScrollPane(table));
            center.add(Box.createVerticalStrut(15));
            center.add(summaryPanel);
            center.add(Box.createVerticalStrut(10));
            center.add(messageLabel);

            root.add(title, BorderLayout.NORTH);
            root.add(center, BorderLayout.CENTER);

            refreshInvoiceMetadata();
            updateTotalLabel();
        }

        JPanel getRootPanel() {
            return root;
        }

        // Thêm một mặt hàng vào danh sách tạm trước khi lưu.
        private void addProduct() {
            clearMessage();
            String maSP = productField.getText().trim();
            String quantityText = quantityField.getText().trim();
            if (maSP.isEmpty() || quantityText.isEmpty()) {
                showError("Vui lòng nhập mã sản phẩm và số lượng");
                return;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException ex) {
                showError("Số lượng không hợp lệ");
                return;
            }
            if (quantity <= 0) {
                showError("Số lượng phải lớn hơn 0");
                return;
            }

            SanPham product = productDao.findById(maSP);
            if (product == null) {
                showError("Không tìm thấy sản phẩm");
                return;
            }

            int totalRequested = quantity + pendingQuantityForProduct(maSP);
            if (product.getSoLuong() < totalRequested) {
                showError("Kho chỉ còn " + product.getSoLuong() + " sản phẩm khả dụng");
                return;
            }

            ChiTietHoaDon item = new ChiTietHoaDon(currentInvoiceId, maSP, quantity, product.getDonGia());
            lineItems.add(item);
            double lineTotal = product.getDonGia() * quantity;
            tableModel.addRow(new Object[]{
                maSP,
                product.getTenSP(),
                quantity,
                formatCurrency(product.getDonGia()),
                formatCurrency(lineTotal)
            });
            updateTotalLabel();
            productField.setText("");
            quantityField.setText("");
        }

        // Gửi dữ liệu xuống DAO để ghi hóa đơn và chi tiết trong DB.
        private void submitInvoice() {
            clearMessage();
            if (lineItems.isEmpty()) {
                showError("Vui lòng thêm ít nhất 1 sản phẩm");
                return;
            }
            double total = lineItems.stream()
                .mapToDouble(item -> item.getDonGia() * item.getSoLuong())
                .sum();
            HoaDon hoaDon = new HoaDon(
                currentInvoiceId,
                customerField.getText().trim(),
                currentUser.getUserId(),
                currentTimestamp,
                total
            );
            boolean success;
            try {
                success = hoaDonDao.createInvoice(hoaDon, lineItems);
            } catch (IllegalStateException ex) {
                showError(ex.getMessage());
                return;
            }
            if (success) {
                showSuccess("Tạo hóa đơn thành công");
                resetForm();
            } else {
                showError("Không thể tạo hóa đơn. Vui lòng thử lại");
            }
        }

        // Xóa sạch form sau khi tạo hóa đơn thành công.
        private void resetForm() {
            lineItems.clear();
            tableModel.setRowCount(0);
            customerField.setText("");
            refreshInvoiceMetadata();
            updateTotalLabel();
        }

        // Sinh lại mã hóa đơn và timestamp hiện tại.
        private void refreshInvoiceMetadata() {
            currentInvoiceId = hoaDonDao.generateInvoiceId();
            currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
            invoiceIdValue.setText(currentInvoiceId);
            timestampValue.setText(DISPLAY_TIME_FORMAT.format(currentTimestamp.toLocalDateTime()));
        }

        // Cập nhật label tổng tiền dựa trên lineItems.
        private void updateTotalLabel() {
            double total = lineItems.stream()
                .mapToDouble(item -> item.getDonGia() * item.getSoLuong())
                .sum();
            totalValue.setText("Tổng tiền: " + formatCurrency(total));
        }

        private void stylePrimaryButton(JButton button, Color bg) {
            button.setBackground(bg);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        private void addInfoField(JPanel panel, GridBagConstraints gbc, int x, int y,
                                   String labelText, java.awt.Component component) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            gbc.gridx = x;
            gbc.gridy = y;
            panel.add(label, gbc);

            gbc.gridx = x + 1;
            gbc.weightx = 1;
            component.setPreferredSize(new Dimension(200, 32));
            panel.add(component, gbc);
        }

        private String formatCurrency(double value) {
            return String.format("%,.0f", value);
        }

        private void clearMessage() {
            messageLabel.setVisible(false);
        }

        private void showError(String text) {
            messageLabel.setForeground(new Color(220, 53, 69));
            messageLabel.setText(text);
            messageLabel.setVisible(true);
        }

        private void showSuccess(String text) {
            messageLabel.setForeground(new Color(40, 167, 69));
            messageLabel.setText(text);
            messageLabel.setVisible(true);
        }

        private int pendingQuantityForProduct(String maSP) {
            return lineItems.stream()
                .filter(item -> item.getMaSP().equalsIgnoreCase(maSP))
                .mapToInt(ChiTietHoaDon::getSoLuong)
                .sum();
        }
    }
}
