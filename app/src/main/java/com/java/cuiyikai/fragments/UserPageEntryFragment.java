package com.java.cuiyikai.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.activities.LoginActivity;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.activities.QuestionsCollectionActivity;
import com.java.cuiyikai.activities.VisitHistoryActivity;
import com.java.cuiyikai.network.RequestBuilder;
import java.util.HashMap;
import java.util.Map;

public class UserPageEntryFragment extends Fragment {


    public UserPageEntryFragment() {
        // Required empty public constructor
    }
    private LinearLayout mQuestion;
    private LinearLayout mQuestionsCollection;
    private LinearLayout mCollect;
    private LinearLayout mHistory;
    private LinearLayout mLogIn;
    private TextView mUserName;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1) {
            mUserName.setText(((MainApplication) getActivity().getApplication()).getSaveUsername());
            mLogIn.setOnClickListener((View v) -> {
                RequestBuilder.logOut();
                Toast.makeText(getActivity(),"Logged out！", Toast.LENGTH_SHORT).show();
                mUserName.setText("请先登录");
                mLogIn.setOnClickListener((View vi) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                });
            });
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_page_entry, container, false);
        mQuestion = view.findViewById(R.id.user_question);
        mCollect = view.findViewById(R.id.user_collect);
        mHistory = view.findViewById(R.id.user_history);
        mUserName = view.findViewById(R.id.user_name);
        mLogIn = view.findViewById(R.id.login_btn);
        mQuestionsCollection=view.findViewById(R.id.user_wrong_question);
        if(RequestBuilder.checkedLogin()){
            mUserName.setText(((MainApplication) getActivity().getApplication()).getSaveUsername());
            mLogIn.setOnClickListener((View v) -> {
                RequestBuilder.logOut();
                Toast.makeText(getActivity(),"Logged out！", Toast.LENGTH_SHORT).show();
                mUserName.setText("请先登录");
                mLogIn.setOnClickListener((View vi) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                });
            });
        }
        else
            mLogIn.setOnClickListener((View v) -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 1);
            });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RequestBuilder.checkedLogin()){
                    Toast.makeText(getContext(), "您尚未登录", 100).show();
                }
                Intent intent=new Intent(getActivity(), VisitHistoryActivity.class);
                startActivity(intent);
            }
        });
        mCollect.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), "您尚未登录", 100).show();
            }
            else {
                Intent intent = new Intent(getActivity(), FavouriteCheckActivity.class);
                startActivity(intent);
            }

        });
        mQuestionsCollection.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), "您尚未登录", 100).show();
            }
            else {
                Intent intent = new Intent(getActivity(), QuestionsCollectionActivity.class);
                startActivity(intent);
            }
        });
        mQuestion.setOnClickListener((View v) -> {
                Map<String, String> map = new HashMap<>();
                Intent mIntent = new Intent(getActivity(), ProblemActivity.class);
                JSONObject msg = null;
                try {
                    msg = RequestBuilder.sendBackendGetRequest("/api/problem/", map, true);
                } catch (Exception e){
                    e.printStackTrace();
                }
                JSONArray arr=msg.getJSONArray("data");
                for(int i = 0; i < arr.size(); i ++){
                    Map<String, JSONObject> map1 = (Map<String, JSONObject>) arr.get(i);
                    JSONObject problem = map1.get("problem");
                    String qBody = (String) problem.get("qBody");
                    String qAnswer = (String) problem.get("qAnswer");
                    String subject = ((Map<?, ?>) arr.get(i)).get("subject").toString();
                    mIntent.putExtra("body " + i, qBody);
                    mIntent.putExtra("answer " + i, qAnswer);
                    mIntent.putExtra("subject " + i, subject);
                }
                mIntent.putExtra("sum", arr.size() + "");
                mIntent.putExtra("type", "list");
                Log.v("mtag",arr.toString());
                Log.v("mtag", arr.size() + "");
                Log.v("mtag", arr.get(0).toString() + "");
                Log.v("mtag", arr.get(0).getClass().toString());
                startActivity(mIntent);
            });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
