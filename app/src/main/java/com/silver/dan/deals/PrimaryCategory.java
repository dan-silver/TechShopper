package com.silver.dan.deals;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by dan on 9/25/15.
 */
public class PrimaryCategory {
    public String name;
    public int id;
    private Context context;
    public ArrayList<SecondaryCategory> secondaryCategories;

    public PrimaryCategory(String name, int id, Context applicationContext) {
        this.name = name;
        this.id = id;
        this.context = applicationContext;
        secondaryCategories = new ArrayList<>();
    }

    public void addSecondaryCategory(SecondaryCategory secondaryCategory) {
        secondaryCategories.add(secondaryCategory);
    }

    public static PrimaryCategory findById(int id) {
        for (PrimaryCategory cat : MainActivity.primary_categories) {
            if (cat.id == id)
                return cat;
        }
        return null;
    }

    public SecondaryCategory findSecondaryCatById(int sec_cat_id) {
        for (SecondaryCategory sec_cat : secondaryCategories) {
            if (sec_cat.id == sec_cat_id)
                return sec_cat;
        }
        return null;
    }
}
