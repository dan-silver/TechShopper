package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dan on 9/26/15.
 */
public class ProductDetail extends AppCompatActivity {

    @InjectView(R.id.list_image)
    ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.inject(this);
        Intent intent = getIntent();

        String imageURL = intent.getStringExtra(Product.PRODUCT_IMAGE);
        Picasso.with(getApplicationContext()).load(imageURL).into(productImage);


    }

}
