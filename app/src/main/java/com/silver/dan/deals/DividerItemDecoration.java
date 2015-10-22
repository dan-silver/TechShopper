package com.silver.dan.deals;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mDividerHeight;
    private int mColor;

    private int mOffsets;
    private int mAdjustedOffsets;
    private boolean mToAdjustOffsets = false;



    /**
     * Create new DividerItemDecoration with the specified height and color
     * without additional offsets
     */
    public DividerItemDecoration(int dividerHeight, int color) {
        mDividerHeight = dividerHeight;
        mColor = color;
        mOffsets = mDividerHeight;
    }



    /**
     * Create new DividerItemDecoration with the specified height, color and offsets between
     * elements. Offsets can not be less than divider height.
     */
    public DividerItemDecoration(int dividerHeight, int color, int offsets, boolean adjustOffsets) {
        super();

        if(offsets < dividerHeight){
            throw new IllegalArgumentException("Offsets can not be less than divider height");
        }else if(adjustOffsets){
            mToAdjustOffsets = true;
            mAdjustedOffsets = (offsets - dividerHeight)/2;
        }

        mOffsets = offsets;
        mDividerHeight = dividerHeight;
        mColor = color;
    }



    /**
     * Create new DividerItemDecoration without any decor
     */
    public DividerItemDecoration() {
        super();
        mDividerHeight = 1;
        mColor = 0x8a000000;
    }



    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager == null){
            throw new RuntimeException("LayoutManager not found");
        }
        if(layoutManager.getPosition(view) != 0)
            outRect.top = mOffsets;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        Paint paint = new Paint();
        paint.setColor(mColor);

        int left = parent.getPaddingLeft();
        int right = left + parent.getWidth()-parent.getPaddingRight();

        for(int i = 0;i<parent.getChildCount();i++){

            View child = parent.getChildAt(i);

            int top = 0;
            //try to place divider in the middle of the space between elements
            if(!mToAdjustOffsets)
                top = child.getBottom();
            else top = child.getBottom() + mAdjustedOffsets;
            int bottom = top + mDividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

}