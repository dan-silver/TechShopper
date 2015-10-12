package com.silver.dan.deals;

/**
 * Created by dan on 10/11/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailsTabOverview extends Fragment {
    Product product;
    private RecyclerView.Adapter mAdapter;

    @Bind(R.id.productDetailTitle) TextView productDetailTitle;
    @Bind(R.id.productDetailPrice) TextView productDetailPrice;
    @Bind(R.id.productDetailListings) RecyclerView productListings;
    @Bind(R.id.productsDetailViewPages) ViewPager mImagesViewPager;
    @Bind(R.id.productsDetailPageCounter) TextView productsDetailPageCounter;
    @Bind(R.id.progress_wheel) ProgressWheel progressWheel;

    public static ProductDetailsTabOverview newInstance(Product product) {
        ProductDetailsTabOverview f = new ProductDetailsTabOverview();
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
        final View view = inflater.inflate(R.layout.product_detail_tab_overview, container, false);
        ButterKnife.bind(this, view);
        progressWheel.spin();

        productDetailTitle.setText(product.title);

        mImagesViewPager.setAdapter(new SamplePagerAdapter(product));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        productListings.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        productListings.setLayoutManager(mLayoutManager);

        mImagesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                productsDetailPageCounter.setText((position + 1) + "/" + mImagesViewPager.getAdapter().getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        product.addDetailsLoadedCallback(new Product.DetailsCallback() {
            @Override
            public void onLoaded() {
                Log.v(MainActivity.TAG, "details callback tab overview");
                productDetailPrice.setText(product.getPriceString());

                mImagesViewPager.getAdapter().notifyDataSetChanged();
                productsDetailPageCounter.setText("1/" + mImagesViewPager.getAdapter().getCount());

                mAdapter = new ProductListingsArrayAdapter(getContext(), product.listings);
                productListings.setAdapter(mAdapter);
            }

            @Override
            public void onError() {

            }
        });

        return view;
    }

    class SamplePagerAdapter extends PagerAdapter {
        private final Product product;

        SamplePagerAdapter(Product product) {
            this.product = product;
        }

        @Override
        public int getCount() {
            if (product.images == null)
                return 0;
            return product.images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final ImageView image = new ImageView(container.getContext());

            container.addView(image, ViewPager.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);

            Picasso.with(container.getContext())
                    .load(product.images.get(position))
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressWheel.stopSpinning();
                        }

                        @Override
                        public void onError() {

                        }
                    });

            return image;
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