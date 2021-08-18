package com.java.cuiyikai.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.java.cuiyikai.R;

import java.util.List;

public class SubjectAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int mType;
    private static boolean mIsEdit;

    public SubjectAdapter(Context context, List<String> list, int type){
        this.context = context;
        this.list = list;
        this.mType = type;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.channel_item, null);

        TextView tv = layout.findViewById(R.id.text_item);
        ImageView iv = layout.findViewById(R.id.iv_edit);
        tv.setText(list.get(i));

//        String info = "1";
//        if (!mIsEdit)
//            info = "0";
//        Log.v("mTag",info);
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

        return layout;
    }
    public void add(String subjectName){
        list.add(subjectName);
        notifyDataSetChanged();
    }

    public void remove(int index){
        if(index > 0 && index < list.size())
            list.remove(index);
        notifyDataSetChanged();
    }

    public static void setEdit(boolean isEdit){
        mIsEdit = isEdit;
    }

    public static boolean getEdit(){
        return mIsEdit;
    }
}
