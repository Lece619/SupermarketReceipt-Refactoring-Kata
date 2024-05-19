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
        if (hasProduct(product)) {
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

            SpecialOfferType offerType = offer.offerType;
            int x = offerType.getAmount();

            if (isCanDiscount(quantityAsInt, offerType, SpecialOfferType.THREE_FOR_TWO)) {
                discount = new Discount(product, "3 for 2", offerType.getDiscountAmount(unitPrice, quantity, 0));
            }

            if (isCanDiscount(quantityAsInt, offerType, SpecialOfferType.TWO_FOR_AMOUNT)) {
                discount = new Discount(product, "2 for " + offer.argument, offerType.getDiscountAmount(unitPrice, quantity, offer.argument));
            }

            if (isCanDiscount(quantityAsInt, offerType, SpecialOfferType.FIVE_FOR_AMOUNT)) {
                discount = new Discount(product, x + " for " + offer.argument, offerType.getDiscountAmount(unitPrice, quantity, offer.argument));
            }

            if (isCanDiscount(quantityAsInt, offerType, SpecialOfferType.TEN_PERCENT_DISCOUNT)) {
                discount = new Discount(product, offerType.getDescription(offer.argument), offerType.getDiscountAmount(unitPrice, quantity, offer.argument));
            }

            if (discount != null)
                receipt.addDiscount(discount);

        }
    }

    private boolean isCanDiscount(int quantityAsInt, SpecialOfferType productOfferType, SpecialOfferType offerType) {
        if (productOfferType == offerType)
            return quantityAsInt >= productOfferType.getAmount();

        return false;
    }
}
