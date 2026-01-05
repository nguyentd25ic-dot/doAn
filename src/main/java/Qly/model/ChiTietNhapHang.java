package Qly.model;

public class ChiTietNhapHang {
    private String maNhap;
    private String maSP;
    private int soLuong;
    private double donGiaNhap;

    public ChiTietNhapHang() {}

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
