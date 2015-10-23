package com.silver.dan.deals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductArrayAdapter extends RecyclerView.Adapter<ProductArrayAdapter.Holder> {
    public List<Product> products;
    public List<Product> removedProducts = new ArrayList<>();
    private OnItemClickListener listener;

    public ProductArrayAdapter(List<Product> products) {
        this.products = products;
        sortProducts();
    }

    public ArrayList<Product> allProducts() {
        ArrayList<Product> l = new ArrayList<>();
        l.addAll(products);
        l.addAll(removedProducts);
        return l;
    }

    public void sortProducts() {
        Collections.sort(products, new ProductComparator());
    }

    //count how many products for each brand
    public HashMap<String, Integer> getBrandCounts() {
        HashMap<String, Integer> brandCounts = new HashMap<>();
        for (Product p : allProducts()) {
            int count = 1;
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
    public void onBindViewHolder(final ProductArrayAdapter.Holder holder, int position) {
        Product rowItem = products.get(position);
        holder.txtProductPrice.setText(rowItem.getPriceString());
        holder.flowTextView.setText(rowItem.title);
        holder.loader.spin();

        Picasso.with(holder.imageView.getContext())
                .load(rowItem.getImageURL())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loader.stopSpinning();
                    }

                    @Override
                    public void onError() {
                        // TODO Auto-generated method stub

                    }
                });

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

    public Product removeItem(int position) {
        final Product product = products.remove(position);
        notifyItemRemoved(position);
        return product;
    }

    public void animateTo(List<Product> products) {
        applyAndAnimateRemovals(products);
        applyAndAnimateAdditions(products);
        applyAndAnimateMovedItems(products);
    }

    private void applyAndAnimateMovedItems(List<Product> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Product product = newModels.get(toPosition);
            final int fromPosition = products.indexOf(product);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Product model = products.remove(fromPosition);
        products.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(List<Product> newModels) {
        for (int i = products.size() - 1; i >= 0; i--) {
            final Product model = products.get(i);
            if (!newModels.contains(model)) {
                removedProducts.add(model);
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Product> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Product model = newModels.get(i);
            if (!products.contains(model)) {
                removedProducts.remove(model);
                addItem(i, model);
            }
        }
    }

    public void addItem(int position, Product model) {
        products.add(position, model);
        notifyItemInserted(position);
        sortProducts();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_image) ImageView imageView;
        @Bind(R.id.product_title) TextView flowTextView;
        @Bind(R.id.price) TextView txtProductPrice;
        @Bind(R.id.product_rating) TextView rating;
        @Bind(R.id.product_image_loading) ProgressWheel loader;

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

    public class ProductComparator implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return o1.images.size() - o2.images.size();
        }
    }
}