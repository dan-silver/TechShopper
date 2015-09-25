package com.silver.dan.deals;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fuel.Fuel;
import fuel.core.FuelError;
import fuel.core.Handler;
import fuel.core.Request;
import fuel.core.Response;

/**
 * Created by dan on 9/24/15.
 */
public class DrawerMainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

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
        final Menu m = navView.getMenu();

        Fuel.get("http://104.131.119.4/data/categories.json").responseJson(new Handler<JSONObject>() {
            @Override
            public void success(@NonNull Request request, @NonNull Response response, JSONObject jsonObject) {
                try {
                    JSONArray categoriesJSON = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < categoriesJSON.length(); i++) {
                        JSONObject o = (JSONObject) categoriesJSON.get(i);
                        m.add(o.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(@NonNull Request request, @NonNull Response response, @NonNull FuelError fuelError) {

            }
        });
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
}