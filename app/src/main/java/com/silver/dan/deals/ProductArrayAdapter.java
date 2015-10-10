package com.silver.dan.deals;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductArrayAdapter extends ArrayAdapter<Product> {

    Context context;

    public ProductArrayAdapter(Context context, int textViewResourceId, List<Product> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        @Bind(R.id.list_image) ImageView imageView;
        @Bind(R.id.product_title) TextView flowTextView;
        @Bind(R.id.price) TextView txtProductPrice;
        @Bind(R.id.product_rating) TextView rating;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_row, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product rowItem = getItem(position);
        holder.txtProductPrice.setText(rowItem.getPriceString());
        holder.flowTextView.setText(rowItem.title);

        Picasso.with(context).load(rowItem.image).into(holder.imageView);

        if (rowItem.hasReviewData()) {
            holder.rating.setText(rowItem.getRating() + "/5");
        } else {
            holder.rating.setVisibility(View.GONE);
        }
        return convertView;
    }

}