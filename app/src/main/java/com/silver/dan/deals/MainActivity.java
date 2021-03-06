package com.silver.dan.deals;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import butterknife.ButterKnife;
import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "SILVER_APP";
    public static final String PRIMARY_CAT_ID = "PRIMARY_CAT_ID";
    public static final String SECONDARY_CAT_ID = "SECONDARY_CAT_ID";
    public static final String PRODUCT_ID = "PRODUCT_ID";

    private static final String IMAGE_SERVER_URL = "files.dansilver.me/";
    private static final String APP_SERVER_URL = "shop-prod.dansilver.me/";
    private static boolean USE_HTTPS = true;

    @Bind(R.id.category_toolbar) PagerSlidingTabStrip slidingTabs;
    @Bind(R.id.pager) ViewPager slidingTabsPager;
    @Bind(R.id.nvView) NavigationView navView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.snackbarPosition) CoordinatorLayout snackbarPosition;
    @Bind(R.id.top_toolbar) Toolbar top_toolbar;
    @Bind(R.id.appBarLayout) AppBarLayout appBarLayout;

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

    public void showToolbar() {
        appBarLayout.setExpanded(true);
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
                selectDrawerItem(navView.getMenu().getItem(0));
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());

                showSnackBar("Cannot load data.", "Retry", Snackbar.LENGTH_SHORT, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMainCategories();
                    }
                });
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
                getCurrentFragment().scrollToTop();
            }
        });

        top_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        slidingTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showToolbar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(top_toolbar);

        // Set the menu icon instead of the launcher icon.
        ActionBar ab = getSupportActionBar();
        assert ab != null;

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawer, top_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawer.setDrawerListener(mDrawerToggle);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        mDrawerToggle.syncState();

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        //only fetch category structure if not already loaded
        if (primary_categories.size() == 0) {
            loadMainCategories();
        } else if (navView.getMenu().size() == 0) {
            updateDrawerWithPrimaryCategories();
            slidingTabsAdapter.notifyDataSetChanged();
        }
    }

    private ProductListFragment getCurrentFragment() {
        return slidingTabsAdapter.getCurrentFragment();
    }

    private void displayCategory(PrimaryCategory category) {
        slidingTabsAdapter = new SlidingTabsAdapter(getSupportFragmentManager());
        slidingTabsAdapter.setCategory(category);
        slidingTabsPager.setAdapter(slidingTabsAdapter);
        slidingTabs.setViewPager(slidingTabsPager);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        int selected = menuItem.getItemId();
        mDrawer.closeDrawers();
        showToolbar();

        PrimaryCategory category = PrimaryCategory.findById(selected);
        displayCategory(category);

        top_toolbar.setTitle(category.name);
    }

    private void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_filter:
                getCurrentFragment().openFilterDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showSnackBar(String text, String action, int length, View.OnClickListener callback) {
        Snackbar.make(snackbarPosition, text, length)
                .setAction(action, callback)
                .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .show();
    }

}