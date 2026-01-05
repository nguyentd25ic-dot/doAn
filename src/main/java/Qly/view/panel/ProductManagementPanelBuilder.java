package Qly.view.panel;

import Qly.dao.ProductDao;
import Qly.model.SanPham;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.Date;

/**
 * CRUD form for managing products.
 */
public class ProductManagementPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final ProductDao productDao;

    public ProductManagementPanelBuilder(Color cardBackground, Color headingColor, ProductDao productDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.productDao = productDao;
    }

    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("QUẢN LÍ SẢN PHẨM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(headingColor);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtGia = new JTextField();
        JTextField txtSoLuong = new JTextField();
        JTextField txtDonVi = new JTextField();
        JTextField txtHan = new JTextField();
        JTextField txtNcc = new JTextField();

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        addField(form, gbc, 0, 0, "Mã sản phẩm", txtMa, labelFont);
        addField(form, gbc, 2, 0, "Tên sản phẩm", txtTen, labelFont);
        addField(form, gbc, 0, 1, "Đơn giá", txtGia, labelFont);
        addField(form, gbc, 2, 1, "Số lượng", txtSoLuong, labelFont);
        addField(form, gbc, 0, 2, "Đơn vị", txtDonVi, labelFont);
        addField(form, gbc, 2, 2, "Hạn sử dụng (yyyy-mm-dd)", txtHan, labelFont);
        addField(form, gbc, 0, 3, "Mã NCC", txtNcc, labelFont);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");

        styleActionButton(btnThem, new Color(40, 167, 69));
        styleActionButton(btnSua, new Color(255, 193, 7));
        styleActionButton(btnXoa, new Color(220, 53, 69));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);

        JLabel message = new JLabel();
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(new Color(40, 167, 69));
        message.setVisible(false);

        btnThem.addActionListener(e -> {
            productDao.insert(buildProduct(txtMa, txtTen, txtGia, txtSoLuong, txtDonVi, txtHan, txtNcc));
            showMessage(message, "Thêm sản phẩm thành công", Color.GREEN);
        });

        btnSua.addActionListener(e -> {
            productDao.update(buildProduct(txtMa, txtTen, txtGia, txtSoLuong, txtDonVi, txtHan, txtNcc));
            showMessage(message, "Cập nhật sản phẩm thành công", Color.GREEN);
        });

        btnXoa.addActionListener(e -> {
            productDao.deleteById(txtMa.getText());
            showMessage(message, "Xóa sản phẩm thành công", Color.GREEN);
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(message, BorderLayout.WEST);
        return panel;
    }

    private SanPham buildProduct(JTextField txtMa, JTextField txtTen, JTextField txtGia,
                                 JTextField txtSoLuong, JTextField txtDonVi,
                                 JTextField txtHan, JTextField txtNcc) {
        Date han = Date.valueOf(txtHan.getText().trim());
        return new SanPham(
            txtMa.getText(),
            txtTen.getText(),
            Double.parseDouble(txtGia.getText()),
            Integer.parseInt(txtSoLuong.getText()),
            txtDonVi.getText(),
            han,
            txtNcc.getText()
        );
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int x, int y,
                          String labelText, JTextField field, Font labelFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);

        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(label, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(200, 36));
        panel.add(field, gbc);
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
    }

    private void showMessage(JLabel label, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
        label.setVisible(true);

        Timer timer = new Timer(3000, e -> label.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }
}
