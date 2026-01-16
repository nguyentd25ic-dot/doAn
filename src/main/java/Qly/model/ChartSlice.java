package Qly.model;

// Đại diện cho một phần dữ liệu được vẽ trên biểu đồ tròn.
public class ChartSlice {
    private final String label;
    private final double value;

    // Lưu trữ cặp nhãn - giá trị phục vụ cho biểu đồ và chú thích.
    public ChartSlice(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }
}
