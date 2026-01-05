package Qly.view.panel;

import Qly.dao.NhapHangDao;
import Qly.dao.ProductDao;
import Qly.model.ChiTietNhapHang;
import Qly.model.NhapHang;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Form panel to create new nhập hàng receipts and update stock.
 */
public class NhapHangFormPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final NhapHangDao nhapHangDao;
    private final ProductDao productDao;
    private final User currentUser;

    public NhapHangFormPanelBuilder(Color cardBackground,
                                    Color headingColor,
                                    NhapHangDao nhapHangDao,
                                    ProductDao productDao,
                                    User currentUser) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.nhapHangDao = nhapHangDao;
        this.productDao = productDao;
        this.currentUser = currentUser;
    }

    public JPanel build() {
        return new FormPanel().root;
    }

    private class FormPanel {
        private final JPanel root;
        private final JLabel receiptIdValue;
        private final JLabel createdByValue;
        private final JTextField supplierField;
        private final JTextField productField;
        private final JTextField quantityField;
        private final JTextField priceField;
        private final JLabel totalValue;
        private final JLabel messageLabel;
        private final DefaultTableModel tableModel;
        private final List<ChiTietNhapHang> lineItems = new ArrayList<>();

        private String currentReceiptId;

        FormPanel() {
            root = new JPanel(new BorderLayout(20, 20));
            root.setBackground(cardBackground);
            root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            JLabel title = new JLabel("LẬP PHIẾU NHẬP HÀNG");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(headingColor);

            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new java.awt.Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            receiptIdValue = new JLabel();
            createdByValue = new JLabel(currentUser.getUsername());
            supplierField = new JTextField();

            addField(infoPanel, gbc, 0, 0, "Mã phiếu nhập", receiptIdValue);
            addField(infoPanel, gbc, 2, 0, "Nhà cung cấp", supplierField);
            addField(infoPanel, gbc, 0, 1, "Người lập", createdByValue);

            JPanel productInput = new JPanel(new GridLayout(1, 7, 10, 0));
            productInput.setOpaque(false);
            productField = new JTextField();
            quantityField = new JTextField();
            priceField = new JTextField();
            JButton btnAdd = new JButton("Thêm sản phẩm");
            stylePrimaryButton(btnAdd, new Color(40, 167, 69));
            btnAdd.addActionListener(e -> addProduct());

            productInput.add(new JLabel("Mã sản phẩm:"));
            productInput.add(productField);
            productInput.add(new JLabel("Số lượng:"));
            productInput.add(quantityField);
            productInput.add(new JLabel("Đơn giá nhập:"));
            productInput.add(priceField);
            productInput.add(btnAdd);

            tableModel = new DefaultTableModel(new String[]{
                "Mã SP", "Tên SP", "Số lượng", "Đơn giá nhập", "Thành tiền"
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

            JPanel bottom = new JPanel();
            bottom.setOpaque(false);
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            totalValue = new JLabel();
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
            totalValue.setForeground(new Color(40, 167, 69));
            bottom.add(totalValue);
            bottom.add(Box.createHorizontalGlue());

            JButton btnCreate = new JButton("Tạo phiếu nhập");
            stylePrimaryButton(btnCreate, new Color(44, 182, 125));
            btnCreate.addActionListener(e -> submitReceipt());
            bottom.add(btnCreate);

            messageLabel = new JLabel();
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            messageLabel.setVisible(false);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.add(infoPanel);
            center.add(Box.createVerticalStrut(18));
            center.add(productInput);
            center.add(Box.createVerticalStrut(18));
            center.add(new JScrollPane(table));
            center.add(Box.createVerticalStrut(12));
            center.add(bottom);
            center.add(Box.createVerticalStrut(8));
            center.add(messageLabel);

            root.add(title, BorderLayout.NORTH);
            root.add(center, BorderLayout.CENTER);

            refreshReceiptId();
            updateTotal();
        }

        private void addProduct() {
            hideMessage();
            String maSP = productField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String priceText = priceField.getText().trim();
            if (maSP.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
                showError("Vui lòng nhập mã SP, số lượng và giá nhập");
                return;
            }
            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(quantityText);
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                showError("Số lượng hoặc giá nhập không hợp lệ");
                return;
            }
            if (quantity <= 0 || price < 0) {
                showError("Số lượng phải > 0 và giá >= 0");
                return;
            }

            SanPham product = productDao.findById(maSP);
            if (product == null) {
                showError("Không tìm thấy sản phẩm");
                return;
            }

            ChiTietNhapHang item = new ChiTietNhapHang(currentReceiptId, maSP, quantity, price);
            lineItems.add(item);
            double lineTotal = quantity * price;
            tableModel.addRow(new Object[]{
                maSP,
                product.getTenSP(),
                quantity,
                formatCurrency(price),
                formatCurrency(lineTotal)
            });
            productField.setText("");
            quantityField.setText("");
            priceField.setText("");
            updateTotal();
        }

        private void submitReceipt() {
            hideMessage();
            if (lineItems.isEmpty()) {
                showError("Danh sách sản phẩm trống");
                return;
            }
            if (supplierField.getText().trim().isEmpty()) {
                showError("Vui lòng nhập mã nhà cung cấp");
                return;
            }
            NhapHang nhapHang = new NhapHang(
                currentReceiptId,
                supplierField.getText().trim(),
                currentUser.getUserId(),
                new Date(System.currentTimeMillis())
            );
            boolean success = nhapHangDao.createNhapHang(nhapHang, lineItems);
            if (success) {
                showSuccess("Tạo phiếu nhập thành công");
                resetForm();
            } else {
                showError("Không thể tạo phiếu nhập. Vui lòng thử lại");
            }
        }

        private void resetForm() {
            lineItems.clear();
            tableModel.setRowCount(0);
            supplierField.setText("");
            refreshReceiptId();
            updateTotal();
        }

        private void refreshReceiptId() {
            currentReceiptId = nhapHangDao.generateNhapHangId();
            receiptIdValue.setText(currentReceiptId);
        }

        private void updateTotal() {
            double total = lineItems.stream()
                .mapToDouble(item -> item.getSoLuong() * item.getDonGiaNhap())
                .sum();
            totalValue.setText("Tổng tiền: " + formatCurrency(total));
        }

        private void addField(JPanel panel, GridBagConstraints gbc, int x, int y,
                               String labelText, java.awt.Component component) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            gbc.gridx = x;
            gbc.gridy = y;
            panel.add(label, gbc);

            gbc.gridx = x + 1;
            gbc.weightx = 1;
            component.setPreferredSize(new Dimension(220, 32));
            panel.add(component, gbc);
        }

        private void stylePrimaryButton(JButton btn, Color bg) {
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        private String formatCurrency(double value) {
            return String.format("%,.0f", value);
        }

        private void hideMessage() {
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
    }
}
