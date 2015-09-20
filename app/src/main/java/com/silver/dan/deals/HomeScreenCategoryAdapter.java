package com.silver.dan.deals;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dan on 9/20/15.
 */
public class HomeScreenCategoryAdapter extends ArrayAdapter<Category> {

        Context context;
        ArrayList<Category> categories;

        public HomeScreenCategoryAdapter(Context context, int textViewResourceId, ArrayList<Category> categories) {
            super(context, textViewResourceId, categories);
            this.context = context;
            this.categories = categories;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return categories.size();
        }

        @Override
        public Category getItem(int arg0) {
            // TODO Auto-generated method stub
            return categories.get(arg0);
        }

        /*private view holder class*/
        private class ViewHolder {
            TextView name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Category rowItem = getItem(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_row_category_option, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.category_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(rowItem.name);

            return convertView;
        }

    }
