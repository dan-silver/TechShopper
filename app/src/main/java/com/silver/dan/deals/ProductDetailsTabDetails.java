package com.silver.dan.deals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailsTabDetails extends Fragment {
    Product product;


    @Bind(R.id.productDetailFeatures) TextView productDetailFeatures;

    public static ProductDetailsTabDetails newInstance(Product product) {
        ProductDetailsTabDetails f = new ProductDetailsTabDetails();
        Bundle b = new Bundle();
        b.putInt(MainActivity.PRIMARY_CAT_ID, product.primaryCategoryId);
        b.putInt(MainActivity.SECONDARY_CAT_ID, product.secondaryCategoryId);
        b.putInt(MainActivity.PRODUCT_ID, product.id);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pri_cat_id = getArguments().getInt(MainActivity.PRIMARY_CAT_ID);
        int sec_cat_id = getArguments().getInt(MainActivity.SECONDARY_CAT_ID);
        int product_id = getArguments().getInt(MainActivity.PRODUCT_ID);

        product = MainActivity.findProduct(pri_cat_id, sec_cat_id, product_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.product_detail_tab_details, container, false);
        ButterKnife.bind(this, view);
        product.addDetailsLoadedCallback(new Product.DetailsCallback() {
            @Override
            public void onLoaded() {
                Log.v(MainActivity.TAG, "details callback tab details");
                // convert the ArrayList of features into a bullet point list
                StringBuilder sb = new StringBuilder();
                for (String s : product.features) {
                    sb.append("&#8226; ");
                    sb.append(s);
                    sb.append("<br/>");
                    sb.append("<br/>");
                }

                productDetailFeatures.setText(Html.fromHtml(sb.toString()));
            }

            @Override
            public void onError() {

            }
        });
        return view;
    }
}