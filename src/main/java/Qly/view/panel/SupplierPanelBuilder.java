package Qly.view.panel;

import Qly.dao.SupplierDao;
import Qly.model.Supplier;

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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

// Panel quản lý CRUD nhà cung cấp, gồm bảng và form chỉnh sửa.
public class SupplierPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final SupplierDao supplierDao;

    public SupplierPanelBuilder(Color cardBackground, Color headingColor, SupplierDao supplierDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.supplierDao = supplierDao;
    }

    public JPanel build() {
        return new SupplierPanel().root;
    }

    private class SupplierPanel {
        private final JPanel root;
        private final DefaultTableModel tableModel;
        private final JTable table;
        private final JTextField txtId;
        private final JTextField txtName;
        private final JTextField txtPhone;
        private final JTextField txtEmail;
        private final JTextField txtAddress;
        private final JLabel messageLabel;

        SupplierPanel() {
            root = new JPanel(new BorderLayout(20, 20));
            root.setBackground(cardBackground);
            root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            JLabel title = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            title.setForeground(headingColor);
            root.add(title, BorderLayout.NORTH);

            tableModel = new DefaultTableModel(new String[]{"ID", "Tên", "SĐT", "Email", "Địa chỉ"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(tableModel);
            table.setRowHeight(26);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());
            root.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new java.awt.Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtId = new JTextField();
            txtName = new JTextField();
            txtPhone = new JTextField();
            txtEmail = new JTextField();
            txtAddress = new JTextField();

            addField(formPanel, gbc, 0, 0, "Mã nhà cung cấp", txtId);
            addField(formPanel, gbc, 2, 0, "Tên nhà cung cấp", txtName);
            addField(formPanel, gbc, 0, 1, "Số điện thoại", txtPhone);
            addField(formPanel, gbc, 2, 1, "Email", txtEmail);
            addField(formPanel, gbc, 0, 2, "Địa chỉ", txtAddress);

            JButton btnAdd = new JButton("Thêm");
            JButton btnUpdate = new JButton("Sửa");
            JButton btnDelete = new JButton("Xóa");
            styleButton(btnAdd, new Color(40, 167, 69));
            styleButton(btnUpdate, new Color(255, 193, 7));
            styleButton(btnDelete, new Color(220, 53, 69));

            btnAdd.addActionListener(e -> insertSupplier());
            btnUpdate.addActionListener(e -> updateSupplier());
            btnDelete.addActionListener(e -> deleteSupplier());

            JPanel buttonRow = new JPanel();
            buttonRow.setOpaque(false);
            buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
            buttonRow.add(btnAdd);
            buttonRow.add(Box.createHorizontalStrut(10));
            buttonRow.add(btnUpdate);
            buttonRow.add(Box.createHorizontalStrut(10));
            buttonRow.add(btnDelete);

            messageLabel = new JLabel();
            messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            messageLabel.setVisible(false);

            JPanel south = new JPanel();
            south.setOpaque(false);
            south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
            south.add(formPanel);
            south.add(Box.createVerticalStrut(12));
            south.add(buttonRow);
            south.add(Box.createVerticalStrut(8));
            south.add(messageLabel);

            root.add(south, BorderLayout.SOUTH);
            reloadTable();
        }

        // Tải lại dữ liệu nhà cung cấp vào bảng.
        private void reloadTable() {
            tableModel.setRowCount(0);
            for (Supplier sup : supplierDao.getSuppliers()) {
                tableModel.addRow(new Object[]{
                    sup.getSupplierID(),
                    sup.getSupplierName(),
                    sup.getPhone(),
                    sup.getEmail(),
                    sup.getAddress()
                });
            }
        }

        // Khi chọn trên bảng sẽ tự đổ xuống form.
        private void fillFormFromSelection() {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            txtId.setText(value(row, 0));
            txtName.setText(value(row, 1));
            txtPhone.setText(value(row, 2));
            txtEmail.setText(value(row, 3));
            txtAddress.setText(value(row, 4));
        }

        private String value(int row, int column) {
            Object v = tableModel.getValueAt(row, column);
            return v == null ? "" : v.toString();
        }

        private void insertSupplier() {
            hideMessage();
            Supplier supplier = buildSupplierFromForm();
            if (supplier == null) {
                return;
            }
            if (supplierDao.insertSupplier(supplier)) {
                showMessage("Thêm nhà cung cấp thành công", new Color(40, 167, 69));
                clearForm();
                reloadTable();
            } else {
                showMessage("Không thể thêm nhà cung cấp", new Color(220, 53, 69));
            }
        }

        private void updateSupplier() {
            hideMessage();
            Supplier supplier = buildSupplierFromForm();
            if (supplier == null) {
                return;
            }
            if (supplierDao.updateSupplier(supplier)) {
                showMessage("Cập nhật thành công", new Color(40, 167, 69));
                reloadTable();
            } else {
                showMessage("Không thể cập nhật", new Color(220, 53, 69));
            }
        }

        private void deleteSupplier() {
            hideMessage();
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                showMessage("Vui lòng chọn nhà cung cấp", new Color(220, 53, 69));
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(root, "Xóa nhà cung cấp " + id + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            if (supplierDao.deleteSupplier(id)) {
                showMessage("Đã xóa", new Color(40, 167, 69));
                clearForm();
                reloadTable();
            } else {
                showMessage("Không thể xóa", new Color(220, 53, 69));
            }
        }

        // Ghép dữ liệu từ form thành đối tượng Supplier.
        private Supplier buildSupplierFromForm() {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            if (id.isEmpty() || name.isEmpty()) {
                showMessage("Mã và tên nhà cung cấp không được để trống", new Color(220, 53, 69));
                return null;
            }
            Supplier supplier = new Supplier();
            supplier.setSupplierID(id);
            supplier.setSupplierName(name);
            supplier.setPhone(txtPhone.getText().trim());
            supplier.setEmail(txtEmail.getText().trim());
            supplier.setAddress(txtAddress.getText().trim());
            return supplier;
        }

        private void clearForm() {
            txtId.setText("");
            txtName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtAddress.setText("");
            table.clearSelection();
        }

        private void addField(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JTextField field) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            gbc.gridx = x;
            gbc.gridy = y;
            panel.add(lbl, gbc);

            gbc.gridx = x + 1;
            gbc.weightx = 1;
            field.setPreferredSize(new Dimension(200, 30));
            panel.add(field, gbc);
        }

        private void styleButton(JButton button, Color bg) {
            button.setBackground(bg);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        private void showMessage(String text, Color color) {
            messageLabel.setText(text);
            messageLabel.setForeground(color);
            messageLabel.setVisible(true);
        }

        private void hideMessage() {
            messageLabel.setVisible(false);
        }
    }
}
