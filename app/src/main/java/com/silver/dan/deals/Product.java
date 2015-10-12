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

public class Product implements Serializable {

    public static String PRODUCT_SERIALIZED = "PRODUCT_SERIALIZED";

    public String title;
    public String image;
    public String thumbnail;
    public int id;
    public transient ArrayList<Listing> listings = new ArrayList<>();
    public transient ArrayList<String> features = new ArrayList<>();
    public transient ArrayList<String> images = new ArrayList<>();
    public int secondaryCategoryId;
    public int primaryCategoryId;
    public boolean detailsLoaded = false;
    private ArrayList<DetailsCallback> callbacks = new ArrayList<>();

    Product (String title, String image, String thumbnail, int id, int primaryCategoryId, int secondaryCategoryId) {
        this.title = title;
        this.image = image;
        this.thumbnail = thumbnail;
        this.id = id;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
    }

    public String getPriceString() {
        return "$" + String.format( "%.2f", getMinPrice() );
    }

    public String getRatingString() {
        return String.format("%s/5", getRating());
    }

    interface DetailsCallback {
        void onLoaded();

        void onError();
    }

    public double getMinPrice() {
        double minPrice = Double.MAX_VALUE;
        for (Listing l : listings) {
            if (l.price < minPrice)
                minPrice = l.price;
        }
        return minPrice;
    }

    public void addOrUpdateListing(Listing newListing) {
        if (listings == null)
            listings = new ArrayList<>();

        //remove the old listing if one exists
        ArrayList<Listing> toRemove = new ArrayList<>();

        for (Listing oldListing : listings)
            if (oldListing.store.equals(newListing.store)) {
                toRemove.add(oldListing);
            }
        listings.removeAll(toRemove);

        //regardless, add the new data
        listings.add(newListing);
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

    public void addDetailsLoadedCallback(DetailsCallback callback) {
        //if details are already saved, execute the callback right away, otherwise save it
        if (detailsLoaded) {
            callback.onLoaded();
        } else {
            callbacks.add(callback);
        }
    }

    private void executeDetailsLoadedCallbacks() {
        for (DetailsCallback callback : callbacks)
            callback.onLoaded();
    }

    protected void fetchDetailData(Context context, final DetailsCallback callback) {
        if (detailsLoaded) {
            callback.onLoaded();
            executeDetailsLoadedCallbacks();
            return;
        }
        Fuel.get(context.getResources().getString(R.string.APP_URL) + "/products/" + id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject productJSON) {
                try {
                    JSONArray listings = productJSON.getJSONArray("listings");
                    for (int j = 0; j < listings.length(); j++) {
                        JSONObject l = (JSONObject) listings.get(j);
                        Listing listing = new Listing(l.getInt("id"), l.getDouble("price"), l.getString("url"), l.getString("store"), false);

                        if (!l.isNull("shippingCost"))
                            listing.shippingCost = l.getDouble("shippingCost");
                        if (!l.isNull("freeShipping"))
                            listing.freeShipping = l.getBoolean("freeShipping");

                        if (!l.isNull("number_of_reviews")) {
                            listing.hasReviewData = true;
                            listing.number_of_reviews = l.getInt("number_of_reviews");
                            listing.average_review = l.getDouble("average_review");
                        }
                        addOrUpdateListing(listing);
                    }

                    dumpJSONArrayToArrayList(productJSON, "features", features);
                    dumpJSONArrayToArrayList(productJSON, "images", images);

                    detailsLoaded = true;
                    executeDetailsLoadedCallbacks();
                    callback.onLoaded();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    callback.onError();
                    detailsLoaded = false;
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());
                callback.onError();
            }
        });
    }

    // given a JSON object with a JSONArray, dump its elements into an ArrayList
    private static void dumpJSONArrayToArrayList(JSONObject json, String jsonKey, ArrayList<String> arrayList) throws JSONException {
        JSONArray arrayElements = json.getJSONArray(jsonKey);
        for(int i = 0; i < arrayElements.length(); i++) {
            if (arrayList == null)
                arrayList = new ArrayList<>();
            arrayList.add(arrayElements.getString(i));
        }
    }
}
