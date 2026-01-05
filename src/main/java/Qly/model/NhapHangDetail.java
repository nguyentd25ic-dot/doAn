package Qly.model;

public class NhapHangDetail {
    private final String maSP;
    private final String tenSP;
    private final int soLuong;
    private final double donGiaNhap;

    public NhapHangDetail(String maSP, String tenSP, int soLuong, double donGiaNhap) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public double getThanhTien() {
        return donGiaNhap * soLuong;
    }
}
