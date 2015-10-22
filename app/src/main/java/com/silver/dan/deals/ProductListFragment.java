package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductListFragment extends Fragment {
    PrimaryCategory pri_cat;
    SecondaryCategory sec_cat;
    public ProductArrayAdapter adapter;
    private ArrayList<FilterCategory> filteredCategories = new ArrayList<>();

    @Bind(R.id.products_list) RecyclerView mRecyclerView;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.progress_wheel) ProgressWheel progressWheel;

    public static ProductListFragment newInstance(int sec_cat_id, int pri_cat_id) {
        ProductListFragment f = new ProductListFragment();
        Bundle b = new Bundle();
        b.putInt(MainActivity.SECONDARY_CAT_ID, sec_cat_id);
        b.putInt(MainActivity.PRIMARY_CAT_ID, pri_cat_id);
        f.setArguments(b);
        return f;
    }

    interface ProductsListenerCallback {
        void onLoaded();

        void onError();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int sec_cat_id = getArguments().getInt(MainActivity.SECONDARY_CAT_ID);
        int pri_cat_id = getArguments().getInt(MainActivity.PRIMARY_CAT_ID);
        pri_cat = PrimaryCategory.findById(pri_cat_id);
        sec_cat = pri_cat.findSecondaryCatById(sec_cat_id);

        adapter = new ProductArrayAdapter(sec_cat.products);

        if (adapter.allProducts().size() == 0) {
            sec_cat.getProducts(new ProductsListenerCallback() {
                @Override
                public void onLoaded() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        progressWheel.stopSpinning();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.products_list, container, false);
        ButterKnife.bind(this, view);

        if (adapter.allProducts().size() > 0)
            progressWheel.stopSpinning();
        else
            progressWheel.spin();

        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new ProductArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Product product = sec_cat.products.get(position);

                Intent intent = new Intent(view.getContext(), ProductDetail.class);

                intent.putExtra(MainActivity.PRIMARY_CAT_ID, product.primaryCategoryId);
                intent.putExtra(MainActivity.SECONDARY_CAT_ID, product.secondaryCategoryId);
                intent.putExtra(MainActivity.PRODUCT_ID, product.id);

                getActivity().startActivity(intent);
            }
        });

        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //build dialog

                if (filteredCategories.size() == 0) { //build the filter categories array
                    HashMap<String, Integer> brandCounts = adapter.getBrandCounts();
                    int position = 0;
                    Iterator it = Utils.entriesSortedByValues(brandCounts).iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        filteredCategories.add(new FilterCategory(position, (String) pair.getKey(), (int) pair.getValue()));
                        position++;
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }


                new MaterialDialog.Builder(getContext())
                        .title("Filter by Brand")
                        .items(getBrandFilterLabels())
                        .itemsCallbackMultiChoice(getFilteredIndices(), new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                updateFilteredIndices(which);

                                //if there are no filters, display all products
                                ArrayList<Product> filteredProductsList = new ArrayList<>();
                                //if we just removed a filter, shuffle the products
                                if (usingFilter() && which.length == 0) {
                                    filteredProductsList = adapter.allProducts();
                                    Collections.shuffle(filteredProductsList);
                                    scrollToTop();
                                } else if (which.length == 0) {
                                    filteredProductsList = adapter.allProducts();
                                } else {
                                    CharSequence[] brandNames = getSelectedBrandNames();
                                    // find the selected brand's products
                                    for (Product product : adapter.allProducts()) {
                                        if (Utils.contains(brandNames, product.brand)) {
                                            filteredProductsList.add(product);
                                        }
                                    }
                                }

                                adapter.animateTo(filteredProductsList);

                                return true;
                            }
                        }).positiveText(R.string.filter)
                        .show();
            }
        });
        return view;
    }

    private CharSequence[] getBrandFilterLabels() {
        ArrayList<CharSequence> labels = new ArrayList<>();
        for (FilterCategory cat : filteredCategories)
            labels.add(cat.getLabel());
        return labels.toArray(new CharSequence[labels.size()]);
    }

    private CharSequence[] getSelectedBrandNames() {
        ArrayList<CharSequence> labels = new ArrayList<>();
        for (FilterCategory cat : filteredCategories)
            if (cat.selected) labels.add(cat.brand);
        return labels.toArray(new CharSequence[labels.size()]);
    }

    private class FilterCategory {
        int position;
        boolean selected;
        String brand;
        int count;

        public FilterCategory(int position, String brand, int count) {
            this.position = position;
            this.selected = false;
            this.count = count;
            this.brand = brand;
        }


        public CharSequence getLabel() {
            return brand + " (" + count + ")";
        }
    }

    private Integer[] getFilteredIndices() {
        ArrayList<Integer> indices = new ArrayList<>();
        for (FilterCategory cat : filteredCategories) {
            if (cat.selected) indices.add(cat.position);
        }

        return indices.toArray(new Integer[indices.size()]);
    }
    private void updateFilteredIndices(Integer[] which) {
        for (FilterCategory cat : filteredCategories) {
            cat.selected = Utils.contains(which, cat.position);
        }
    }
    public boolean usingFilter() {
        return adapter.products.size() < adapter.allProducts().size();
    }
}