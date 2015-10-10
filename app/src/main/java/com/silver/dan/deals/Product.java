package com.silver.dan.deals;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;

/**
 * Created by dan on 9/19/15.
 */
public class Product implements Serializable {
    public static String PRODUCT_SERIALIZED = "PRODUCT_SERIALIZED";

    public String title;
    public String image;
    public String thumbnail;
    public int id;
    public transient ArrayList<Listing> listings = new ArrayList<>();

    Product (String title, String image, String thumbnail, int id) {
        this.title = title;
        this.image = image;
        this.thumbnail = thumbnail;
        this.id = id;
    }

    public String getPriceString() {
        return "$" + getMinPrice();
    }

    //define callback interface
    interface Callback {
        void onDetailsLoaded();

        void onDetailsError();
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
        if (listings == null)
            listings = new ArrayList<>();
        listings.add(l);
    }

    public String getRating() {
        int maxReviews = Integer.MIN_VALUE;
        double rating = 0;
        for (Listing l : listings) {
            if (l.number_of_reviews > maxReviews) {
                maxReviews = l.number_of_reviews;
                rating = l.average_review;
            }
        }

        return String.format("%.2f", rating );
    }

    public boolean hasReviewData() {
        for (Listing l : listings) {
            if (l.hasReviewData)
                return true;
        }
        return false;
    }

    protected void fetchDetailData(Context context, final Callback callback) {
        Fuel.get(context.getResources().getString(R.string.APP_URL) + "/products/" + id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject productJSON) {
                try {
                    JSONArray listings = productJSON.getJSONArray("listings");
                    for (int j=0; j<listings.length(); j++) {
                        JSONObject l = (JSONObject) listings.get(j);
                        Listing listing = new Listing(l.getDouble("price"), l.getString("url"), l.getString("store"), false);
                        if (!l.isNull("number_of_reviews")) {
                            listing.hasReviewData = true;
                            listing.number_of_reviews = l.getInt("number_of_reviews");
                            listing.average_review = l.getDouble("average_review");
                        }
                        addListing(listing);
                        callback.onDetailsLoaded();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    callback.onDetailsError();
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());
                callback.onDetailsError();
            }
        });

    }
}
