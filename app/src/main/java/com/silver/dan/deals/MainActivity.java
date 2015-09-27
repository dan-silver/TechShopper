package com.silver.dan.deals;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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

import butterknife.InjectView;
import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;
import butterknife.ButterKnife;

/**
 * Created by dan on 9/24/15.
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "SILVER_APP";
    private DrawerLayout mDrawer;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager pager;


    private MyPagerAdapter adapter;
    static ArrayList<PrimaryCategory> primary_categories = new ArrayList<>();

    public void updateDrawerWithPrimaryCategories(Menu menu) {
        for (PrimaryCategory cat : primary_categories) {
            menu.add(0, cat.id, cat.id, cat.name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        ButterKnife.inject(this);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        final NavigationView navView = (NavigationView) findViewById(R.id.nvView);
        final Menu drawerMenu = navView.getMenu();

        setupDrawerContent(navView);


        Fuel.get(getResources().getString(R.string.APP_URL) + "/primary_categories.json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                Context context = getApplicationContext();
                try {
                    JSONArray categoriesJSON = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < categoriesJSON.length(); i++) {
                        JSONObject primaryCategory = (JSONObject) categoriesJSON.get(i);
                        PrimaryCategory category = new PrimaryCategory(primaryCategory.getString("title"), primaryCategory.getInt("id"), context);
                        JSONArray secondaryCategories = primaryCategory.getJSONArray("secondaryCategories");
                        for (int j=0;j<secondaryCategories.length();j++) {
                            JSONObject secondaryCategory = (JSONObject) secondaryCategories.get(j);
                            category.addSecondaryCategory(new SecondaryCategory(secondaryCategory.getInt("id"), secondaryCategory.getString("title"), context));
                        }
                        primary_categories.add(category);
                    }
                    updateDrawerWithPrimaryCategories(drawerMenu);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {
                Log.e(MainActivity.TAG, fuelError.toString());
            }
        });
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
//        new Runnable() {
//
//            @Override
//            public void run() {
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.setCategory(category);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
//            }
//        }.run();

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

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        PrimaryCategory category;

        public MyPagerAdapter(FragmentManager fm) {
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
            return SuperAwesomeCardFragment.newInstance(sec_cat.id, category.id);
        }
    }
}