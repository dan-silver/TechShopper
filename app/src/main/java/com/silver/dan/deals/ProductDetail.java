package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ProductDetail extends AppCompatActivity {

    @Bind(R.id.list_image) ImageView productImage;
    @Bind(R.id.productDetailTitle) TextView productDetailTitle;
    @Bind(R.id.productDetailPrice) TextView productDetailPrice;
    @Bind(R.id.productDetailListings) RecyclerView productListings;
    @Bind(R.id.product_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.productDetailFeatures) TextView productDetailFeatures;
    @Bind(R.id.productsDetailViewPages) HackyViewPager mImagesViewPager;

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

        mImagesViewPager.setAdapter(new SamplePagerAdapter(product));

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
                mImagesViewPager.getAdapter().notifyDataSetChanged();
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
    class SamplePagerAdapter extends PagerAdapter {
        private final Product product;
        private PhotoViewAttacher mAttacher;

        SamplePagerAdapter(Product product) {
            this.product = product;
        }

        @Override
        public int getCount() {
            return product.images.size();
        }



        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);

            Picasso.with(container.getContext())
                    .load(product.images.get(position))
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (mAttacher!=null) {
                                mAttacher.update();
                            } else {
                                mAttacher = new PhotoViewAttacher(photoView);
                            }
                        }

                        @Override
                        public void onError() {
                            // TODO Auto-generated method stub
                        }
                    });

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}