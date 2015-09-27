package com.silver.dan.deals;

import java.util.ArrayList;

/**
 * Created by dan on 9/19/15.
 */
public class Product {
    public String name;
    public String image;
    public String thumbnail;
    public ArrayList<Listing> listings = new ArrayList<>();

    Product (String title, String image, String thumbnail) {
        this.name = title;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public double getMinPrice() {
        double minPrice = Double.MAX_VALUE;
        for (Listing l : listings) {
            if (l.price < minPrice)
                minPrice = l.price;
        }
        return minPrice;
    }

    public void addListing(Listing l) {
        this.listings.add(l);
    }
}
