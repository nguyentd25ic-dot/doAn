package Qly.model;

// Mô tả từng dòng sản phẩm xuất hiện trên một hóa đơn bán hàng.
public class ChiTietHoaDon {
    private String maHD;
    private String maSP;
    private int soLuong;
    private double donGia;

    // Dùng cho các framework/ORM cần khởi tạo rỗng.
    public ChiTietHoaDon() {}

    // Tạo mới một chi tiết hóa đơn với mã hóa đơn, sản phẩm, số lượng và giá bán.
    public ChiTietHoaDon(String maHD, String maSP, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }
}
