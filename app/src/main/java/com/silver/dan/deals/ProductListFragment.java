package com.silver.dan.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    int sec_cat_id;
    int pri_cat_id;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec_cat_id = getArguments().getInt(MainActivity.SECONDARY_CAT_ID);
        pri_cat_id = getArguments().getInt(MainActivity.PRIMARY_CAT_ID);
        pri_cat = PrimaryCategory.findById(pri_cat_id);
        sec_cat = pri_cat.findSecondaryCatById(sec_cat_id);

        if (sec_cat.adapter.allProducts().size() == 0)
            sec_cat.getProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.products_list, container, false);
        ButterKnife.bind(this, view);
        progressWheel.spin();

        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.setAdapter(sec_cat.adapter);
        sec_cat.adapter.setOnProductsLoadedListener(new ProductArrayAdapter.ProductsLoadedCallback() {
            @Override
            public void onLoaded() {
                progressWheel.stopSpinning();
            }

            @Override
            public void onError() {

            }
        });
        sec_cat.adapter.setOnItemClickListener(new ProductArrayAdapter.OnItemClickListener() {
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
                HashMap<String, Integer> brandCounts = sec_cat.adapter.getBrandCounts();
                final HashMap<String, Integer> brandPositionNameMap = new HashMap<>();

                ArrayList<String> brandLabels = new ArrayList<>();
                Iterator it = brandCounts.entrySet().iterator();
                int position = 0;

                //if a filter is currently being used, add the option "Remove Filter" at the top
                if (sec_cat.usingFilter()) {
                    brandLabels.add("Remove Filter");
                }

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    brandLabels.add(pair.getKey() + " (" + pair.getValue() + ")");
                    brandPositionNameMap.put((String) pair.getKey(), position);
                    position++;
                    it.remove(); // avoids a ConcurrentModificationException
                }

                new MaterialDialog.Builder(getContext())
                        .title("Filter by Brand")
                        .items(brandLabels.toArray(new CharSequence[brandLabels.size()]))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text == "Remove Filter") {
                                    ArrayList<Product> newProductsList = (ArrayList<Product>) sec_cat.adapter.allProducts();
                                    Collections.shuffle(newProductsList);
                                    sec_cat.adapter.animateTo(newProductsList);
                                    return;
                                }

                                int position = sec_cat.usingFilter() ? which - 1 : which;

                                // find the selected brand's products
                                ArrayList<Product> filteredProductsList = new ArrayList<>();
                                for (Product product : sec_cat.adapter.allProducts()) {
                                    if (brandPositionNameMap.get(product.brand) == position)
                                        filteredProductsList.add(product);
                                }

                                sec_cat.adapter.animateTo(filteredProductsList);
                            }
                        })
                        .show();
            }
        });
        return view;
    }
}