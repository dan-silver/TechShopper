package com.silver.dan.deals;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.deanwild.flowtextview.FlowTextView;

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
        ImageView sourceLogo;
        FlowTextView flowTextView;
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
            holder.flowTextView = (FlowTextView) convertView.findViewById(R.id.menu_name);
            holder.txtProductPrice = (TextView) convertView.findViewById(R.id.price);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
            holder.sourceLogo = (ImageView) convertView.findViewById(R.id.source_logo);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtProductPrice.setText("$" + rowItem.price);
        Picasso.with(context).load(rowItem.thumbnail).into(holder.imageView);

        int imageResource = 0;
        switch (rowItem.source) {
            case "newegg":
                imageResource = R.mipmap.newegg;
                break;
            case "techbargains":
                imageResource = R.mipmap.techbargains;
                break;
            case "amazon":
                imageResource = R.mipmap.amazon;
                break;
        }
        holder.sourceLogo.setImageResource(imageResource);
        holder.flowTextView.setText(rowItem.name);


        holder.flowTextView.setTextSize(55);


        return convertView;
    }

}