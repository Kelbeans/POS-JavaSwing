package common.Dto;

public class ApiResponse {
    private String transactionId;
    private String discount;
    private String discountType;
    private double amountDiscounted;
    private double totalAmountBeforeTax;

    // Default constructor (required for Jackson)
    public ApiResponse() {}

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getDiscountType() {
        return discountType;
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

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
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
        return "ApiResponse{" + '\n' +
                "transactionId='" + transactionId + '\n' +
                ", discountType='" + discountType + '\n' +
                ", discount='" + discount + '\n' +
                ", amountDiscounted=" + amountDiscounted + '\n'+
                ", totalAmountBeforeTax=" + totalAmountBeforeTax + '\n'+
                '}';
    }
}