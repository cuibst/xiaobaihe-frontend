package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    public ItemAdapter(Context context, String s)
    {
        mContext=context;
        chooseSubject=s;
    }
    public  String chooseSubject;
    public JSONArray subject=new JSONArray();
    private Context mContext;
    private LinearLayout searchline;
    private ImageView img;
    public void addSubject(JSONArray arr) {
        subject=arr;
    }
    public void addMoreSubject(JSONArray arr)
    {
        subject.addAll(arr);
        System.out.println(subject.toString());
    }
    public void clearSubject()
    {
        subject.clear();
    }
    public ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
    {
        return new ItemAdapter.ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
    }
    @Override
    public void onBindViewHolder(ItemAdapter.ItemViewHolder holder, int position)
    {
        holder.labeltxt.setText(subject.getJSONObject(position).get("name").toString());
        switch (chooseSubject)
        {
            case "physics":
                img.setImageResource(R.drawable.phy);
                break;
            case "chemistry":
                img.setImageResource(R.drawable.che);
                break;
            case "biology":
                img.setImageResource(R.drawable.bio);
                break;
            default:
                img.setImageResource(R.drawable.book);
                break;

        }
        holder.categorytxt.setText("");
    }
    @Override
    public int getItemCount(){
        return subject.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView labeltxt,categorytxt;
        public ItemViewHolder(View view)
        {
            super(view);
            labeltxt=view.findViewById(R.id.label);
            img=view.findViewById(R.id.img);
            categorytxt=view.findViewById(R.id.category);
            searchline=view.findViewById(R.id.search_line);
            searchline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent f = new Intent(mContext, EntityActivity.class);
                    f.putExtra("name", labeltxt.getText());
                    f.putExtra("subject", chooseSubject);
                    mContext.startActivity(f);
                }
            });
        }
    }
}