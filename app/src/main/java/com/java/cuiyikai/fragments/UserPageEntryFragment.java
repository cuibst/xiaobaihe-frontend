package com.java.cuiyikai.fragments;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.activities.LoginActivity;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.activities.QuestionsCollectionActivity;
import com.java.cuiyikai.activities.VisitHistoryActivity;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class UserPageEntryFragment extends Fragment {


    public UserPageEntryFragment() {
        // Required empty public constructor
    }

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
        LinearLayout mQuestion = view.findViewById(R.id.user_question);
        LinearLayout mCollect = view.findViewById(R.id.user_collect);
        LinearLayout mHistory = view.findViewById(R.id.user_history);
        mUserName = view.findViewById(R.id.user_name);
        mLogIn = view.findViewById(R.id.login_btn);
        LinearLayout mQuestionsCollection = view.findViewById(R.id.user_wrong_question);
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
        mHistory.setOnClickListener(v -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), ConstantUtilities.MESSAGE_NOT_LOGIN, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(getActivity(), VisitHistoryActivity.class);
            startActivity(intent);
        });
        mCollect.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), ConstantUtilities.MESSAGE_NOT_LOGIN, Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(getActivity(), FavouriteCheckActivity.class);
                startActivity(intent);
            }

        });
        mQuestionsCollection.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), ConstantUtilities.MESSAGE_NOT_LOGIN, Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(getActivity(), QuestionsCollectionActivity.class);
                startActivity(intent);
            }
        });
        mQuestion.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), ConstantUtilities.MESSAGE_NOT_LOGIN, Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> map = new HashMap<>();
            loadingDialog = new LoadingDialog(getActivity());
            loadingFlag = true;
            loadingDialog.setDimissListener(() -> loadingFlag = false);
            loadingDialog.setLoadingText("正在生成")
                    .setInterceptBack(false)
                    .setFailedText("加载失败")
                    .show();
            try {
                Handler handler = new MyHandler(getMainLooper());
                futureMsg = RequestBuilder.asyncSendBackendGetRequest("/api/problem/", map, handler, true);
            } catch (BackendTokenExpiredException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    private Future<JSONObject> futureMsg = null;

    private LoadingDialog loadingDialog;
    private boolean loadingFlag = false;

    private class MyHandler extends Handler {

        public MyHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg1) {
            if(!loadingFlag)
                return;
            if(msg1.what == 1) {
                JSONObject msg;
                try {
                    msg = futureMsg.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingDialog.loadFailed();
                    return;
                }
                if(msg == null || msg.getJSONArray(ConstantUtilities.ARG_DATA).isEmpty()) {
                    loadingDialog.loadFailed();
                    return;
                }
                loadingDialog.close();
                Intent mIntent = new Intent(getActivity(), ProblemActivity.class);
                JSONArray arr = msg.getJSONArray(ConstantUtilities.ARG_DATA);
                for(int i = 0; i < arr.size(); i ++){
                    Map<String, JSONObject> map1 = (Map<String, JSONObject>) arr.get(i);
                    JSONObject problem = map1.get(ConstantUtilities.ARG_PROBLEM);
                    String qBody = (String) problem.get("qBody");
                    String qAnswer = (String) problem.get("qAnswer");
                    for(int j=0;j<qAnswer.length();j++)
                        if(qAnswer.charAt(j) >= 'A' && qAnswer.charAt(j) <= 'E') {
                            qAnswer = qAnswer.substring(j, j+1);
                            break;
                        }
                    String subject = ((Map<?, ?>) arr.get(i)).get(ConstantUtilities.ARG_SUBJECT).toString();
                    mIntent.putExtra("body " + i, qBody);
                    mIntent.putExtra("answer " + i, qAnswer);
                    mIntent.putExtra("subject " + i, subject);
                }
                mIntent.putExtra("sum", arr.size() + "");
                mIntent.putExtra("type", "list");
                Log.v("mTag",arr.toString());
                Log.v("mTag", arr.size() + "");
                Log.v("mTag", arr.get(0).toString() + "");
                Log.v("mTag", arr.get(0).getClass().toString());
                startActivity(mIntent);
            }
        }

    }

}
