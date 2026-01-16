package Qly.dao;

import Qly.model.ChartSlice;
import Qly.model.ProductStat;
import Qly.model.YearlyStatSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatsDao {
    // Tập hợp các truy vấn thống kê nhập - bán và dữ liệu cho biểu đồ.
    private static final String YEARLY_PURCHASE_SQL =
        "SELECT COALESCE(SUM(c.SoLuong), 0) AS TotalQty, " +
        "COALESCE(SUM(c.SoLuong * c.DonGiaNhap), 0) AS TotalValue " +
        "FROM ChiTietNhapHang c JOIN NhapHang n ON c.MaNhap = n.MaNhap " +
        "WHERE n.NgayNhap >= DATEADD(YEAR, -1, GETDATE())";
    private static final String YEARLY_SALES_SQL =
        "SELECT COALESCE(SUM(c.SoLuong), 0) AS TotalQty, " +
        "COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS TotalValue " +
        "FROM ChiTietHoaDon c JOIN HoaDon h ON c.MaHD = h.MaHD " +
        "WHERE h.NgayLap >= DATEADD(YEAR, -1, GETDATE())";
    private static final String TOP_PRODUCTS_SQL =
        "SELECT TOP (?) c.MaSP, sp.TenSP, SUM(c.SoLuong) AS TotalQty, " +
        "SUM(c.SoLuong * c.DonGia) AS TotalRevenue " +
        "FROM ChiTietHoaDon c " +
        "JOIN SanPham sp ON c.MaSP = sp.MaSP " +
        "JOIN HoaDon h ON c.MaHD = h.MaHD " +
        "WHERE h.NgayLap >= DATEADD(YEAR, -1, GETDATE()) " +
        "GROUP BY c.MaSP, sp.TenSP ORDER BY TotalQty DESC";
    private static final String YEARLY_REVENUE_BREAKDOWN_SQL =
        "SELECT YEAR(h.NgayLap) AS YearPart, MONTH(h.NgayLap) AS MonthPart, " +
        "SUM(c.SoLuong * c.DonGia) AS TotalRevenue " +
        "FROM ChiTietHoaDon c " +
        "JOIN HoaDon h ON c.MaHD = h.MaHD " +
        "WHERE h.NgayLap >= DATEADD(YEAR, -1, GETDATE()) " +
        "GROUP BY YEAR(h.NgayLap), MONTH(h.NgayLap) " +
        "ORDER BY YEAR(h.NgayLap), MONTH(h.NgayLap)";
    private static final String CURRENT_MONTH_REVENUE_SQL =
        "SELECT ((DATEPART(DAY, h.NgayLap) - 1) / 7) + 1 AS WeekBucket, " +
        "SUM(c.SoLuong * c.DonGia) AS TotalRevenue " +
        "FROM ChiTietHoaDon c " +
        "JOIN HoaDon h ON c.MaHD = h.MaHD " +
        "WHERE YEAR(h.NgayLap) = YEAR(GETDATE()) " +
        "AND MONTH(h.NgayLap) = MONTH(GETDATE()) " +
        "GROUP BY ((DATEPART(DAY, h.NgayLap) - 1) / 7) + 1 " +
        "ORDER BY WeekBucket";

    // Gộp số liệu nhập - bán trong 12 tháng và ước tính lợi nhuận.
    public YearlyStatSummary getYearlySummary() {
        StatAggregate purchase = queryAggregate(YEARLY_PURCHASE_SQL);
        StatAggregate sales = queryAggregate(YEARLY_SALES_SQL);
        double profit = sales.value - purchase.value;
        return new YearlyStatSummary(
            purchase.quantity,
            purchase.value,
            sales.quantity,
            sales.value,
            profit
        );
    }

    // Trả về danh sách sản phẩm bán chạy cho bảng và biểu đồ sản phẩm.
    public List<ProductStat> getTopProducts(int limit) {
        List<ProductStat> stats = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(TOP_PRODUCTS_SQL)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.add(new ProductStat(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("TotalQty"),
                        rs.getDouble("TotalRevenue")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Phân rã doanh thu theo từng tháng trong 12 tháng gần nhất.
    public List<ChartSlice> getYearlyRevenueSlices() {
        List<ChartSlice> slices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(YEARLY_REVENUE_BREAKDOWN_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int year = rs.getInt("YearPart");
                int month = rs.getInt("MonthPart");
                double revenue = rs.getDouble("TotalRevenue");
                slices.add(new ChartSlice(String.format("%02d/%d", month, year), revenue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slices;
    }

    // Gom doanh thu tháng hiện tại theo từng tuần để xem xu hướng ngắn hạn.
    public List<ChartSlice> getCurrentMonthRevenueSlices() {
        List<ChartSlice> slices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CURRENT_MONTH_REVENUE_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int week = rs.getInt("WeekBucket");
                double revenue = rs.getDouble("TotalRevenue");
                slices.add(new ChartSlice("Tuần " + week, revenue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slices;
    }

    // Hàm hỗ trợ chạy truy vấn SUM và trả về số lượng, giá trị.
    private StatAggregate queryAggregate(String sql) {
        StatAggregate aggregate = new StatAggregate();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                aggregate.quantity = rs.getInt("TotalQty");
                aggregate.value = rs.getDouble("TotalValue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aggregate;
    }

    // Lớp phụ trợ lưu kết quả tổng hợp tạm thời.
    private static class StatAggregate {
        private int quantity;
        private double value;
    }
}
