package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

import java.util.ArrayList;
import java.util.List;

public class RcyAdapter extends RecyclerView.Adapter<RcyAdapter.RcyViewHolder> {
    class RcyViewHolder extends RecyclerView.ViewHolder{
        private TextView tview;
        public RcyViewHolder(View view)
        {
            super(view);
            tview=view.findViewById(R.id.t1);
        }
    }


    private Context mContext;
    List<String> subject;
    public RcyAdapter(Context context) {
        this.mContext = context;
        subject = new ArrayList<String>() {
            {
                this.add("语文");
                this.add("数学");
                this.add("英语");
                this.add("物理");
                this.add("化学");
                this.add("生物");
                this.add("地理");
                this.add("历史");
                this.add("政治");
            }
        };
    }
    @Override
    public  RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
    {
        return new RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_linear_item,parent,false));
    }


    @Override
    public void onBindViewHolder(RcyViewHolder holder, int position)
    {
        holder.tview.setText(subject.get(position));
    }
    @Override
    public int getItemCount(){
        return subject.size();
    }
}
