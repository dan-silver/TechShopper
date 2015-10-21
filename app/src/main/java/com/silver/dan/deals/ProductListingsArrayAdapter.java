package com.silver.dan.deals;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductListingsArrayAdapter extends RecyclerView.Adapter<ProductListingsArrayAdapter.Holder> {

    Context context;
    private List<Listing> listings;

    public ProductListingsArrayAdapter(Context context, List<Listing> listings) {
        this.context = context;
        this.listings = listings;
    }

    @Override
    public ProductListingsArrayAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_listing_detail, parent, false);
        return new ProductListingsArrayAdapter.Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ProductListingsArrayAdapter.Holder holder, int position) {
        final Listing rowItem = listings.get(position);
        holder.product_listing_price.setText(rowItem.getPriceString());
        holder.product_listing_store.setText(rowItem.store);
        if (rowItem.hasReviewData) {
            holder.product_listing_number_reviews.setText(rowItem.getNumberReviewsStr());
            holder.product_listing_average_review.setText(rowItem.getRatingString());
        } else {
            holder.product_listing_review_info.setVisibility(View.GONE);
        }

        if (rowItem.freeShipping) {
            holder.product_listing_shipping_info.setText(R.string.free_shipping);
        } else if (rowItem.shippingCost != 0.0){
            holder.product_listing_shipping_info.setText(rowItem.getShippingCostStr());
        } else {
            holder.product_listing_shipping_info.setVisibility(View.GONE);
        }

        if (rowItem.otherAttrs.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : rowItem.otherAttrs)
                sb.append(s);
            holder.product_listing_other_attributes.setText(sb);
        } else {
            holder.product_listing_other_attributes.setVisibility(View.GONE);
        }

        holder.product_listing_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(rowItem.url));
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @Bind(R.id.product_listing_price)
        TextView product_listing_price;

        @Bind(R.id.product_listing_store)
        TextView product_listing_store;

        @Bind(R.id.product_listing_average_review)
        TextView product_listing_average_review;

        @Bind(R.id.product_listing_number_reviews)
        TextView product_listing_number_reviews;

        @Bind(R.id.product_listing_review_info)
        LinearLayout product_listing_review_info;

        @Bind(R.id.product_listing_shipping_info)
        TextView product_listing_shipping_info;

        @Bind(R.id.product_listing_other_attributes)
        TextView product_listing_other_attributes;

        @Bind(R.id.product_listing_card_view)
        LinearLayout product_listing_cardview;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}