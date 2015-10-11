package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
public class ProductDetail extends AppCompatActivity {

    @Bind(R.id.list_image) ImageView productImage;
    @Bind(R.id.productDetailTitle) TextView productDetailTitle;
    @Bind(R.id.productDetailPrice) TextView productDetailPrice;
    @Bind(R.id.productDetailListings) RecyclerView productListings;

    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra(Product.PRODUCT_SERIALIZED);

        //recreate the empty listings array
        product.listings = new ArrayList<>();

        productDetailTitle.setText(product.title);
        Picasso.with(getApplicationContext()).load(product.image).into(productImage);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        productListings.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        productListings.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        mAdapter = new ProductListingsArrayAdapter(getApplicationContext(), product.listings);
        productListings.setAdapter(mAdapter);


        product.fetchDetailData(getApplicationContext(), new Product.DetailsCallback() {
            @Override
            public void onLoaded() {
                Log.v(MainActivity.TAG, product.title + " has loaded details");
                productDetailPrice.setText(product.getPriceString());

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError() {

            }
        });


    }

}