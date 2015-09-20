package com.silver.dan.deals;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

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
public class Category {
    public String name;
    public ArrayList<Product> products;
    public ArrayAdapter<String> adapter;
    public boolean fetchingProducts = false;
    int index;
    ArrayList<String> te = new ArrayList<>();

    Category(String name, int index, Context c) {
        this.name = name;
        this.index = index;
        products = new ArrayList<>();
        te = new ArrayList<>();
        adapter = new ArrayAdapter<>(c
                , R.layout.list_item,
                te);
    }


    public void extractProductInfoIntoList() {
        Log.v(MainActivity.TAG, "making list for " + this.index);
        for (Product p : products) {
            te.add(p.title);
        }
    }

    public void getProducts() {
        Log.v(MainActivity.TAG, "getProducts() " + this.index);
        if (fetchingProducts) return; //don't run this method multiple times
        fetchingProducts = true;
        Fuel.get("http://104.131.119.4/data/" + this.index + ".json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(Request request, Response response, JSONObject jsonObject) {
                //parse response
                try {
                    JSONArray productsJSON = jsonObject.getJSONArray("products");
//                    int index = jsonObject.getInt("index");
                    for (int i = 0; i < productsJSON.length(); i++) {
                        JSONObject o = (JSONObject) productsJSON.get(i);
                        products.add(new Product(o.getString("title"), o.getString("link"), o.getString("price"), o.getDouble("price")));
                    }

                        if (adapter != null) {
                            extractProductInfoIntoList();
                            adapter.notifyDataSetChanged();
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fetchingProducts = false;
            }

            @Override
            public void failure(Request request, Response response, FuelError fuelError) {
                fetchingProducts = false;
            }
        });
    }
}