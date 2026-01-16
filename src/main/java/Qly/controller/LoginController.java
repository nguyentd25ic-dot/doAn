package Qly.controller;
import Qly.model.User;
import Qly.dao.UserDao;
import java.sql.Connection;
import Qly.dao.DBConnection;
import Qly.view.DashBoard;
import Qly.view.Login;
import javax.swing.*;

// Điều khiển màn hình đăng nhập và chuyển sang dashboard khi thành công.
public class LoginController {
    private Login view;
    private UserDao dao;

    public LoginController(Login view) {
        this.view = view;
        dao= new UserDao();
        view.getBlogin().addActionListener(e-> login());
    }

    // Lấy thông tin form, gọi DAO và phản hồi cho người dùng.
    private void login(){
        String username = view.getUsername();
        String pass = view.getPassword();
        User u = dao.login(username, pass);
        if(u!=null){
            JOptionPane.showMessageDialog(view, "Đăng nhập thành công!");
            view.dispose();
            new DashBoard(u);
        }
        else{
            JOptionPane.showMessageDialog(view, "Sai tài khoản hoặc mật khẩu");
        }
    }
}
