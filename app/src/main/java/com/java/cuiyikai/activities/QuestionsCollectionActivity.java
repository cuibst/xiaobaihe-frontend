package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.QuestionAdapter;
import com.java.cuiyikai.network.RequestBuilder;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsCollectionActivity extends AppCompatActivity {

    private ImageView backupImg;
    private String getQuestionsUrl="/api/problem/getSaves";
    private String removeQuestionUrl="/api/problem/deleteSave";
    private SwipeRecyclerView swipeRecyclerView;
    private QuestionAdapter questionAdapter;
    private JSONArray questionsArr;
    private JSONArray originArr;
    private TextView questionTxt;
    private SearchView searchQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_collection);
        swipeRecyclerView=findViewById(R.id.swipeRecyclerView);
        questionAdapter=new QuestionAdapter(QuestionsCollectionActivity.this);
        GetQuestions getQuestions=new GetQuestions();
        backupImg=findViewById(R.id.backImg);
        Thread getQuestionThread=new Thread(getQuestions);
        getQuestionThread.start();
        questionTxt=findViewById(R.id.questiontext);
        backupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchQuestion=findViewById(R.id.searchQuestion);

        searchQuestion.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionTxt.setVisibility(View.INVISIBLE);
            }
        });
        searchQuestion.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                questionTxt.setVisibility(View.VISIBLE);
                questionAdapter.addQuestions(questionsArr);
                questionAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchQuestion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.equals(""))
                {
                    questionAdapter.addQuestions(questionsArr);
                    questionAdapter.notifyDataSetChanged();
                }
                else
                {
                    JSONArray arr=new JSONArray();
                    for(int i=0;i<questionsArr.size();i++)
                    {
                        String s=questionsArr.get(i).toString();
                        if(s.contains(query))
                        {
                            arr.add(questionsArr.get(i));
                        }
                    }
                    questionAdapter.addQuestions(arr);
                    questionAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                {
                    questionAdapter.addQuestions(questionsArr);
                    questionAdapter.notifyDataSetChanged();
                }
                else
                {
                    JSONArray arr=new JSONArray();
                    for(int i=0;i<questionsArr.size();i++)
                    {
                        String s=questionsArr.get(i).toString();
                        if(s.contains(newText))
                        {
                            arr.add(questionsArr.get(i));
                        }
                    }
                    questionAdapter.addQuestions(arr);
                    questionAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(QuestionsCollectionActivity.this);
        swipeRecyclerView.setLayoutManager(linearLayoutManager);
        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(QuestionsCollectionActivity.this);
                deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setWidth(170);

                rightMenu.addMenuItem(deleteItem);

            }
        });
        swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                questionAdapter.questionsArr.remove(adapterPosition);
                questionAdapter.notifyItemRemoved(adapterPosition);
                RemoveQuestion removeQuestion=new  RemoveQuestion(adapterPosition);
                Thread removeThread=new Thread(removeQuestion);
                removeThread.start();
            }
        });
        swipeRecyclerView.setAdapter(questionAdapter);
    }

    MyHandler myHandler=new MyHandler();
    private class GetQuestions implements Runnable
    {

        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                JSONObject msg = RequestBuilder.sendBackendGetRequest(getQuestionsUrl, map, true);
                JSONArray arr=msg.getJSONArray("data");
                Message message=new Message();
                message.what=0;
                message.obj=arr.toString();
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch(msg.what)
            {
                case 0:
                    JSONArray array=new JSONArray();
                    JSONArray arr=JSONArray.parseArray(msg.obj.toString());
                    originArr=arr;
                    for(int i=0;i<arr.size();i++)
                    {
                        System.out.println(arr.get(i).toString());
                        JSONObject object=fixQuestions(JSONObject.parseObject(((JSONObject)arr.get(i)).get("problem").toString()));
                        object.put("subject",((JSONObject)arr.get(i)).get("subject"));
                        array.add(object);
                    }
                    questionAdapter.addQuestions(array);
                    questionsArr=array;
                    questionAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    public JSONObject fixQuestions(JSONObject questions)
    {
        String ans="";
//        String sub=questions.get("subject").toString();
        if(questions.get("qAnswer").toString().contains("A"))
            ans+="A";
        if(questions.get("qAnswer").toString().contains("B"))
            ans+="B";
        if(questions.get("qAnswer").toString().contains("C"))
            ans+="C";
        if(questions.get("qAnswer").toString().contains("D"))
            ans+="D";
        if(questions.get("qAnswer").toString().contains("E"))
            ans+="E";
        String qBody=questions.get("qBody").toString();
        List<String> chooses=new ArrayList<>();
        List<String> alp=new ArrayList<>();
        alp.add("A.");
        alp.add("B.");
        alp.add("C.");
        alp.add("D.");
        alp.add("E.");
        int head=0,tail=0;
        int cnt=0;
        String question="";
        while(tail!=-1&&cnt!=alp.size()) {
            tail=qBody.indexOf(alp.get(cnt));
            if(tail==-1)
            {
                chooses.add(qBody.substring(head,qBody.length()));
                break;
            }
            if(cnt==0)
            {
                question=qBody.substring(head,tail);
            }
            else
            {
                chooses.add(qBody.substring(head,tail));
            }
            cnt++;
            head=tail;
        }
        Map<String ,Object> map=new HashMap<>();
        map.put("qBody",qBody);
        map.put("chooses",chooses);
        map.put("question",question);
        map.put("ans",ans);
        return new JSONObject(map);
    }
    private class RemoveQuestion implements Runnable{
        int num;
        RemoveQuestion(int n)
        {
            num=n;
        }
        @Override
        public void run() {
            try {
                Map<String ,Object> map=new HashMap<>();
                map.put("problem",originArr.get(num));
                RequestBuilder.sendBackendPostRequest(removeQuestionUrl,new JSONObject(map), true);
                originArr.remove(num);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}