package com.java.cuiyikai.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.adapters.viewholders.CategoryViewHolder;

public class GridViewItemTouchCallback extends ItemTouchHelper.Callback {

    private final GridViewAdapter adapter;

    public GridViewItemTouchCallback(GridViewAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(!adapter.isEditable())
            return makeMovementFlags(0, 0);
        if (viewHolder instanceof CategoryViewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        } else {
            return makeMovementFlags(0, 0);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder == null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        throw new UnsupportedOperationException("No swipe!");
    }


}
