package com.silver.dan.deals;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "SILVER_APP";
    public static final String PRIMARY_CAT_ID = "PRIMARY_CAT_ID";
    public static final String SECONDARY_CAT_ID = "SECONDARY_CAT_ID";
    public static final String PRODUCT_ID = "PRODUCT_ID";

    private static final String IMAGE_SERVER_URL = "files.dansilver.me/";
    private static final String APP_SERVER_URL = "shop-prod.dansilver.me/";
    private static boolean USE_HTTPS = true;

    @Bind(R.id.tabs) PagerSlidingTabStrip slidingTabs;
    @Bind(R.id.pager) ViewPager slidingTabsPager;
    @Bind(R.id.nvView) NavigationView navView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.snackbarPosition) CoordinatorLayout snackbarPosition;

    private SlidingTabsAdapter slidingTabsAdapter;
    static ArrayList<PrimaryCategory> primary_categories = new ArrayList<>();

    public static String getImageServerUrl() {
        return "http" + (USE_HTTPS ? "s" : "") + "://" + IMAGE_SERVER_URL;
    }

    public static String getAppServerUrl() {
        return "http" + (USE_HTTPS ? "s" : "") + "://" + APP_SERVER_URL;
    }

    public void updateDrawerWithPrimaryCategories() {
        int order = 0;
        Menu menu = navView.getMenu();
        for (PrimaryCategory cat : primary_categories) {
            menu.add(0, cat.id, order++, cat.name);
        }
    }

    private void loadMainCategories() {
        Fuel.get(getAppServerUrl() + "primary_categories.json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                try {
                    JSONArray categoriesJSON = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < categoriesJSON.length(); i++) {
                        JSONObject primaryCategory = (JSONObject) categoriesJSON.get(i);
                        PrimaryCategory category = new PrimaryCategory(primaryCategory.getString("title"), primaryCategory.getInt("id"));
                        JSONArray secondaryCategories = primaryCategory.getJSONArray("secondaryCategories");
                        for (int j = 0; j < secondaryCategories.length(); j++) {
                            JSONObject secondaryCategory = (JSONObject) secondaryCategories.get(j);
                            SecondaryCategory secCategory = new SecondaryCategory(secondaryCategory.getInt("id"), secondaryCategory.getString("title"), category.id);
                            category.addSecondaryCategory(secCategory);
                        }
                        primary_categories.add(category);
                    }
                    updateDrawerWithPrimaryCategories();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                slidingTabsAdapter.notifyDataSetChanged();
                displayCategory(primary_categories.get(0));
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());
                Snackbar.make(snackbarPosition, "Cannot load data.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadMainCategories();
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                                .show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        ButterKnife.bind(this);

        slidingTabsAdapter = new SlidingTabsAdapter(getSupportFragmentManager());
        slidingTabsPager.setAdapter(slidingTabsAdapter);
        slidingTabs.setViewPager(slidingTabsPager);

        // When a tab is reselected (current tab only), then scroll to the top
        slidingTabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                ProductListFragment fragment = (ProductListFragment) slidingTabsPager.getAdapter().instantiateItem(slidingTabsPager, position);
                fragment.scrollToTop();
            }
        });

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        setupDrawerContent(navView);

        //only fetch category structure if not already loaded
        if (primary_categories.size() == 0) {
            loadMainCategories();
        } else if (navView.getMenu().size() == 0) {
            updateDrawerWithPrimaryCategories();
            slidingTabsAdapter.notifyDataSetChanged();
        }
    }

    private void displayCategory(PrimaryCategory category) {
        slidingTabsAdapter = new SlidingTabsAdapter(getSupportFragmentManager());
        slidingTabsAdapter.setCategory(category);
        slidingTabsPager.setAdapter(slidingTabsAdapter);
        slidingTabs.setViewPager(slidingTabsPager);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        int selected = menuItem.getItemId();
        mDrawer.closeDrawers();

        final PrimaryCategory category = PrimaryCategory.findById(selected);
        displayCategory(category);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public static Product findProduct(int pri_cat_id, int sec_cat_id, int product_id) {
        PrimaryCategory primaryCategory = PrimaryCategory.findById(pri_cat_id);
        SecondaryCategory secondaryCategory = primaryCategory.findSecondaryCatById(sec_cat_id);
        return secondaryCategory.findProductById(product_id);
    }

    public class SlidingTabsAdapter extends FragmentStatePagerAdapter {
        PrimaryCategory category;

        public SlidingTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setCategory(PrimaryCategory category) {
            this.category = category;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category.secondaryCategories.get(position).name;
        }

        @Override
        public int getCount() {
            if (category == null) {
                return 0;
            }
            return category.secondaryCategories.size();
        }

        @Override
        public Fragment getItem(int position) {
            SecondaryCategory sec_cat = category.secondaryCategories.get(position);
            return ProductListFragment.newInstance(sec_cat.id, category.id);
        }
    }
}