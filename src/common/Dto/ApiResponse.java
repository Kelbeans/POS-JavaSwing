package common.Dto;

public class ApiResponse {
    private String transactionId;
    private String discount;
    private Double amountDiscounted;
    private Double totalAmountBeforeTax;

    // Default constructor (required for Jackson)
    public ApiResponse() {}

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getDiscount() {
        return discount;
    }

    public Double getAmountDiscounted() {
        return amountDiscounted;
    }

    public Double getTotalAmountBeforeTax() {
        return totalAmountBeforeTax;
    }

    // Setters
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setAmountDiscounted(Double amountDiscounted) {
        this.amountDiscounted = amountDiscounted;
    }

    public void setTotalAmountBeforeTax(Double totalAmountBeforeTax) {
        this.totalAmountBeforeTax = totalAmountBeforeTax;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", discount='" + discount + '\'' +
                ", amountDiscounted=" + amountDiscounted +
                ", totalAmountBeforeTax=" + totalAmountBeforeTax +
                '}';
    }
}