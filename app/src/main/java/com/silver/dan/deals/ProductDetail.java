package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import butterknife.Bind;
import butterknife.ButterKnife;
public class ProductDetail extends AppCompatActivity {

    @Bind(R.id.list_image) ImageView productImage;
    @Bind(R.id.productDetailTitle) TextView productDetailTitle;
    @Bind(R.id.productDetailPrice) TextView productDetailPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra(Product.PRODUCT_SERIALIZED);

        productDetailTitle.setText(product.title);
        Picasso.with(getApplicationContext()).load(product.image).into(productImage);

        product.fetchDetailData(getApplicationContext(), new Product.Callback() {
            @Override
            public void onDetailsLoaded() {
                Log.v(MainActivity.TAG, product.title + " has loaded details");
                productDetailPrice.setText(product.getPriceString());

            }

            @Override
            public void onDetailsError() {

            }
        });


    }

}
