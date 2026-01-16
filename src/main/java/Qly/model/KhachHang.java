package Qly.model;

// Lưu thông tin liên hệ của khách hàng mua dụng cụ.
public class KhachHang {
    private String maKH;
    private String tenKH;
    private String sdt;
    private String diaChi;

    // Constructor mặc định cho mục đích binding.
    public KhachHang() {}

    // Khởi tạo khách hàng với mã, tên, số điện thoại và địa chỉ.
    public KhachHang(String maKH, String tenKH, String sdt, String diaChi) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
