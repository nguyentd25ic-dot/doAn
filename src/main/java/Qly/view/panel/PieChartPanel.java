package Qly.view.panel;

import Qly.model.ChartSlice;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.List;

// Thành phần tuỳ chỉnh để vẽ biểu đồ tròn dạng donut nhẹ nhàng.
public class PieChartPanel extends JPanel {
    private List<ChartSlice> slices;
    private final Color[] palette;

    // Nhận dữ liệu ban đầu và bảng màu để tái sử dụng việc vẽ biểu đồ.
    public PieChartPanel(List<ChartSlice> slices, Color[] palette) {
        this.slices = slices == null ? Collections.emptyList() : slices;
        this.palette = palette;
        setOpaque(false);
    }

    // Cho phép cập nhật biểu đồ mỗi khi dữ liệu thay đổi.
    public void setSlices(List<ChartSlice> slices) {
        this.slices = slices == null ? Collections.emptyList() : slices;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = Math.min(getWidth(), getHeight()) - 20;
        diameter = Math.max(diameter, 0);
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        double total = slices.stream().mapToDouble(ChartSlice::getValue).sum();
        if (total <= 0 || diameter == 0) {
            drawEmptyState(g2);
            g2.dispose();
            return;
        }

        // Vẽ từng lát theo tỷ lệ so với tổng giá trị.
        double startAngle = 90;
        for (int i = 0; i < slices.size(); i++) {
            ChartSlice slice = slices.get(i);
            double sweep = (slice.getValue() / total) * 360d;
            g2.setColor(palette[i % palette.length]);
            g2.fillArc(x, y, diameter, diameter, (int) Math.round(startAngle), (int) Math.round(sweep));
            startAngle += sweep;
        }

        // Khoét tâm để tạo hiệu ứng donut và viền tinh tế.
        int inner = (int) (diameter * 0.52);
        g2.setColor(getParent() != null ? getParent().getBackground() : getBackground());
        g2.fillOval(x + (diameter - inner) / 2, y + (diameter - inner) / 2, inner, inner);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x, y, diameter, diameter);

        g2.dispose();
    }

    // Hiển thị thông báo khi không có dữ liệu thống kê.
    private void drawEmptyState(Graphics2D g2) {
        g2.setColor(new Color(150, 170, 180));
        g2.setFont(getFont().deriveFont(Font.ITALIC, 13f));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Không có dữ liệu";
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (getWidth() - textWidth) / 2f, getHeight() / 2f);
    }
}
