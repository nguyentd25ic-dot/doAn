package Qly.dao;

import Qly.model.ChiTietNhapHang;
import Qly.model.NhapHang;
import Qly.model.NhapHangDetail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Truy cập dữ liệu cho phiếu nhập kho và chi tiết kèm theo.
public class NhapHangDao {
    private static final String SELECT_ALL_SQL =
        "SELECT MaNhap, SupplierID, UserID, NgayNhap FROM NhapHang ORDER BY NgayNhap DESC";
    private static final String INSERT_RECEIPT_SQL =
        "INSERT INTO NhapHang (MaNhap, SupplierID, UserID, NgayNhap) VALUES (?, ?, ?, ?)";
    private static final String INSERT_DETAIL_SQL =
        "INSERT INTO ChiTietNhapHang (MaNhap, MaSP, SoLuong, DonGiaNhap) VALUES (?, ?, ?, ?)";
    private static final String INCREASE_STOCK_SQL =
        "UPDATE SanPham SET SoLuong = SoLuong + ? WHERE MaSP = ?";
    private static final String SELECT_DETAILS_SQL =
        "SELECT c.MaSP, sp.TenSP, c.SoLuong, c.DonGiaNhap " +
        "FROM ChiTietNhapHang c JOIN SanPham sp ON c.MaSP = sp.MaSP WHERE c.MaNhap = ?";

    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final int DEFAULT_ID_LENGTH = 10;
    private static volatile Integer maNhapLength;

    // Lấy toàn bộ phiếu nhập (mới nhất lên trước).
    public List<NhapHang> findAll() {
        List<NhapHang> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapNhapHang(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết sản phẩm của một phiếu nhập.
    public List<NhapHangDetail> findDetails(String maNhap) {
        List<NhapHangDetail> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_DETAILS_SQL)) {
            ps.setString(1, maNhap);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new NhapHangDetail(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGiaNhap")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Sinh mã Nhập hàng dựa trên ngày + số ngẫu nhiên, cắt theo độ dài cột.
    public String generateNhapHangId() {
        String candidate = "NH" + LocalDateTime.now().format(ID_FORMATTER)
            + String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        int maxLength = getMaNhapLength();
        if (candidate.length() > maxLength) {
            candidate = candidate.substring(0, maxLength);
        }
        return candidate;
    }

    // Tạo phiếu nhập và cập nhật tồn kho trong một transaction.
    public boolean createNhapHang(NhapHang nhapHang, List<ChiTietNhapHang> details) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất 1 sản phẩm");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            try (PreparedStatement receiptStmt = conn.prepareStatement(INSERT_RECEIPT_SQL);
                 PreparedStatement detailStmt = conn.prepareStatement(INSERT_DETAIL_SQL);
                 PreparedStatement stockStmt = conn.prepareStatement(INCREASE_STOCK_SQL)) {

                receiptStmt.setString(1, nhapHang.getMaNhap());
                receiptStmt.setString(2, nhapHang.getSupplierID());
                receiptStmt.setString(3, nhapHang.getUserID());
                receiptStmt.setDate(4, new java.sql.Date(nhapHang.getNgayNhap().getTime()));
                receiptStmt.executeUpdate();

                for (ChiTietNhapHang item : details) {
                    detailStmt.setString(1, item.getMaNhap());
                    detailStmt.setString(2, item.getMaSP());
                    detailStmt.setInt(3, item.getSoLuong());
                    detailStmt.setDouble(4, item.getDonGiaNhap());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();

                for (ChiTietNhapHang item : details) {
                    stockStmt.setInt(1, item.getSoLuong());
                    stockStmt.setString(2, item.getMaSP());
                    stockStmt.executeUpdate();
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
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Chuyển ResultSet thành đối tượng NhapHang.
    private NhapHang mapNhapHang(ResultSet rs) throws SQLException {
        NhapHang nh = new NhapHang();
        nh.setMaNhap(rs.getString("MaNhap"));
        nh.setSupplierID(rs.getString("SupplierID"));
        nh.setUserID(rs.getString("UserID"));
        nh.setNgayNhap(rs.getDate("NgayNhap"));
        return nh;
    }

    // Đọc và cache độ dài cột MaNhap.
    private int getMaNhapLength() {
        Integer cached = maNhapLength;
        if (cached != null) {
            return cached;
        }
        synchronized (NhapHangDao.class) {
            if (maNhapLength == null) {
                maNhapLength = fetchColumnLength();
            }
            return maNhapLength;
        }
    }

    // Hỏi metadata để biết chính xác kích thước cột mã nhập.
    private int fetchColumnLength() {
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
            }
            Integer length = queryColumnSize(meta, catalog, schema, "NhapHang", "MaNhap");
            if (length == null) {
                length = queryColumnSize(meta, catalog, schema, "NHAPHANG", "MANHAP");
            }
            if (length != null && length > 0) {
                return length;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DEFAULT_ID_LENGTH;
    }

    // Tiện ích đọc COLUMN_SIZE từ metadata.
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
