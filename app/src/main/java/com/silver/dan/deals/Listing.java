package com.silver.dan.deals;

public class Listing {
    public double price;
    public String url;
    public String store;
    public boolean freeShipping;
    public double average_review;
    public int number_of_reviews;
    public boolean hasReviewData;
    public double shippingCost;
    private int id;

    Listing(int id, double price, String url, String store, boolean freeShipping) {
        hasReviewData = false; //default to no review data
        this.price = price;
        this.url = url;
        this.store = store;
        this.freeShipping = freeShipping;
        this.id = id;
    }

    public String getPriceString() {
        return "$" + String.format( "%.2f", price);
    }

    public String getStoreCapitalized() {
        return store.substring(0, 1).toUpperCase() + store.substring(1); //uppercase the first char
    }

    public String getRatingString() {
        return String.format("%.2f/5", average_review);
    }

    public String getShippingCostStr() {
        return String.format("$%.2f shipping", shippingCost);
    }
}
