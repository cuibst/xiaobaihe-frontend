package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.QuestionAdapter;
import com.java.cuiyikai.network.RequestBuilder;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsCollectionActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(QuestionsCollectionActivity.class);

    private QuestionAdapter questionAdapter;
    private JSONArray questionsArr;
    private JSONArray originArr;
    private TextView questionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_collection);
        SwipeRecyclerView swipeRecyclerView = findViewById(R.id.swipeRecyclerView);
        questionAdapter=new QuestionAdapter(QuestionsCollectionActivity.this);
        GetQuestions getQuestions=new GetQuestions();
        ImageView backupImg = findViewById(R.id.backImg);
        Thread getQuestionThread=new Thread(getQuestions);
        getQuestionThread.start();
        questionTxt=findViewById(R.id.questiontext);
        backupImg.setOnClickListener(v -> onBackPressed());
        SearchView searchQuestion = findViewById(R.id.searchQuestion);

        searchQuestion.setOnSearchClickListener(v -> questionTxt.setVisibility(View.INVISIBLE));
        searchQuestion.setOnCloseListener(() -> {
            questionTxt.setVisibility(View.VISIBLE);
            questionAdapter.addQuestions(questionsArr);
            questionAdapter.notifyDataSetChanged();
            return false;
        });
        searchQuestion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.equals(""))
                    questionAdapter.addQuestions(questionsArr);
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
                }
                questionAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                    questionAdapter.addQuestions(questionsArr);
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
                }
                questionAdapter.notifyDataSetChanged();
                return true;
            }
        });


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(QuestionsCollectionActivity.this);
        swipeRecyclerView.setLayoutManager(linearLayoutManager);
        swipeRecyclerView.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(QuestionsCollectionActivity.this);
            deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(170);

            rightMenu.addMenuItem(deleteItem);

        });
        swipeRecyclerView.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
            menuBridge.closeMenu();
            questionAdapter.questionsArr.remove(adapterPosition);
            questionAdapter.notifyItemRemoved(adapterPosition);
            RemoveQuestion removeQuestion=new  RemoveQuestion(adapterPosition);
            Thread removeThread=new Thread(removeQuestion);
            removeThread.start();
        });
        swipeRecyclerView.setAdapter(questionAdapter);
    }

    private final MyHandler myHandler=new MyHandler();

    private class GetQuestions implements Runnable
    {
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                String getQuestionsUrl = "/api/problem/getSaves";
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
            if (msg.what == 0) {
                JSONArray array = new JSONArray();
                JSONArray arr = JSON.parseArray(msg.obj.toString());
                originArr = arr;
                for (int i = 0; i < arr.size(); i++) {
                    logger.info("Array(i) : {}", arr.get(i));
                    JSONObject object = fixQuestions(JSON.parseObject(((JSONObject) arr.get(i)).get("problem").toString()));
                    object.put("subject", ((JSONObject) arr.get(i)).get("subject"));
                    array.add(object);
                }
                questionAdapter.addQuestions(array);
                questionsArr = array;
                questionAdapter.notifyDataSetChanged();
            }
        }
    }
    public JSONObject fixQuestions(JSONObject questions)
    {
        String ans="";
        String qAnswer = questions.getString("qAnswer");
        if(qAnswer.contains("A"))
            ans+="A";
        if(qAnswer.contains("B"))
            ans+="B";
        if(qAnswer.contains("C"))
            ans+="C";
        if(qAnswer.contains("D"))
            ans+="D";
        if(qAnswer.contains("E"))
            ans+="E";
        String qBody=questions.getString("qBody");
        List<String> chooses=new ArrayList<>();
        List<String> alp=new ArrayList<>();
        List<String> alp1=new ArrayList<>();
        alp.add("A.");
        alp.add("B.");
        alp.add("C.");
        alp.add("D.");
        alp.add("E.");
        alp1.add("A．");
        alp1.add("B．");
        alp1.add("C．");
        alp1.add("D．");
        alp1.add("E．");
        int head=0;
        int tail=0;
        int cnt=0;
        String question="";
        while(cnt != alp.size()) {
            tail=qBody.indexOf(alp.get(cnt));
            if(tail==-1)
                tail=qBody.indexOf(alp1.get(cnt));
            if(tail==-1)
            {
                chooses.add(qBody.substring(head));
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
                String removeQuestionUrl = "/api/problem/deleteSave";
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