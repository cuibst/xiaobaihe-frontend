package com.java.cuiyikai.adapters.category;

import androidx.recyclerview.widget.GridLayoutManager;

import com.java.cuiyikai.adapters.GridViewAdapter;

public class CategoryLayoutManagerSizeLookUp extends GridLayoutManager.SpanSizeLookup {

    private final GridViewAdapter adapter;

    private final GridLayoutManager layoutManager;

    public CategoryLayoutManagerSizeLookUp(GridViewAdapter adapter, GridLayoutManager layoutManager) {
        this.adapter = adapter;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getSpanSize(int position) {
        if (position == 0 || position == adapter.getUserSectionSize() + 1)
            return layoutManager.getSpanCount();
        return 1;
    }
}
