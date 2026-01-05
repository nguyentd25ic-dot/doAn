package Qly.view.panel;

import Qly.dao.StatsDao;
import Qly.model.ProductStat;
import Qly.model.YearlyStatSummary;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatsPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final StatsDao statsDao;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StatsPanelBuilder(Color cardBackground, Color headingColor, StatsDao statsDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.statsDao = statsDao;
    }

    public JPanel build() {
        YearlyStatSummary summary = statsDao.getYearlySummary();
        List<ProductStat> topProducts = statsDao.getTopProducts(5);
        LocalDate today = LocalDate.now();
        LocalDate rangeStart = today.minusYears(1);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("THỐNG KÊ 12 THÁNG GẦN NHẤT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(headingColor);

        JLabel subtitle = new JLabel("Từ " + formatDate(rangeStart) + " đến " + formatDate(today));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(91, 112, 131));

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        panel.add(header, BorderLayout.NORTH);

        JPanel cardRow = new JPanel(new GridLayout(1, 3, 16, 16));
        cardRow.setOpaque(false);
        cardRow.add(statCard(
            "Nhập hàng (12 tháng)",
            formatQuantity(summary.getPurchasedQuantity()) + " sản phẩm",
            "Giá trị: " + formatCurrency(summary.getPurchasedValue())
        ));
        cardRow.add(statCard(
            "Bán ra (12 tháng)",
            formatQuantity(summary.getSoldQuantity()) + " sản phẩm",
            "Doanh thu: " + formatCurrency(summary.getSoldValue())
        ));
        cardRow.add(statCard(
            "Lợi nhuận ước tính",
            formatCurrency(summary.getProfit()),
            "Doanh thu - nhập hàng (12 tháng)"
        ));
        panel.add(cardRow, BorderLayout.CENTER);

        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Mã SP", "Tên sản phẩm", "Số lượng đã bán", "Doanh thu"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (ProductStat stat : topProducts) {
            model.addRow(new Object[]{
                stat.getMaSP(),
                stat.getTenSP(),
                stat.getTotalQuantity(),
                formatCurrency(stat.getTotalRevenue())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel tableTitle = new JLabel("Top 5 sản phẩm bán chạy (12 tháng)");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(headingColor);
        bottom.add(tableTitle, BorderLayout.NORTH);
        bottom.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel statCard(String label, String value, String detail) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(headingColor);
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelLabel.setForeground(new Color(91, 112, 131));
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLabel.setForeground(new Color(120, 138, 150));
        card.add(labelLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(detailLabel, BorderLayout.SOUTH);
        return card;
    }

    private String formatCurrency(double value) {
        return String.format("%,.0f", value);
    }

    private String formatQuantity(int value) {
        return String.format("%,d", value);
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
