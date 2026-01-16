package Qly.model;

import java.sql.Date;

// Thực thể sản phẩm trong kho với giá bán, số lượng và hạn sử dụng.
public class SanPham {
    private String maSP;
    private String tenSP;
    private double donGia;
    private int soLuong;
    private String donViTinh;
    private Date hanSuDung;
    private String supplierID;

    // Constructor rỗng phục vụ binding dữ liệu.
    public SanPham() {}

    // Khởi tạo sản phẩm đầy đủ thông tin tồn kho.
    public SanPham(String maSP, String tenSP, double donGia, int soLuong,
                   String donViTinh, Date hanSuDung, String supplierID) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.donViTinh = donViTinh;
        this.hanSuDung = hanSuDung;
        this.supplierID = supplierID;
    }



    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public Date getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(Date hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
    // Tự suy luận trạng thái hàng dựa trên hạn sử dụng và số lượng.
    public String getTrangThai() {
        Date today = new Date(System.currentTimeMillis());
        if (hanSuDung.before(today)) {
            return "Hết hạn";
        }
        if (soLuong == 0) {
            return "Hết hàng";
        }
        return "Còn hàng";
    }

}
