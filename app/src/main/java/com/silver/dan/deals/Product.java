package com.silver.dan.deals;

/**
 * Created by dan on 9/19/15.
 */
public class Product {
    public String title;
    public String link;
    public String image;
    public Double price;

    Product( String title, String link, String image, Double price) {
        this.title = title;
        this.link = link;
        this.price = price;
        this.image = image;
    }
}
