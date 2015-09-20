/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.silver.dan.deals;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeScreen extends Fragment {

    @InjectView(R.id.home_title)
    TextView title;

    @InjectView(R.id.category_option_list_view)
    ListView category_option_list_view;

    CategoryOptionAdapter adapter;
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.home_page, container, false);

    // TODO Use fields...
        ButterKnife.inject(this, view);

        return view;
  }

    private List<CategoryOption> getDataForListView() {
        ArrayList<CategoryOption> options = new ArrayList<CategoryOption>() {};
        options.add(new CategoryOption("A"));
        options.add(new CategoryOption("B"));
        options.add(new CategoryOption("C"));
        return options;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        title.setText("...Dashboard...");

        adapter = new CategoryOptionAdapter(getContext(), R.id.category_option_list_view, getDataForListView());
        category_option_list_view.setAdapter(adapter);
    }






    public class CategoryOptionAdapter extends ArrayAdapter<CategoryOption> {

        Context context;

        public CategoryOptionAdapter(Context context, int textViewResourceId, List<CategoryOption> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        List<CategoryOption> categoryOptionList = getDataForListView();

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return categoryOptionList.size();
        }

        @Override
        public CategoryOption getItem(int arg0) {
            // TODO Auto-generated method stub
            return categoryOptionList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        /*private view holder class*/
        private class ViewHolder {
            TextView name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            CategoryOption rowItem = getItem(position);

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
}