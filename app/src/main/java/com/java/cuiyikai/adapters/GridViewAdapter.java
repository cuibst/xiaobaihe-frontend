package com.java.cuiyikai.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> strList;
    private int hidePosition = AdapterView.INVALID_POSITION;
    private static boolean mIsEdit;
    private int mType;

    public GridViewAdapter(Context context, List<String> strList, int type) {
        this.context = context;
        this.strList = strList;
        this.mType = type;
    }

    public interface OnListSwapChangeListener {
        public void onListSwapChange();
    }

    private OnListSwapChangeListener onListSwapChangeListener = null;

    public void setOnListSwapChangeListener(OnListSwapChangeListener onListSwapChangeListener) {
        this.onListSwapChangeListener = onListSwapChangeListener;
    }

    @Override
    public int getCount() {
        return strList.size();
    }

    @Override
    public String getItem(int position) {
        return strList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        return null;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.channel_item, null);

        TextView tv = layout.findViewById(R.id.text_item);
        ImageView iv = layout.findViewById(R.id.iv_edit);
        tv.setText(strList.get(position));
        Log.v("grid", strList.get(position));
        if(mIsEdit){
            iv.setVisibility(View.VISIBLE);
            if (mType == 0)
                iv.setImageResource(R.drawable.x);
            else
                iv.setImageResource(R.drawable.add_channel);
        }
        else {
            iv.setVisibility(View.INVISIBLE);
        }
        if(position == hidePosition){
            layout.setVisibility(View.INVISIBLE);
        }
        return layout;
    }

    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        strList.remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            strList.add(destPos+1, getItem(draggedPos));
            strList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            strList.add(destPos, getItem(draggedPos));
            strList.remove(draggedPos+1);
        }
        hidePosition = destPos;

        if(onListSwapChangeListener != null)
            onListSwapChangeListener.onListSwapChange();

        notifyDataSetChanged();
    }
}