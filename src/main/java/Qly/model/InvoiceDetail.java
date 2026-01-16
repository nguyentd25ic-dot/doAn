package Qly.model;

// DTO dùng để hiển thị chi tiết hóa đơn với tên sản phẩm và thành tiền.
public class InvoiceDetail {
    private final String maSP;
    private final String tenSP;
    private final int soLuong;
    private final double donGia;

    // Dữ liệu bất biến được truyền tới bảng/biểu mẫu UI.
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

    // Thành tiền = đơn giá * số lượng, phục vụ thống kê và hiển thị.
    public double getThanhTien() {
        return donGia * soLuong;
    }
}
