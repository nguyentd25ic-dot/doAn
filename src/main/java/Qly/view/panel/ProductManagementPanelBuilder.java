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

// Form CRUD để quản lý danh sách sản phẩm.
public class ProductManagementPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final ProductDao productDao;

    public ProductManagementPanelBuilder(Color cardBackground, Color headingColor, ProductDao productDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.productDao = productDao;
    }

    // Lắp ráp form nhập liệu và các nút hành động.
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

        btnThem.addActionListener(e -> handleProductAction(message, () -> {
            productDao.insert(buildProduct(txtMa, txtTen, txtGia, txtSoLuong, txtDonVi, txtHan, txtNcc));
            showMessage(message, "Thêm sản phẩm thành công", new Color(40, 167, 69));
        }));

        btnSua.addActionListener(e -> handleProductAction(message, () -> {
            productDao.update(buildProduct(txtMa, txtTen, txtGia, txtSoLuong, txtDonVi, txtHan, txtNcc));
            showMessage(message, "Cập nhật sản phẩm thành công", new Color(40, 167, 69));
        }));

        btnXoa.addActionListener(e -> handleProductAction(message, () -> {
            String id = txtMa.getText().trim();
            if (id.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập mã sản phẩm để xóa");
            }
            productDao.deleteById(id);
            showMessage(message, "Xóa sản phẩm thành công", new Color(40, 167, 69));
        }));

        panel.add(title, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(message, BorderLayout.WEST);
        return panel;
    }

    // Tạo đối tượng SanPham từ giá trị các ô input.
    private SanPham buildProduct(JTextField txtMa, JTextField txtTen, JTextField txtGia,
                                 JTextField txtSoLuong, JTextField txtDonVi,
                                 JTextField txtHan, JTextField txtNcc) {
        String ma = requireText(txtMa, "mã sản phẩm");
        String ten = requireText(txtTen, "tên sản phẩm");
        String donVi = requireText(txtDonVi, "đơn vị");
        String ncc = requireText(txtNcc, "mã nhà cung cấp");

        double gia;
        try {
            gia = Double.parseDouble(requireText(txtGia, "đơn giá"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Đơn giá phải là số hợp lệ");
        }
        if (gia < 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(requireText(txtSoLuong, "số lượng"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng phải là số nguyên");
        }
        if (soLuong < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }

        Date han;
        try {
            han = Date.valueOf(requireText(txtHan, "hạn sử dụng"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Hạn sử dụng phải theo định dạng yyyy-mm-dd");
        }

        return new SanPham(ma, ten, gia, soLuong, donVi, han, ncc);
    }

    private String requireText(JTextField field, String label) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập " + label);
        }
        return value;
    }

    // Tiện ích thêm label + textfield theo GridBag.
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

    // Hiển thị thông báo ngắn rồi tự ẩn sau 3 giây.
    private void handleProductAction(JLabel messageLabel, Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException ex) {
            showMessage(messageLabel, ex.getMessage(), new Color(220, 53, 69));
        } catch (Exception ex) {
            showMessage(messageLabel, "Có lỗi xảy ra. Vui lòng thử lại", new Color(220, 53, 69));
        }
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
