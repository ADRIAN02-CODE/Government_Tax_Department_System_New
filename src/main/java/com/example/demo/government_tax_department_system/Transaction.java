package com.example.demo.government_tax_department_system;

public class Transaction {
    private String itemCode;
    private double internalPrice;
    private double discount;
    private double salePrice;
    private int quantity;
    private String checksum;
    private boolean isValid;
    private double profit;

    public Transaction(String itemCode, double internalPrice, double discount, double salePrice, int quantity, String checksum) {
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.discount = discount;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.checksum = checksum;
        this.isValid = validateChecksum();
    }

    // Add valid field constructor
    public Transaction(String itemCode, double internalPrice, double discount, double salePrice, int quantity, String checksum, boolean valid) {
        this(itemCode, internalPrice, discount, salePrice, quantity, checksum);
        this.isValid = valid && validateChecksum(); // Use CSV valid field and revalidate
    }

    private boolean validateChecksum() {
        String record = String.format("%s,%.2f,%.2f,%.2f,%d", itemCode, internalPrice, discount, salePrice, quantity);
        String calculatedChecksum = calculateMD5(record);
        return checksum.equals(calculatedChecksum);
    }

    private String calculateMD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return "";
        }
    }

    public void calculateProfit() {
        this.profit = (salePrice - internalPrice) * quantity;
    }

    // Getters
    public String getItemCode() { return itemCode; }
    public double getInternalPrice() { return internalPrice; }
    public double getDiscount() { return discount; }
    public double getSalePrice() { return salePrice; }
    public int getQuantity() { return quantity; }
    public String getChecksum() { return checksum; }
    public boolean getIsValid() { return isValid; }
    public double getProfit() { return profit; }
}