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
}
