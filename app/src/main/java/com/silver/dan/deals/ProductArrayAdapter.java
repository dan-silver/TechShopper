package com.silver.dan.deals;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductArrayAdapter extends RecyclerView.Adapter<ProductArrayAdapter.Holder> {

    Context context;
    private List<Product> products;

    public ProductArrayAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    // Define listener member variable
    private OnItemClickListener listener;

    //count how many products for each brand
    public HashMap<String, Integer> getBrandCounts() {
        HashMap<String, Integer> brandCounts = new HashMap<>();
        for (Product p : products) {
            Integer count = 1;
            if (brandCounts.containsKey(p.brand)) {
                count = brandCounts.get(p.brand);
                count++;
            }
            brandCounts.put(p.brand, count);
        }
        return brandCounts;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ProductArrayAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ProductArrayAdapter.Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProductArrayAdapter.Holder holder, int position) {
        Product rowItem = products.get(position);
        holder.txtProductPrice.setText(rowItem.getPriceString());
        holder.flowTextView.setText(rowItem.title);

        Picasso.with(context).load(rowItem.image).into(holder.imageView);

        if (rowItem.hasReviewData()) {
            holder.rating.setText(rowItem.getRatingString());
        } else {
            holder.rating.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_image) ImageView imageView;
        @Bind(R.id.product_title) TextView flowTextView;
        @Bind(R.id.price) TextView txtProductPrice;
        @Bind(R.id.product_rating) TextView rating;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // Triggers click upwards to the adapter on click
                if (listener != null)
                    listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}