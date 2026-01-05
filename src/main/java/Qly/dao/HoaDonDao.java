package Qly.dao;

import Qly.model.ChiTietHoaDon;
import Qly.model.HoaDon;
import Qly.model.InvoiceDetail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDao {
    private static final String INSERT_INVOICE_SQL =
        "INSERT INTO HoaDon (MaHD, MaKH, UserID, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_DETAIL_SQL =
        "INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
    private static final String DECREASE_STOCK_SQL =
        "UPDATE SanPham SET SoLuong = SoLuong - ? WHERE MaSP = ? AND SoLuong >= ?";
    private static final String SELECT_ALL_SQL =
        "SELECT MaHD, MaKH, UserID, NgayLap, TongTien FROM HoaDon ORDER BY NgayLap DESC";
    private static final String SELECT_DETAIL_SQL =
        "SELECT c.MaSP, sp.TenSP, c.SoLuong, c.DonGia " +
        "FROM ChiTietHoaDon c JOIN SanPham sp ON c.MaSP = sp.MaSP WHERE c.MaHD = ?";

    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final int DEFAULT_ID_LENGTH = 10;
    private static volatile Integer invoiceIdColumnLength;

    public String generateInvoiceId() {
        String candidate = buildCandidate();
        int maxLength = getInvoiceIdColumnLength();
        if (candidate.length() > maxLength) {
            candidate = candidate.substring(0, maxLength);
        }
        return candidate;
    }

    private String buildCandidate() {
        String base = "HD" + LocalDateTime.now().format(ID_FORMATTER);
        String suffix = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return base + suffix;
    }

    public boolean createInvoice(HoaDon hoaDon, List<ChiTietHoaDon> details) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one product");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            try (PreparedStatement invoiceStmt = conn.prepareStatement(INSERT_INVOICE_SQL);
                 PreparedStatement detailStmt = conn.prepareStatement(INSERT_DETAIL_SQL);
                 PreparedStatement stockStmt = conn.prepareStatement(DECREASE_STOCK_SQL)) {

                invoiceStmt.setString(1, hoaDon.getMaHD());
                invoiceStmt.setString(2, hoaDon.getMaKH());
                invoiceStmt.setString(3, hoaDon.getUserID());
                invoiceStmt.setTimestamp(4, hoaDon.getNgayLap());
                invoiceStmt.setDouble(5, hoaDon.getTongTien());
                invoiceStmt.executeUpdate();

                for (ChiTietHoaDon item : details) {
                    detailStmt.setString(1, item.getMaHD());
                    detailStmt.setString(2, item.getMaSP());
                    detailStmt.setInt(3, item.getSoLuong());
                    detailStmt.setDouble(4, item.getDonGia());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();

                for (ChiTietHoaDon item : details) {
                    stockStmt.setInt(1, item.getSoLuong());
                    stockStmt.setString(2, item.getMaSP());
                    stockStmt.setInt(3, item.getSoLuong());
                    int updated = stockStmt.executeUpdate();
                    if (updated == 0) {
                        throw new IllegalStateException("Số lượng kho không đủ cho sản phẩm: " + item.getMaSP());
                    }
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public List<HoaDon> findAll() {
        List<HoaDon> invoices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapHoaDon(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public List<InvoiceDetail> findDetails(String maHD) {
        List<InvoiceDetail> details = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_DETAIL_SQL)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new InvoiceDetail(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getString("MaHD"));
        hd.setMaKH(rs.getString("MaKH"));
        hd.setUserID(rs.getString("UserID"));
        hd.setNgayLap(rs.getTimestamp("NgayLap"));
        hd.setTongTien(rs.getDouble("TongTien"));
        return hd;
    }

    private int getInvoiceIdColumnLength() {
        Integer cached = invoiceIdColumnLength;
        if (cached != null) {
            return cached;
        }
        synchronized (HoaDonDao.class) {
            if (invoiceIdColumnLength == null) {
                invoiceIdColumnLength = fetchInvoiceIdColumnLength();
            }
            return invoiceIdColumnLength;
        }
    }

    private int fetchInvoiceIdColumnLength() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return DEFAULT_ID_LENGTH;
            }
            DatabaseMetaData meta = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = null;
            try {
                schema = conn.getSchema();
            } catch (SQLException ignore) {
                // schema not supported
            }

            Integer size = queryColumnSize(meta, catalog, schema, "HoaDon", "MaHD");
            if (size == null) {
                size = queryColumnSize(meta, catalog, schema, "HOADON", "MAHD");
            }
            if (size != null && size > 0) {
                return size;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DEFAULT_ID_LENGTH;
    }

    private Integer queryColumnSize(DatabaseMetaData meta, String catalog, String schema,
                                    String table, String column) throws SQLException {
        try (ResultSet rs = meta.getColumns(catalog, schema, table, column)) {
            if (rs.next()) {
                return rs.getInt("COLUMN_SIZE");
            }
        }
        return null;
    }
}
