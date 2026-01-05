package Qly.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1434;databaseName=MedicalStore;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "YourStrong!Passw0rd";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException ex) {
            System.out.println("Kết nối thất bại!");
            ex.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {


    }
}