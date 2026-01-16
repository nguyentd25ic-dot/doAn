package Qly.model;

import java.sql.Timestamp;

// Lưu thông tin tổng quan của hóa đơn bán hàng.
public class HoaDon {
    private String maHD;
    private String maKH;
    private String userID;
    private Timestamp ngayLap;
    private double tongTien;

    // Constructor rỗng hỗ trợ ORM hoặc quá trình binding dữ liệu.
    public HoaDon() {}

    // Tạo mới một hóa đơn với khách hàng, người lập, thời gian và tổng tiền.
    public HoaDon(String maHD, String maKH, String userID, Timestamp ngayLap, double tongTien) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.userID = userID;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Timestamp getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Timestamp ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
}
