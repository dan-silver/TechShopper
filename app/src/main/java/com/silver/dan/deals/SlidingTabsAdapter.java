package com.silver.dan.deals;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class SlidingTabsAdapter extends FragmentStatePagerAdapter {
    ProductListFragment mCurrentFragment;
    PrimaryCategory category;

    public ProductListFragment getCurrentFragment() {
        return mCurrentFragment;
    }

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

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((ProductListFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
}