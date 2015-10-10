package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


import butterknife.Bind;
import butterknife.ButterKnife;
public class ProductDetail extends AppCompatActivity {

    @Bind(R.id.list_image) ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra(Product.PRODUCT_SERIALIZED);

        Picasso.with(getApplicationContext()).load(product.image).into(productImage);

    }

}
