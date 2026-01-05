package Qly.dao;

import Qly.model.ProductStat;
import Qly.model.YearlyStatSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatsDao {
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

    private static class StatAggregate {
        private int quantity;
        private double value;
    }
}
