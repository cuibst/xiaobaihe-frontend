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
    private Context mContext;
    public JSONArray questionsArr;
    public QuestionAdapter(Context context)
    {
        mContext=context;
    }
    public void addQuestions(JSONArray arr)
    {
        questionsArr=arr;
    }
//    public void ReportRemove(int a)
//    {
//        System.out.println(a);
//        System.out.println(allData.get(a).getClass());
//        System.out.println(allData.get(a).toString());
//        if(!(allData.get(a) instanceof Date))
//        {
//            RemoveHistory removeHistory=new RemoveHistory(a);
//            Thread removethread=new Thread(removeHistory);
//            removethread.start();
//            for(int i=0;i<historyArr.size();i++)
//            {
//                if(historyArr.get(i).equals(allData.get(a)))
//                {
//                    historyArr.remove(i);
//                    addHistory(historyArr);
//                }
//            }
//
//        }
//    }
//    public List<Integer> getTimeNumber(){
//        return timeNumber;
//    }
//    public void initDateList()
//    {
//        timeNumber=new ArrayList<>();
//        datelist=new ArrayList<>();
//        allData=new HashMap<>();
//        cnt=0;
//        Date mDate=new Date();
//        for(int i=0;i<historyArr.size()+cnt;i++)
//        {
//            long time=Long.parseLong(historyArr.getJSONObject(i-cnt).get("time").toString());
//            Date date=new Date(time);
//            if(i==0)
//            {
//                allData.put(i,date);
//                timeNumber.add(i);
//                cnt++;
//                mDate=date;
//                datelist.add(date);
//            }
//            else if(mDate.getYear()!=date.getYear()||mDate.getMonth()!=date.getMonth()||date.getDate()!=mDate.getDate())
//            {
//                mDate=date;
//                allData.put(i,date);
//                timeNumber.add(i);
//                cnt++;
//                datelist.add(date);
//            }
//            else
//                allData.put(i,historyArr.getJSONObject(i-cnt));
//        }
//    }
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
            holder.category.setText("");
            holder.view.setOnClickListener((View view) -> {
                Intent intent = new Intent(mContext, ProblemActivity.class);
                intent.putExtra("body 0",m.get("qBody").toString());
                intent.putExtra("sum","1");
                intent.putExtra("answer 0",m.get("ans").toString());
                intent.putExtra("type","single");
                intent.putExtra("subject 0","ignorant");
                mContext.startActivity(intent);
            });
            holder.label.setText(name);
            switch (sub) {
                case "chinese" :
                    holder.view.setBackgroundResource(R.drawable.chinese_radius);
                    holder.img.setImageResource(R.drawable.chinese);
                    break;
                case "math" :
                    holder.view.setBackgroundResource(R.drawable.maths_radius);
                    holder.img.setImageResource(R.drawable.maths);
                    break;
                case "english" :
                    holder.view.setBackgroundResource(R.drawable.english_radius);
                    holder.img.setImageResource(R.drawable.english);
                    break;
                case "physics" :
                    holder.view.setBackgroundResource(R.drawable.physics_radius);
                    holder.img.setImageResource(R.drawable.physics);
                    break;
                case "chemistry" :
                    holder.view.setBackgroundResource(R.drawable.chemistry_radius);
                    holder.img.setImageResource(R.drawable.chemistry);
                    break;
                case "biology" :
                    holder.view.setBackgroundResource(R.drawable.biology_radius);
                    holder.img.setImageResource(R.drawable.biology);
                    break;
                case "history" :
                    holder.view.setBackgroundResource(R.drawable.history_radius);
                    holder.img.setImageResource(R.drawable.history);
                    break;
                case "geo" :
                    holder.view.setBackgroundResource(R.drawable.geography_radius);
                    holder.img.setImageResource(R.drawable.geography);
                    break;
                case "politics":
                    holder.view.setBackgroundResource(R.drawable.politics_radius);
                    holder.img.setImageResource(R.drawable.politics);
                    break;
                default:
                    holder.view.setBackgroundResource(R.drawable.recommend_radius);
                    holder.img.setImageResource(R.drawable.recommend);
                    break;
        }
    }

    @Override
    public int getItemCount() {
        if(questionsArr==null)
            return 0;
        return questionsArr.size();
    }

//    private class RemoveHistory implements Runnable
//    {
//        private int  num;
//        public RemoveHistory(int i)
//        {
//            num=i;
//        }
//        @Override
//        public void run() {
//            try {
//                Map<String, String> map = new HashMap<>();
//                map.put("name",((JSONObject)allData.get(num)).get("name").toString());
//                map.put("subject",((JSONObject)allData.get(num)).get("subject").toString());
//                RequestBuilder.sendBackendGetRequest(removeHistoryUrl, map, true);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
}
