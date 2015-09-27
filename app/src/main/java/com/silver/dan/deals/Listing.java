package com.silver.dan.deals;

/**
 * Created by dan on 9/26/15.
 */
public class Listing {
    public double price;
    public String url;
    public String store;
    public boolean freeShipping;

    Listing(double price, String url, String store, boolean freeShipping) {
        this.price = price;
        this.url = url;
        this.store = store;
        this.freeShipping = freeShipping;
    }
}
