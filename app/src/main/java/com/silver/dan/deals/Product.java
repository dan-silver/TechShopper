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

public class Product {

    public String title, image, brand;
    public int id;
    public ArrayList<Listing> listings = new ArrayList<>();
    public ArrayList<String> features = new ArrayList<>();
    public ArrayList<String> images = new ArrayList<>();
    public int primaryCategoryId, secondaryCategoryId;
    public boolean detailsLoaded = false;
    private ArrayList<DetailsCallback> callbacks = new ArrayList<>();

    Product(String title, String image, int id, String brand, int primaryCategoryId, int secondaryCategoryId) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.brand = brand;
        this.primaryCategoryId = primaryCategoryId;
        this.secondaryCategoryId = secondaryCategoryId;
    }

    public String getPriceString() {
        return "$" + String.format( "%.2f", getMinPrice() );
    }

    public String getRatingString() {
        return String.format("%s/5", getRating());
    }

    public String getImageURL() {
        return MainActivity.IMAGE_SERVER_URL + this.image;
    }

    public String getImageURL(int position) {
        return MainActivity.IMAGE_SERVER_URL + images.get(position);
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
            if (oldListing.id == newListing.id) {
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


    //@todo modify method to handle error cases. pass boolean for success/failure?
    private void executeDetailsLoadedCallbacks() {
        for (DetailsCallback callback : callbacks)
            callback.onLoaded();
        callbacks.clear();
    }

    protected void fetchDetailData(Context context) {
        if (detailsLoaded) {
            executeDetailsLoadedCallbacks();
            return;
        }
        Fuel.get(context.getResources().getString(R.string.APP_URL) + "/products/" + id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject productJSON) {
                try {
                    JSONArray listings = productJSON.getJSONArray("listings");
                    for (int j = 0; j < listings.length(); j++) {
                        JSONObject listingJSON = (JSONObject) listings.get(j);
                        Listing listing = new Listing(listingJSON.getInt("id"), listingJSON.getDouble("price"), listingJSON.getString("url"), listingJSON.getString("store"), false);

                        if (!listingJSON.isNull("shippingCost"))
                            listing.shippingCost = listingJSON.getDouble("shippingCost");
                        if (!listingJSON.isNull("freeShipping"))
                            listing.freeShipping = listingJSON.getBoolean("freeShipping");

                        if (!listingJSON.isNull("number_of_reviews")) {
                            listing.hasReviewData = true;
                            listing.number_of_reviews = listingJSON.getInt("number_of_reviews");
                            listing.average_review = listingJSON.getDouble("average_review");
                        }

                        dumpJSONArrayToArrayList(listingJSON, "otherAttrs", listing.otherAttrs);

                        addOrUpdateListing(listing);
                    }

                    dumpJSONArrayToArrayList(productJSON, "features", features);
                    dumpJSONArrayToArrayList(productJSON, "images", images);


                    detailsLoaded = true;
                    executeDetailsLoadedCallbacks();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
//                Log.e(MainActivity.TAG, fuelError.toString());
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
