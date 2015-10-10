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
                Intent intent = new Intent(view.getContext(), ProductDetail.class);
                intent.putExtra(Product.PRODUCT_SERIALIZED, sec_cat.products.get(position));
                getActivity().startActivity(intent);
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