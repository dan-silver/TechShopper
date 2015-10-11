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

/**
 * Created by dan on 9/19/15.
 */
public class SecondaryCategory {
    public String name;
    public ArrayList<Product> products;
    public ProductArrayAdapter adapter;
    int id;
    private Context context;

    public SecondaryCategory(int id, String title, Context context) {
        this.name = title;
        this.id = id;
        this.context = context;
        products = new ArrayList<>();
        adapter = new ProductArrayAdapter(context, products);
    }

    public void getProducts() {
        Fuel.get(context.getResources().getString(R.string.APP_URL) + "/secondary_categories/" + this.id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                try {
                    JSONArray productsJSON = jsonObject.getJSONArray("products");
                    for (int i = 0; i < productsJSON.length(); i++) {
                        JSONObject o = (JSONObject) productsJSON.get(i);
                        Product product = new Product(o.getString("title"), o.getString("image"), o.getString("thumbnail"), o.getInt("id"));
                        JSONArray listings = o.getJSONArray("listings");
                        for (int j=0; j<listings.length(); j++) {
                            JSONObject l = (JSONObject) listings.get(j);
                            Listing listing = new Listing(l.getInt("id"), l.getDouble("price"), l.getString("url"), l.getString("store"), false);
                            if (!l.isNull("number_of_reviews")) {
                                listing.hasReviewData = true;
                                listing.number_of_reviews = l.getInt("number_of_reviews");
                                listing.average_review = l.getDouble("average_review");
                            }
                            product.addOrUpdateListing(listing);
                        }
                        products.add(product);
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());
            }
        });
    }
}