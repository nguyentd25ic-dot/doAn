package Qly.model;

// Đại diện cho từng mặt hàng trong phiếu nhập kho.
public class ChiTietNhapHang {
    private String maNhap;
    private String maSP;
    private int soLuong;
    private double donGiaNhap;

    // Constructor mặc định phục vụ quá trình mapping dữ liệu.
    public ChiTietNhapHang() {}

    // Khởi tạo chi tiết nhập với mã phiếu, sản phẩm, số lượng và giá nhập.
    public ChiTietNhapHang(String maNhap, String maSP, int soLuong, double donGiaNhap) {
        this.maNhap = maNhap;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public String getMaNhap() {
        return maNhap;
    }

    public void setMaNhap(String maNhap) {
        this.maNhap = maNhap;
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

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }
}
