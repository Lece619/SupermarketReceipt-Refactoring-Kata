package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, 1.0);
    }

    List<Product> getProducts() {
        return items.stream().map(ProductQuantity::getProduct).collect(Collectors.toList());
    }

    boolean hasProduct(Product product) {
        return items.stream().anyMatch(i -> i.getProduct().equals(product));
    }

    ProductQuantity getProductQuantity(Product product) {
        return items.stream().filter(i -> i.getProduct().equals(product)).findFirst().orElse(null);
    }

    public void addItemQuantity(Product product, double quantity) {

        double newQuantity = quantity;
        if(hasProduct(product)) {
            ProductQuantity productQuantity = getProductQuantity(product);
            newQuantity += productQuantity.getQuantity();
            items.remove(productQuantity);
        }

        items.add(new ProductQuantity(product, newQuantity));
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog) {
        for (ProductQuantity productQuantity : getItems()) {

            Product product = productQuantity.getProduct();
            double quantity = productQuantity.getQuantity();

            if (!offers.containsKey(product)) {
                continue;
            }

            Offer offer = offers.get(product);
            double unitPrice = catalog.getUnitPrice(product);
            int quantityAsInt = (int) quantity;
            Discount discount = null;

            int x = offer.offerType.getAmount();
            if (offer.offerType == SpecialOfferType.THREE_FOR_TWO && isCanDiscount(quantityAsInt, x)) {
                    double discountAmount = quantity * unitPrice - (((quantityAsInt / x) * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                    discount = new Discount(product, "3 for 2", -discountAmount);
            }

            if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT && isCanDiscount(quantityAsInt, x)){
//                x = 2;
                    double total = offer.argument * (quantityAsInt / x) + quantityAsInt % 2 * unitPrice;
                    double discountN = unitPrice * quantity - total;
                    discount = new Discount(product, "2 for " + offer.argument, -discountN);
            }

            if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT && isCanDiscount(quantityAsInt, x)) {
//                x = 5;
                    double discountTotal = unitPrice * quantity - (offer.argument * (quantityAsInt / x) + quantityAsInt % 5 * unitPrice);
                    discount = new Discount(product, x + " for " + offer.argument, -discountTotal);
            }

            if (offer.offerType == SpecialOfferType.TEN_PERCENT_DISCOUNT && isCanDiscount(quantityAsInt, x)) {
                discount = new Discount(product, offer.argument + "% off", -quantity * unitPrice * offer.argument / 100.0);
            }

            if (discount != null)
                receipt.addDiscount(discount);

        }
    }

    private boolean isCanDiscount(int quantityAsInt, int amount) {
        return quantityAsInt >= amount;
    }
}
