package Qly.model;

// Thông tin tài khoản đăng nhập cùng vai trò trong hệ thống.
public class User {
    private final String userId;
    private final String username;
    private final String password;
    private final String role;

    // Người dùng được khởi tạo bất biến để đảm bảo an toàn dữ liệu đăng nhập.
    public User(String userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}

