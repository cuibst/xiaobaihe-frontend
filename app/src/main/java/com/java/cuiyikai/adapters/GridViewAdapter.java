package com.java.cuiyikai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;
import com.java.cuiyikai.entities.CategoryItem;
import com.java.cuiyikai.entities.CategoryObject;
import com.java.cuiyikai.adapters.viewholders.CategoryTitleHolder;
import com.java.cuiyikai.adapters.viewholders.CategoryViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} for {@link RecyclerView} in {@link CategoryActivity}
 */
public class GridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isEditable = false;

    private final CategoryActivity context;
    private final List<CategoryObject> itemList;

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_TITLE = 2;
    private static final Logger logger = LoggerFactory.getLogger(GridViewAdapter.class);

    private int userSectionSize;

    /**
     * @return the number of elements that user selects.
     */
    public int getUserSectionSize() {
        return userSectionSize;
    }

    public void setEditable(boolean editable) {
        if(isEditable != editable) {
            isEditable = editable;
            notifyDataSetChanged();
        }
    }

    /**
     * @return edit status
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * @param context the view is inflated, normally {@link CategoryActivity}
     * @param itemList objects being demonstrated in the view.
     * @param userSectionSize origin number of objects in user section.
     */
    public GridViewAdapter(CategoryActivity context, List<CategoryObject> itemList, int userSectionSize) {
        this.context = context;
        this.itemList = itemList;
        this.userSectionSize = userSectionSize;
    }

    /**
     * {@inheritDoc}
     * @param fromPosition
     * @param toPosition
     */
    public void onItemMove(int fromPosition, int toPosition) {
        if(toPosition == 0)
            return;
        logger.info("Move from {} to {}", fromPosition, toPosition);
        if((inUserSection(fromPosition) && inUserSection(toPosition)) || (fromPosition > userSectionSize + 1 && toPosition > userSectionSize + 1)) { //user section not changed
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            notifyItemMoved(fromPosition, toPosition);
        } else if(inUserSection(fromPosition) && toPosition > userSectionSize) { //move out from user section
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            userSectionSize --;
            notifyItemMoved(fromPosition, toPosition);
        } else if(fromPosition > userSectionSize + 1 && toPosition <= userSectionSize + 1) { //move into user section.
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            userSectionSize ++;
            notifyItemMoved(fromPosition, toPosition);
        }

        List<String> subjectList = new ArrayList<>();
        for(CategoryObject object : itemList.subList(1, userSectionSize + 1)) //update the category list.
            subjectList.add(object.getName());

        ((MainApplication) context.getApplication()).setSubjects(subjectList);
    }

    /**
     * {@inheritDoc}
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View categoryItem = LayoutInflater.from(context).inflate(R.layout.channel_item, parent, false);
            return new CategoryViewHolder(categoryItem);
        } else {
            View categoryItem = LayoutInflater.from(context).inflate(R.layout.channel_title_item, parent, false);
            return new CategoryTitleHolder(categoryItem);
        }
    }

    /**
     * {@inheritDoc}
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CategoryTitleHolder) { //title
            ((CategoryTitleHolder) holder).getTextView().setText(itemList.get(position).getName());
        } else { //item
            ((CategoryViewHolder) holder).getTextView().setText(itemList.get(position).getName());
            if(!isEditable)
                ((CategoryViewHolder) holder).getImageView().setVisibility(View.GONE);
            else
                ((CategoryViewHolder) holder).getImageView().setVisibility(View.VISIBLE);
            if(inUserSection(position)) { // in user section, this will effect the click binding
                ((CategoryViewHolder) holder).getImageView().setImageResource(R.drawable.x);
                if(isEditable)
                    ((CategoryViewHolder) holder).getWholeView().setOnClickListener((View v) -> {
                        CategoryObject object = itemList.get(position);
                        itemList.remove(position);
                        itemList.add(object);
                        userSectionSize --;
                        List<String> subjectList = new ArrayList<>();
                        for(CategoryObject obj : itemList.subList(1, userSectionSize + 1))
                            subjectList.add(obj.getName());

                        ((MainApplication)(context).getApplication()).setSubjects(subjectList);
                        notifyDataSetChanged();
                    });
                else
                    ((CategoryViewHolder) holder).getWholeView().setOnLongClickListener((View v) -> {
                        setEditable(true);
                        context.onEditableChanged(true);
                        return true;
                    });
            } else { // outside user section, the plus button is always shown.
                ((CategoryViewHolder) holder).getImageView().setVisibility(View.VISIBLE);
                ((CategoryViewHolder) holder).getImageView().setImageResource(R.drawable.add_channel);
                ((CategoryViewHolder) holder).getWholeView().setOnClickListener((View v) -> {
                    CategoryObject object = itemList.get(position);
                    itemList.remove(position);
                    itemList.add(userSectionSize + 1, object);
                    userSectionSize ++;
                    List<String> subjectList = new ArrayList<>();
                    for(CategoryObject obj : itemList.subList(1, userSectionSize + 1))
                        subjectList.add(obj.getName());

                    ((MainApplication)(context).getApplication()).setSubjects(subjectList);
                    notifyDataSetChanged();
                });
                if(!isEditable)
                    ((CategoryViewHolder) holder).getWholeView().setOnLongClickListener((View v) -> {
                        setEditable(true);
                        context.onEditableChanged(true);
                        return true;
                    });
            }
        }
    }

    /**
     * Check whether a position in user section.
     * @param position item's adapter position.
     * @return the result.
     */
    public boolean inUserSection(int position) {
        return position <= userSectionSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * {@inheritDoc}
     * @param position
     * @return {@link #TYPE_ITEM} for category item and {@link #TYPE_TITLE} for title item.
     */
    @Override
    public int getItemViewType(int position) {
        if(itemList.get(position) instanceof CategoryItem)
            return TYPE_ITEM;
        else
            return TYPE_TITLE;
    }
}

