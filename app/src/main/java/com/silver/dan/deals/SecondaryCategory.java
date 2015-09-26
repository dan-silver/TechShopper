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
    public boolean fetchingProducts = false;
    int id;
    private Context context;

    public SecondaryCategory(int id, String title, Context context) {
        this.name = title;
        this.id = id;
        this.context = context;
        products = new ArrayList<>();
        adapter = new ProductArrayAdapter(context,
                R.layout.list_item,
                products);
    }

    public void getProducts() {
        if (fetchingProducts) return; //don't run this method multiple times
        fetchingProducts = true;
        Fuel.get(context.getResources().getString(R.string.APP_URL) + "/secondary_categories/" + this.id + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                try {
                    JSONArray productsJSON = jsonObject.getJSONArray("products");
                    for (int i = 0; i < productsJSON.length(); i++) {
                        JSONObject o = (JSONObject) productsJSON.get(i);
                        products.add(new Product(o.getString("title"), o.getString("detailPageURL"), o.getString("image"), o.getDouble("price"), o.getString("source"), o.getString("thumbnail")));
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fetchingProducts = false;
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                fetchingProducts = false;
            }
        });
    }
}