package Qly.model;

// DTO mô tả từng sản phẩm trong bảng chi tiết nhập kho.
public class NhapHangDetail {
    private final String maSP;
    private final String tenSP;
    private final int soLuong;
    private final double donGiaNhap;

    // Nhận dữ liệu bất biến để tránh bị chỉnh sửa ngoài ý muốn khi render UI.
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

    // Thành tiền nhập = số lượng * đơn giá nhập.
    public double getThanhTien() {
        return donGiaNhap * soLuong;
    }
}
