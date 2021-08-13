package com.java.cuiyikai;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Search_adapter extends RecyclerView.Adapter<Search_adapter.RcyViewHolder> {
    Search_adapter(Context context)
    {
        mContext=context;
    }

    class RcyViewHolder extends RecyclerView.ViewHolder{
        private TextView labeltxt,categorytxt;
        public RcyViewHolder(View view)
        {
            super(view);
            labeltxt=view.findViewById(R.id.label);
            img=view.findViewById(R.id.img);
            categorytxt=view.findViewById(R.id.category);
        }
    }
    public void addSubject(JSONArray arr) {
        subject=arr;
    }
    private JSONArray subject=new JSONArray();
    private Context mContext;
    private String for_pic_chose;
    private ImageView img;
    public Search_adapter.RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
    {
        return new Search_adapter.RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
    }
    public void addpic(String s)
    {
        for_pic_chose=s;
    }
//    @Override
//    public int getItemViewType(int position) {
//        if(position%2==0)
//            return 0;
//        else
//            return 1;
//    }

    @Override
    public void onBindViewHolder(Search_adapter.RcyViewHolder holder, int position)
    {
        holder.labeltxt.setText(subject.getJSONObject(position).get("label").toString());
        switch (for_pic_chose)
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
        if(subject.getJSONObject(position).get("category").toString().length()==0)
            holder.categorytxt.setText("无");
        else
            holder.categorytxt.setText(subject.getJSONObject(position).get("category").toString());
    }
    @Override
    public int getItemCount(){
        return subject.size();
    }
}

