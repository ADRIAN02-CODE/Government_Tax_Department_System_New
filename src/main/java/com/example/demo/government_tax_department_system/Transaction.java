package com.example.demo.government_tax_department_system;

import javafx.beans.property.*;

public class Transaction {
    private final StringProperty itemCode;
    private final DoubleProperty internalPrice;
    private final DoubleProperty discount;
    private final DoubleProperty salePrice;
    private final IntegerProperty quantity;
    private final StringProperty checksum;
    private final BooleanProperty isValid;
    private final DoubleProperty profit;

    public Transaction(String itemCode, double internalPrice, double discount,
                       double salePrice, int quantity, String checksum) {
        this.itemCode = new SimpleStringProperty(itemCode);
        this.internalPrice = new SimpleDoubleProperty(internalPrice);
        this.discount = new SimpleDoubleProperty(discount);
        this.salePrice = new SimpleDoubleProperty(salePrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.checksum = new SimpleStringProperty(checksum);
        this.isValid = new SimpleBooleanProperty(false);
        this.profit = new SimpleDoubleProperty(0.0);

        validate();
    }

    // Getters and setters
    public String getItemCode() { return itemCode.get(); }
    public double getInternalPrice() { return internalPrice.get(); }
    public double getDiscount() { return discount.get(); }
    public double getSalePrice() { return salePrice.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getChecksum() { return checksum.get(); }
    public boolean getIsValid() { return isValid.get(); }
    public double getProfit() { return profit.get(); }

    public void setItemCode(String value) { itemCode.set(value); validate(); }
    public void setInternalPrice(double value) { internalPrice.set(value); validate(); }
    public void setDiscount(double value) { discount.set(value); validate(); }
    public void setSalePrice(double value) { salePrice.set(value); validate(); }
    public void setQuantity(int value) { quantity.set(value); validate(); }
    public void setChecksum(String value) { checksum.set(value); validate(); }
    public void calculateProfit() {
        profit.set((salePrice.get() * quantity.get() - discount.get()) -
                (internalPrice.get() * quantity.get()));
    }

    // Property getters for JavaFX binding
    public StringProperty itemCodeProperty() { return itemCode; }
    public DoubleProperty internalPriceProperty() { return internalPrice; }
    public DoubleProperty discountProperty() { return discount; }
    public DoubleProperty salePriceProperty() { return salePrice; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty checksumProperty() { return checksum; }
    public BooleanProperty isValidProperty() { return isValid; }
    public DoubleProperty profitProperty() { return profit; }

    // Validate the transaction
    private void validate() {
        boolean valid = true;

        // Check for special characters in item code
        if (!itemCode.get().matches("[a-zA-Z0-9]+")) {
            System.out.println("Validation failed for " + itemCode.get() + ": itemCode is not alphanumeric");
            valid = false;
        }

        // Check for negative price
        if (internalPrice.get() < 0 || salePrice.get() < 0 || discount.get() < 0 || quantity.get() < 0) {
            System.out.println("Validation failed for " + itemCode.get() + ": negative values detected");
            valid = false;
        }

        // Calculate checksum and compare
        String calculatedChecksum = calculateChecksum();
        if (!calculatedChecksum.equals(checksum.get())) {
            System.out.println("Validation failed for " + itemCode.get() + ": checksum mismatch, expected " + calculatedChecksum + ", got " + checksum.get());
            valid = false;
        }

        isValid.set(valid);
    }

    // Calculate checksum for validation
    private String calculateChecksum() {
        String data = String.format("%s%.2f%.2f%.2f%d",
                itemCode.get(), internalPrice.get(), discount.get(), salePrice.get(), quantity.get());
        int hash = data.hashCode();
        return Integer.toHexString(hash);
    }
    public class ChecksumTest {
        public static void main(String[] args) {
            String itemCode = "ABC123";
            double internalPrice = 10.00;
            double discount = 2.00;
            double salePrice = 8.00;
            int quantity = 5;

            String data = String.format("%s%.2f%.2f%.2f%d", itemCode, internalPrice, discount, salePrice, quantity);
            System.out.println("Data string: " + data);
            int hash = data.hashCode();
            String checksum = Integer.toHexString(hash);
            System.out.println("Checksum: " + checksum);
        }
    }

    @Override
    public String toString() {
        return String.format("%s,%.2f,%.2f,%.2f,%d,%s",
                itemCode.get(), internalPrice.get(), discount.get(), salePrice.get(), quantity.get(), checksum.get());
    }
}