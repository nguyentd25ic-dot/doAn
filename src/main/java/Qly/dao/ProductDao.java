package Qly.dao;

import Qly.model.SanPham;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private static final String BASE_SELECT =
        "SELECT sp.MaSP, sp.TenSP, sp.DonGia, sp.SoLuong, sp.DonViTinh, sp.HanSuDung, " +
        "sp.SupplierID, s.SupplierName " +
        "FROM SanPham sp LEFT JOIN Supplier s ON sp.SupplierID = s.SupplierID";

    private static final String INSERT_SQL =
        "INSERT INTO SanPham (MaSP, TenSP, DonGia, SoLuong, DonViTinh, HanSuDung, SupplierID) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_SQL = "DELETE FROM SanPham WHERE MaSP = ?";

    public List<SanPham> findAll() {
        List<SanPham> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();

        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(BASE_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapSanPham(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(SanPham sanPham)  {
        Connection con = DBConnection.getConnection();
        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {
            ps.setString(1, sanPham.getMaSP());
            ps.setString(2, sanPham.getTenSP());
            ps.setDouble(3, sanPham.getDonGia());
            ps.setInt(4, sanPham.getSoLuong());
            ps.setString(5, sanPham.getDonViTinh());
            if (sanPham.getHanSuDung() != null) {
                ps.setDate(6, sanPham.getHanSuDung());
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, sanPham.getSupplierID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(String maSP) {
        Connection con = DBConnection.getConnection();
        try (Connection c = con;
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setString(1, maSP);
            ps.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean update(SanPham sp) {
        String sql = """
        UPDATE sanpham
        SET tenSP = ?,
            donGia = ?,
            soLuong = ?,
            donViTinh = ?,
            hanSuDung = ?,
            supplierID = ?
        WHERE maSP = ?
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuong());
            ps.setString(4, sp.getDonViTinh());
            ps.setDate(5, java.sql.Date.valueOf(sp.getHanSuDung().toLocalDate()));

            ps.setString(6, sp.getSupplierID());
            ps.setString(7, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SanPham findById(String maSP) {
        String sql = BASE_SELECT + " WHERE sp.MaSP = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSanPham(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SanPham mapSanPham(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaSP(rs.getString("MaSP"));
        sp.setTenSP(rs.getString("TenSP"));
        sp.setDonGia(rs.getDouble("DonGia"));
        sp.setSoLuong(rs.getInt("SoLuong"));
        sp.setDonViTinh(rs.getString("DonViTinh"));
        sp.setHanSuDung(rs.getDate("HanSuDung"));
        sp.setSupplierID(rs.getString("SupplierID"));
        return sp;
    }

}
