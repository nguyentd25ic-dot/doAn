package Qly.view.panel;

import Qly.dao.StatsDao;
import Qly.model.ChartSlice;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Panel thống kê doanh thu/lợi nhuận và top sản phẩm.
public class StatsPanelBuilder {
    private final Color cardBackground;
    private final Color headingColor;
    private final StatsDao statsDao;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Bảng màu cố định giúp các biểu đồ tròn đồng nhất giữa các lần render.
    private static final Color[] PIE_COLORS = new Color[]{
        new Color(44, 182, 125),
        new Color(15, 109, 145),
        new Color(255, 159, 67),
        new Color(75, 123, 236),
        new Color(255, 99, 132),
        new Color(155, 89, 182),
        new Color(52, 152, 219),
        new Color(241, 196, 15)
    };
    private static final Dimension PIE_CHART_SIZE = new Dimension(260, 260);

    public StatsPanelBuilder(Color cardBackground, Color headingColor, StatsDao statsDao) {
        this.cardBackground = cardBackground;
        this.headingColor = headingColor;
        this.statsDao = statsDao;
    }

    public JPanel build() {
        // Lấy toàn bộ số liệu cần thiết cho thẻ tổng quan, bảng và biểu đồ.
        YearlyStatSummary summary = statsDao.getYearlySummary();
        List<ProductStat> topProducts = statsDao.getTopProducts(5);
        List<ChartSlice> yearlyRevenueSlices = statsDao.getYearlyRevenueSlices();
        List<ChartSlice> currentMonthRevenueSlices = statsDao.getCurrentMonthRevenueSlices();
        List<ChartSlice> productSlices = toProductSlices(topProducts);
        LocalDate today = LocalDate.now();
        LocalDate rangeStart = today.minusYears(1);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Phần đầu hiển thị tiêu đề + khoảng thời gian của báo cáo.
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

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // Ba thẻ số liệu chính: nhập hàng, bán ra, và lợi nhuận.
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
        body.add(cardRow);
        body.add(Box.createVerticalStrut(24));

        // Hàng biểu đồ tròn cho doanh thu cả năm, tháng hiện tại và tỉ trọng sản phẩm.
        JPanel chartRow = buildChartsRow(yearlyRevenueSlices, currentMonthRevenueSlices, productSlices, today);
        body.add(chartRow);
        body.add(Box.createVerticalStrut(24));

        // Bảng top sản phẩm bán chạy vẫn giữ nguyên dưới cùng.
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

        body.add(bottom);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Tạo hàng chứa ba thẻ biểu đồ tròn.
    private JPanel buildChartsRow(List<ChartSlice> yearly, List<ChartSlice> monthly,
                                  List<ChartSlice> productSlices, LocalDate today) {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 16));
        row.setOpaque(false);
        row.add(pieChartCard("Doanh thu 12 tháng", yearly));
        row.add(pieChartCard("Doanh thu tháng " + formatMonthYear(today), monthly));
        row.add(pieChartCard("Tỉ trọng sản phẩm bán ra", productSlices));
        return row;
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

    // Thẻ biểu đồ tròn gồm tiêu đề, biểu đồ và chú thích màu.
    private JPanel pieChartCard(String title, List<ChartSlice> slices) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(headingColor);

        PieChartPanel pieChartPanel = new PieChartPanel(slices, PIE_COLORS);
        pieChartPanel.setPreferredSize(PIE_CHART_SIZE);
        pieChartPanel.setMinimumSize(PIE_CHART_SIZE);

        JPanel legend = buildLegend(slices);

        card.add(label, BorderLayout.NORTH);
        card.add(pieChartPanel, BorderLayout.CENTER);
        card.add(legend, BorderLayout.SOUTH);
        return card;
    }

    // Dựng legend hiển thị màu cùng phần trăm tương ứng của từng lát.
    private JPanel buildLegend(List<ChartSlice> slices) {
        JPanel legend = new JPanel();
        legend.setOpaque(false);
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));

        double total = slices.stream().mapToDouble(ChartSlice::getValue).sum();
        if (total <= 0) {
            JLabel empty = new JLabel("Không có dữ liệu");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            empty.setForeground(new Color(120, 138, 150));
            legend.add(empty);
            return legend;
        }

        for (int i = 0; i < slices.size(); i++) {
            ChartSlice slice = slices.get(i);
            double percent = (slice.getValue() / total) * 100d;
            legend.add(createLegendRow(PIE_COLORS[i % PIE_COLORS.length],
                String.format("%s (%.1f%%)", slice.getLabel(), percent)));
        }
        return legend;
    }

    private JPanel createLegendRow(Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        JPanel colorDot = new JPanel();
        colorDot.setBackground(color);
        colorDot.setPreferredSize(new Dimension(12, 12));
        colorDot.setMaximumSize(new Dimension(12, 12));
        colorDot.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 150)));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(91, 112, 131));

        row.add(colorDot);
        row.add(label);
        return row;
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

    private String formatMonthYear(LocalDate date) {
        return String.format("%02d/%d", date.getMonthValue(), date.getYear());
    }

    // Quy đổi dữ liệu sản phẩm sang dạng lát biểu đồ theo số lượng bán ra.
    private List<ChartSlice> toProductSlices(List<ProductStat> stats) {
        List<ChartSlice> slices = new ArrayList<>();
        for (ProductStat stat : stats) {
            slices.add(new ChartSlice(stat.getTenSP(), stat.getTotalQuantity()));
        }
        return slices;
    }
}
