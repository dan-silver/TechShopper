package com.silver.dan.deals;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetail extends AppCompatActivity {
    @Bind(R.id.product_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.productDetailTabs) PagerSlidingTabStrip productDetailTabs;
    @Bind(R.id.productDetailTabsPager) ViewPager slidingTabsPager;
    @Bind(R.id.snackbarPosition) CoordinatorLayout snackbarPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int pri_cat_id = getIntent().getIntExtra(MainActivity.PRIMARY_CAT_ID, -1);
        int sec_cat_id = getIntent().getIntExtra(MainActivity.SECONDARY_CAT_ID, -1);
        int product_id = getIntent().getIntExtra(MainActivity.PRODUCT_ID, -1);


        final Product product = MainActivity.findProduct(pri_cat_id, sec_cat_id, product_id);

        final SlidingTabsAdapter slidingTabsAdapter = new SlidingTabsAdapter(getSupportFragmentManager(), product);
        slidingTabsPager.setAdapter(slidingTabsAdapter);
        productDetailTabs.setViewPager(slidingTabsPager);

        product.fetchDetailData();
        product.addDetailsLoadedCallback(new Product.DetailsCallback() {
            @Override
            public void onLoaded() {

            }

            @Override
            public void onError() {
                Snackbar.make(snackbarPosition, "Cannot load data.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                product.fetchDetailData();
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                        .show();
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


    public class SlidingTabsAdapter extends FragmentStatePagerAdapter {
        String[] pages = {"Overview", "Details"};
        Product product;
        public SlidingTabsAdapter(FragmentManager fm, Product product) {
            super(fm);
            this.product = product;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return pages[position];
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (pages[position]) {
                case "Overview":
                    return ProductDetailsTabOverview.newInstance(product);
                case "Details":
                    return ProductDetailsTabDetails.newInstance(product);
            }
            return null;
        }
    }
}