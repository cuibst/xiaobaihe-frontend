package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.adapters.VisitHistoryAdapter;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.QuestionAdapter;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.java.cuiyikai.adapters.viewholders.VisitHistoryViewHolder;

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
import java.util.concurrent.ExecutionException;

/**
 * <p>This class is used to show the collection of the wrong questions.</p>
 * <p>When you answer one question incorrectly, it will be added to this collection autonaticlly.
 * You can delete it by swiping left. It supports the conducting of  searching the whole collection.
 * Once the question contains the query text, it will be selected to show.
 * The layout is implemented by {@link SwipeRecyclerView},
 * its adapter also uses {@link VisitHistoryAdapter} and viewholder uses {@link VisitHistoryViewHolder}</p>
 */
public class QuestionsCollectionActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(QuestionsCollectionActivity.class);

    private QuestionAdapter questionAdapter;
    private JSONArray questionsArr;
    private JSONArray originArr;
    private TextView questionTxt;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_collection);
        myHandler=new MyHandler(Looper.getMainLooper());
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
                changeSearchItem(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                changeSearchItem(newText);
                return true;
            }
        });


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(QuestionsCollectionActivity.this);
        swipeRecyclerView.setLayoutManager(linearLayoutManager);
        swipeRecyclerView.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(QuestionsCollectionActivity.this);
            deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                    .setText("??????")
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

    //When the search query text changed or the searchButton is clicked , this method will be called ,
    //and the contents of the layout will change.
    private void changeSearchItem(String newText)
    {
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
    }

    private class GetQuestions implements Runnable
    {
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                String getQuestionsUrl = "/api/problem/getSaves";
                JSONObject msg = RequestBuilder.sendBackendGetRequest(getQuestionsUrl, map, true);
                JSONArray arr=msg.getJSONArray(ConstantUtilities.ARG_DATA);
                Message message=new Message();
                message.what=0;
                message.obj=arr.toString();
                myHandler.sendMessage(message);
            }
            catch (InterruptedException | ExecutionException | BackendTokenExpiredException e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        MyHandler(Looper looper)
        {
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == 0) {
                JSONArray array = new JSONArray();
                JSONArray arr = JSON.parseArray(msg.obj.toString());
                originArr = arr;
                for (int i = 0; i < arr.size(); i++) {
                    logger.info("Array(i) : {}", arr.get(i));
                    JSONObject object = fixQuestions(JSON.parseObject(((JSONObject) arr.get(i)).get(ConstantUtilities.ARG_PROBLEM).toString()));
                    object.put(ConstantUtilities.ARG_SUBJECT, ((JSONObject) arr.get(i)).get(ConstantUtilities.ARG_SUBJECT));
                    array.add(object);
                }
                questionAdapter.addQuestions(array);
                questionsArr = array;
                questionAdapter.notifyDataSetChanged();
            }
        }
    }
    //Questions get from backend need pre-process, this method is used to do it.
    public JSONObject fixQuestions(JSONObject questions)
    {
        String ans="";
        String qAnswer = questions.getString("qAnswer");
        //The questions' answer maybe multiple.
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
        alp1.add("A???");
        alp1.add("B???");
        alp1.add("C???");
        alp1.add("D???");
        alp1.add("E???");
        int head=0;
        int tail;
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
    //Used to tell the backend this question has been deleted.
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
                map.put(ConstantUtilities.ARG_PROBLEM,originArr.get(num));
                String removeQuestionUrl = "/api/problem/deleteSave";
                RequestBuilder.sendBackendPostRequest(removeQuestionUrl,new JSONObject(map), true);
                originArr.remove(num);
            }
            catch (InterruptedException |BackendTokenExpiredException|ExecutionException e)
            {
                e.printStackTrace();
            }
        }
    }
}