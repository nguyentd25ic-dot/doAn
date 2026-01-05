package Qly.model;

public class YearlyStatSummary {
    private final int purchasedQuantity;
    private final double purchasedValue;
    private final int soldQuantity;
    private final double soldValue;
    private final double profit;

    public YearlyStatSummary(int purchasedQuantity, double purchasedValue,
                             int soldQuantity, double soldValue, double profit) {
        this.purchasedQuantity = purchasedQuantity;
        this.purchasedValue = purchasedValue;
        this.soldQuantity = soldQuantity;
        this.soldValue = soldValue;
        this.profit = profit;
    }

    public int getPurchasedQuantity() {
        return purchasedQuantity;
    }

    public double getPurchasedValue() {
        return purchasedValue;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public double getSoldValue() {
        return soldValue;
    }

    public double getProfit() {
        return profit;
    }
}
