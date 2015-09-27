package com.silver.dan.deals;

/**
 * Created by dan on 9/26/15.
 */
public class Listing {
    public double price;
    public String url;
    public String store;
    public boolean freeShipping;
    public double average_review;
    public int number_of_reviews;

    Listing(double price, String url, String store, boolean freeShipping, int number_of_reviews, double average_review) {
        this.price = price;
        this.url = url;
        this.store = store;
        this.freeShipping = freeShipping;
        this.number_of_reviews = number_of_reviews;
        this.average_review = average_review;
    }
}
