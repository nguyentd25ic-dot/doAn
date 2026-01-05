package Qly.view.panel;

import Qly.dao.ProductDao;
import Qly.model.SanPham;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Builds the inventory (kho) panel so DashBoard stays lean.
 */
public class InventoryPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final ProductDao productDao;

    public InventoryPanelBuilder(Color cardBackground, Color headingColor, ProductDao productDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.productDao = productDao;
    }

    public JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("KHO HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(headingColor);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã SP", "Tên SP", "Số lượng", "Hạn sử dụng", "Trạng thái"}, 0
        );

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.setPreferredScrollableViewportSize(new Dimension(450, 300));

        for (SanPham sp : productDao.findAll()) {
            model.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getSoLuong(),
                sp.getHanSuDung(),
                sp.getTrangThai()
            });
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
