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
            if (!offers.containsKey(product)) {
                continue;
            }


            Offer offer = offers.get(product);
            double quantity = productQuantity.getQuantity();
            SpecialOfferType offerType = offer.offerType;

            if (isCanDiscount(quantity, offerType)) {

                double unitPrice = catalog.getUnitPrice(product);
                double discountAmount = offerType.getDiscountAmount(unitPrice, quantity, offer.argument);

                Discount discount = new Discount(product, offerType.getDescription(offer.argument), discountAmount);
                receipt.addDiscount(discount);
            }

        }
    }

    private boolean isCanDiscount(double quantity, SpecialOfferType productOfferType) {
        int quantityAsInt = (int) quantity;
        return quantityAsInt >= productOfferType.getAmount();
    }
}
