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

/**
 * Created by dan on 9/19/15.
 */
public class ProductArrayAdapter extends ArrayAdapter<Product> {

    Context context;

    public ProductArrayAdapter(Context context, int textViewResourceId, List<Product> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView flowTextView;
        TextView txtProductPrice;
        TextView rating;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Product rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, null);

            holder = new ViewHolder();
            holder.flowTextView = (TextView) convertView.findViewById(R.id.product_title);
            holder.txtProductPrice = (TextView) convertView.findViewById(R.id.price);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            holder.rating = (TextView) convertView.findViewById(R.id.product_rating);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtProductPrice.setText("$" + rowItem.getMinPrice());
        holder.flowTextView.setText(rowItem.name);

        Picasso.with(context).load(rowItem.image).into(holder.imageView);

        holder.rating.setText(rowItem.getRating() + "/5");
        return convertView;
    }

}