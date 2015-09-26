package com.silver.dan.deals;

/**
 * Created by dan on 9/19/15.
 */
public class Product {
    public String name;
    public String link;
    public String image;
    public String thumbnail;
    public Double price;
    public String source;

    Product (String title, String link, String image, Double price, String source, String thumbnail) {
        this.name = title;
        this.link = link;
        this.price = price;
        this.image = image;
        this.source = source;
        this.thumbnail = thumbnail;
    }
}
