package Qly.dao;

import Qly.model.User;
import Qly.dao.DBConnection;
import java.sql.*;

public class UserDao {
    public User login(String user, String pass) {
        User u = null;
        String sql = "SELECT * FROM Users WHERE Username=? AND Password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new User(
                    rs.getString("UserID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("Role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }
}

