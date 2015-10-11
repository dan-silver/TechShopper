package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
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
    @Bind(R.id.product_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.productDetailFeatures) TextView productDetailFeatures;

    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //Your toolbar is now an action bar and you can use it like you always do, for example:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra(Product.PRODUCT_SERIALIZED);

        //recreate the empty arrays
        product.listings = new ArrayList<>();
        product.features = new ArrayList<>();
        product.images = new ArrayList<>();

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

                // convert the arraylist of features into a bullet point list
                StringBuilder sb = new StringBuilder();
                for (String s : product.features) {
                    sb.append("&#8226; ");
                    sb.append(s);
                    sb.append("<br/>");
                    sb.append("<br/>");
                }
                productDetailFeatures.setText(Html.fromHtml(sb.toString()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {

            }
        });


    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}