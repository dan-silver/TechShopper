package com.silver.dan.deals;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dan on 9/19/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<Product> {

    Context context;

    public CustomArrayAdapter(Context context, int textViewResourceId, List<Product> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtProductName;
//        TextView txtProduct;
        TextView txtProductPrice;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Product rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.txtProductName = (TextView) convertView.findViewById(R.id.menu_name);
            holder.txtProductPrice = (TextView) convertView.findViewById(R.id.price);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtProductName.setText(rowItem.name);
        holder.txtProductPrice.setText("$"+rowItem.price);
        Picasso.with(context).load(rowItem.image).into(holder.imageView);

        return convertView;
    }

}