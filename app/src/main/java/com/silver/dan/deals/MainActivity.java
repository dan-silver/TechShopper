package com.silver.dan.deals;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

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

    @Bind(R.id.category_toolbar) PagerSlidingTabStrip slidingTabs;
    @Bind(R.id.pager) ViewPager slidingTabsPager;
    @Bind(R.id.nvView) NavigationView navView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.snackbarPosition) CoordinatorLayout snackbarPosition;
    @Bind(R.id.top_toolbar) Toolbar top_toolbar;

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

    public void showToolbar(ObservableRecyclerView rv) {
        moveToolbar(0, rv);
    }

    public void hideToolbar(ObservableRecyclerView rv) {
        moveToolbar(-slidingTabs.getHeight(), rv);
    }

    private void moveToolbar(float toTranslationY, final ObservableRecyclerView rv) {
        if (ViewHelper.getTranslationY(slidingTabs) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(slidingTabs), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(slidingTabs, translationY);
                ViewHelper.setTranslationY(rv, translationY);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) rv.getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                rv.requestLayout();
            }
        });
        animator.start();
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
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
                getCurrentFragment().scrollToTop();
            }
        });

        slidingTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showToolbar(getCurrentFragment().mRecyclerView);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(top_toolbar);

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

    private ProductListFragment getCurrentFragment() {
        return slidingTabsAdapter.getCurrentFragment();
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
}