package com.java.cuiyikai.adapters;

import androidx.recyclerview.widget.GridLayoutManager;

import com.java.cuiyikai.adapters.GridViewAdapter;

/**
 * Modified size look up to adjust the item size in {@link androidx.recyclerview.widget.RecyclerView} in {@link com.java.cuiyikai.activities.CategoryActivity}
 */
public class CategoryLayoutManagerSizeLookUp extends GridLayoutManager.SpanSizeLookup {

    private final GridViewAdapter adapter;

    private final GridLayoutManager layoutManager;

    public CategoryLayoutManagerSizeLookUp(GridViewAdapter adapter, GridLayoutManager layoutManager) {
        this.adapter = adapter;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getSpanSize(int position) {
        if (position == 0 || position == adapter.getUserSectionSize() + 1) // title section
            return layoutManager.getSpanCount();
        return 1; //normal item
    }
}
