package com.silver.dan.deals;

import java.util.ArrayList;

public class Listing {
    public double price;
    public String url;
    public String store;
    public boolean freeShipping;
    public double average_review;
    public int number_of_reviews;
    public boolean hasReviewData;
    public double shippingCost;
    public ArrayList<String> otherAttrs = new ArrayList<>();
    public int id;

    Listing(int id, double price, String url, String store, boolean freeShipping) {
        hasReviewData = false; //default to no review data
        this.price = price;
        this.url = url;
        this.store = store;
        this.freeShipping = freeShipping;
        this.id = id;
    }

    public Listing(int id, double price) {
        this.price = price;
        this.id = id;
    }

    public String getPriceString() {
        return "$" + String.format("%.2f", price);
    }

    public String getRatingString() {
        return String.format("%.2f/5", average_review);
    }

    public String getShippingCostStr() {
        return String.format("$%.2f shipping", shippingCost);
    }

    public String getNumberReviewsStr() {
        if (number_of_reviews > 1)
            return String.format("%d reviews", number_of_reviews);
        else if (number_of_reviews == 1) {
            return "1 review";
        } else {
            return "No reviews"; //shouldn't be used
        }
    }
}
