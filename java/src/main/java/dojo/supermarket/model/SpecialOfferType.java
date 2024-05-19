package dojo.supermarket.model;

public enum SpecialOfferType {
    THREE_FOR_TWO(3),
    TEN_PERCENT_DISCOUNT(1),
    TWO_FOR_AMOUNT(2),
    FIVE_FOR_AMOUNT(5),
    ;

    public final int amount;

    SpecialOfferType(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public double getDiscountAmount(double unitPrice, double quantity, double argument) {
        int quantityAsInt = (int) quantity;
        double discountTotal = switch (this) {
            case THREE_FOR_TWO ->
                    unitPrice * quantity - (((quantityAsInt / amount) * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);

            case TWO_FOR_AMOUNT ->
                    unitPrice * quantity - (argument * (quantityAsInt / amount) + quantityAsInt % 2 * unitPrice);

            case FIVE_FOR_AMOUNT ->
                    unitPrice * quantity - (argument * (quantityAsInt / amount) + quantityAsInt % 5 * unitPrice);

            case TEN_PERCENT_DISCOUNT ->
                    unitPrice * quantity * argument / 100.0;
        };
        return -discountTotal;
    }

    public String getDescription(double offerArgument){
        return switch (this) {
            case THREE_FOR_TWO -> "3 for 2";
            case TWO_FOR_AMOUNT -> "2 for " + offerArgument;
            case FIVE_FOR_AMOUNT -> amount + " for " + offerArgument;
            case TEN_PERCENT_DISCOUNT -> offerArgument + "% off";
        };
    }
}
