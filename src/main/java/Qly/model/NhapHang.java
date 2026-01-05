package Qly.model;

import java.sql.Date;

public class NhapHang {
    private String maNhap;
    private String supplierID;
    private String userID;
    private Date ngayNhap;

    public NhapHang() {}

    public NhapHang(String maNhap, String supplierID, String userID, Date ngayNhap) {
        this.maNhap = maNhap;
        this.supplierID = supplierID;
        this.userID = userID;
        this.ngayNhap = ngayNhap;
    }

    public String getMaNhap() {
        return maNhap;
    }

    public void setMaNhap(String maNhap) {
        this.maNhap = maNhap;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }
}
