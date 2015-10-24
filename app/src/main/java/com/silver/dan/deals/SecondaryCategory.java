package com.silver.dan.deals;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;

public class SecondaryCategory {
    public String name;
    public ArrayList<Product> products = new ArrayList<>();
    int id;
    int primaryCategoryId;

    public SecondaryCategory(int id, String title, int primaryCategoryId) {
        this.name = title;
        this.id = id;
        this.primaryCategoryId = primaryCategoryId;
    }

    public Product findProductById(int id) {
        for (Product p : products)
            if (p.id == id)
                return p;
        return null;
    }

    public void getProducts(final ProductListFragment.ProductsListenerCallback callback) {
        Fuel.get(MainActivity.getAppServerUrl() + "secondary_categories/" + this.id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                try {
                    JSONArray productsJSON = jsonObject.getJSONArray("products");
                    for (int i = 0; i < productsJSON.length(); i++) {
                        JSONObject o = (JSONObject) productsJSON.get(i);
                        Product product = new Product(o.getString("title"), o.getString("image"), o.getInt("id"), o.getString("brand"), primaryCategoryId, id);
                        JSONArray listings = o.getJSONArray("listings");
                        for (int j=0; j<listings.length(); j++) {
                            JSONObject l = (JSONObject) listings.get(j);
                            Listing listing = new Listing(l.getInt("id"), l.getDouble("price"));
                            if (!l.isNull("number_of_reviews")) {
                                listing.hasReviewData = true;
                                listing.number_of_reviews = l.getInt("number_of_reviews");
                                listing.average_review = l.getDouble("average_review");
                            }
                            product.addOrUpdateListing(listing);
                        }
                        products.add(product);
                    }

                    callback.onLoaded();

                } catch (JSONException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                callback.onError(fuelError.toString());
            }
        });
    }

}