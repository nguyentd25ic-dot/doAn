package Qly.model;

public class InvoiceDetail {
    private final String maSP;
    private final String tenSP;
    private final int soLuong;
    private final double donGia;

    public InvoiceDetail(String maSP, String tenSP, int soLuong, double donGia) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
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

    public double getDonGia() {
        return donGia;
    }

    public double getThanhTien() {
        return donGia * soLuong;
    }
}
