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
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.products_list);

        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.setAdapter(sec_cat.adapter);
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
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