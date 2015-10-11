package com.silver.dan.deals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetail extends AppCompatActivity {
    @Bind(R.id.product_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.productDetailTabs) PagerSlidingTabStrip productDetailTabs;
    @Bind(R.id.productDetailTabsPager) ViewPager slidingTabsPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Product product = (Product) getIntent().getSerializableExtra(Product.PRODUCT_SERIALIZED);
        //recreate the empty arrays
        product.listings = new ArrayList<>();
        product.features = new ArrayList<>();
        product.images = new ArrayList<>();

        SlidingTabsAdapter slidingTabsAdapter = new SlidingTabsAdapter(getSupportFragmentManager(), product);
        slidingTabsPager.setAdapter(slidingTabsAdapter);
        productDetailTabs.setViewPager(slidingTabsPager);
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