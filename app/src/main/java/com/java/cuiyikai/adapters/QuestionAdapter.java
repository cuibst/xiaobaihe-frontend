package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.adapters.viewholders.QuestionViewHolder;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    public JSONArray questionsArr;
    public QuestionAdapter(Context context)
    {
        mContext=context;
    }
    public void addQuestions(JSONArray arr)
    {
        questionsArr=arr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(mContext).inflate(R.layout.visit_history_content,parent,false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
            QuestionViewHolder holder=(QuestionViewHolder) holder1;
            JSONObject m=(JSONObject) questionsArr.get(position);
            String name = m.getString("question");
            String sub = m.getString("subject");
            holder.getCategory().setText("");
            holder.getView().setOnClickListener((View view) -> {
                Intent intent = new Intent(mContext, ProblemActivity.class);
                intent.putExtra("body 0",m.get("qBody").toString());
                intent.putExtra("sum","1");
                intent.putExtra("answer 0",m.get("ans").toString());
                intent.putExtra("type","single");
                intent.putExtra("subject 0","ignorant");
                mContext.startActivity(intent);
            });
            holder.getLabel().setText(name);
            switch (sub) {
                case "chinese" :
                    holder.getView().setBackgroundResource(R.drawable.chinese_radius);
                    holder.getImg().setImageResource(R.drawable.chinese);
                    break;
                case "math" :
                    holder.getView().setBackgroundResource(R.drawable.maths_radius);
                    holder.getImg().setImageResource(R.drawable.maths);
                    break;
                case "english" :
                    holder.getView().setBackgroundResource(R.drawable.english_radius);
                    holder.getImg().setImageResource(R.drawable.english);
                    break;
                case "physics" :
                    holder.getView().setBackgroundResource(R.drawable.physics_radius);
                    holder.getImg().setImageResource(R.drawable.physics);
                    break;
                case "chemistry" :
                    holder.getView().setBackgroundResource(R.drawable.chemistry_radius);
                    holder.getImg().setImageResource(R.drawable.chemistry);
                    break;
                case "biology" :
                    holder.getView().setBackgroundResource(R.drawable.biology_radius);
                    holder.getImg().setImageResource(R.drawable.biology);
                    break;
                case "history" :
                    holder.getView().setBackgroundResource(R.drawable.history_radius);
                    holder.getImg().setImageResource(R.drawable.history);
                    break;
                case "geo" :
                    holder.getView().setBackgroundResource(R.drawable.geography_radius);
                    holder.getImg().setImageResource(R.drawable.geography);
                    break;
                case "politics":
                    holder.getView().setBackgroundResource(R.drawable.politics_radius);
                    holder.getImg().setImageResource(R.drawable.politics);
                    break;
                default:
                    holder.getView().setBackgroundResource(R.drawable.recommend_radius);
                    holder.getImg().setImageResource(R.drawable.recommend);
                    break;
        }
    }

    @Override
    public int getItemCount() {
        if(questionsArr==null)
            return 0;
        return questionsArr.size();
    }

}
