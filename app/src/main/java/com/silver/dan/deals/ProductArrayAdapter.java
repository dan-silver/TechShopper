package com.silver.dan.deals;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ProductArrayAdapter extends RecyclerView.Adapter<Product.Holder> {

    Context context;
    private List<Product> products;

    public ProductArrayAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public Product.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new Product.Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(Product.Holder holder, int position) {
        Product rowItem = products.get(position);
        holder.txtProductPrice.setText(rowItem.getPriceString());
        holder.flowTextView.setText(rowItem.title);

        Picasso.with(context).load(rowItem.image).into(holder.imageView);

        if (rowItem.hasReviewData()) {
            holder.rating.setText(rowItem.getRating() + "/5");
        } else {
            holder.rating.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}