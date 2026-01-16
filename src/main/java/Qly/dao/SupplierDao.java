package Qly.dao;
import Qly.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

// Cung cấp CRUD cho bảng nhà cung cấp.
public class SupplierDao {
    private static final String SELECT_ALL = "SELECT SupplierID, SupplierName, Phone, Email, Address FROM Supplier";
    private static final String INSERT_SQL =
        "INSERT INTO Supplier (SupplierID, SupplierName, Phone, Email, Address) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
        "UPDATE Supplier SET SupplierName = ?, Phone = ?, Email = ?, Address = ? WHERE SupplierID = ?";
    private static final String DELETE_SQL = "DELETE FROM Supplier WHERE SupplierID = ?";

    private static final String TOTAL_SQL = "SELECT COUNT(*) FROM Supplier";
    // Lấy danh sách nhà cung cấp để hiển thị/đổ vào combobox.
    public List<Supplier> getSuppliers() {
        List<Supplier> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier sup = new Supplier();
                sup.setSupplierID(rs.getString("SupplierID"));
                sup.setSupplierName(rs.getString("SupplierName"));
                sup.setPhone(rs.getString("Phone"));
                sup.setEmail(rs.getString("Email"));
                sup.setAddress(rs.getString("Address"));
                list.add(sup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhà cung cấp mới.
    public boolean insertSupplier(Supplier supplier) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setString(1, supplier.getSupplierID());
            ps.setString(2, supplier.getSupplierName());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin nhà cung cấp.
    public boolean updateSupplier(Supplier supplier) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());
            ps.setString(5, supplier.getSupplierID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhà cung cấp theo mã.
    public boolean deleteSupplier(String supplierId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setString(1, supplierId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getTotalSup() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(TOTAL_SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}


