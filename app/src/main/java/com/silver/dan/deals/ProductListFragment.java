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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.etsy.android.grid.StaggeredGridView;
import com.melnykov.fab.FloatingActionButton;

public class ProductListFragment extends Fragment {
    private static final String SECONDARY_CAT_ID = "sec_cat_id";
    private static final String PRIMARY_CAT_ID = "pri_cat_id";
    PrimaryCategory pri_cat;
    SecondaryCategory sec_cat;
    int sec_cat_id;
    int pri_cat_id;

    public static ProductListFragment newInstance(int sec_cat_id, int pri_cat_id) {
        ProductListFragment f = new ProductListFragment();
        Bundle b = new Bundle();
        b.putInt(SECONDARY_CAT_ID, sec_cat_id);
        b.putInt(PRIMARY_CAT_ID, pri_cat_id);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec_cat_id = getArguments().getInt(SECONDARY_CAT_ID);
        pri_cat_id = getArguments().getInt(PRIMARY_CAT_ID);
        pri_cat = PrimaryCategory.findById(pri_cat_id);
        sec_cat = pri_cat.findSecondaryCatById(sec_cat_id);
        sec_cat.getProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.products_list, container, false);
        StaggeredGridView productsList = (StaggeredGridView) view.findViewById(R.id.products_list);
        productsList.setAdapter(sec_cat.adapter);
        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the common element for the transition in this activity
                View androidRobotView = view.findViewById(R.id.list_image);

                Intent intent = new Intent(view.getContext(), ProductDetail.class);
                intent.putExtra(Product.PRODUCT_IMAGE, sec_cat.products.get(position).image);
                // create the transition animation - the images in the layouts
                // of both activities are defined with android:transitionName="robot"
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(), androidRobotView, "robot");
                // start the new activity

                getActivity().startActivity(intent, options.toBundle());
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(productsList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Filter by Price")
                        .items(R.array.items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            }
                        })
                        .show();
            }
        });
        return view;
    }
}