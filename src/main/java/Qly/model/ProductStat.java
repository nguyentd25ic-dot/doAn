package Qly.model;

public class ProductStat {
    private final String maSP;
    private final String tenSP;
    private final int totalQuantity;
    private final double totalRevenue;

    public ProductStat(String maSP, String tenSP, int totalQuantity, double totalRevenue) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
