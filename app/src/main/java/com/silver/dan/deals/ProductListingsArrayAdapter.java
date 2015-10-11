package com.silver.dan.deals;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onBindViewHolder(ProductListingsArrayAdapter.Holder holder, int position) {
        Listing rowItem = listings.get(position);
        holder.product_listing_price.setText(rowItem.getPriceString());
        holder.product_listing_store.setText(rowItem.store);
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

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}