package com.java.cuiyikai.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemActivity extends Activity {

    List<String> []optionList;

    int []optionId;

    TextView problemDescription;

    private int sum;

    private int cnt;

    private int cur;

    private String type;

    private int[] optionNum;

    private final String[] letter = {"A", "B", "C", "D","E"};

    private List<String> questionList;

    private List<String> subjectList;

    private List<String> answerList;

    private RadioGroup optionGroup;

    private TextView explanationTv;

    private Button confirmButton;

    public void initData(){
        for(int i = 0; i < sum; i ++){
            if(Character.isAlphabetic(answerList.get(i).charAt(0))) {
                Log.v("getNum", i + "");
                for(int j=0;j<answerList.get(0).length();j++)
                    if(Character.isLowerCase(answerList.get(i).charAt(j)) || Character.isUpperCase(answerList.get(i).charAt(j))) {
                        System.out.printf("%d %c%n", j, answerList.get(i).charAt(j));
                        optionId[i] = Character.toUpperCase(answerList.get(i).charAt(j)) - 'A';
                        break;
                    }
                optionId[i] = answerList.get(i).charAt(0) - 'A';

                int placeA = questionList.get(i).indexOf("A.");
                int placeB = questionList.get(i).indexOf("B.");
                int placeC = questionList.get(i).indexOf("C.");
                int placeD = questionList.get(i).indexOf("D.");
                int placeE = questionList.get(i).indexOf("E.");

                if(placeA == -1)
                {
                    placeA = questionList.get(i).indexOf("A．");
                    placeB = questionList.get(i).indexOf("B．");
                    placeC = questionList.get(i).indexOf("C．");
                    placeD = questionList.get(i).indexOf("D．");
                    placeE = questionList.get(i).indexOf("E．");
                }

                if(placeA == -1)
                {
                    placeA = questionList.get(i).indexOf("A、");
                    placeB = questionList.get(i).indexOf("B、");
                    placeC = questionList.get(i).indexOf("C、");
                    placeD = questionList.get(i).indexOf("D、");
                    placeE = questionList.get(i).indexOf("E、");
                }

                System.out.printf("%d %d %d %d%n", placeA, placeB, placeC, placeD);
//                problemDescription.setText(questionList.get(i).substring(0, placeA));

                String aText = questionList.get(i).substring(placeA + 2, placeB);
                String bText = questionList.get(i).substring(placeB + 2, placeC);
                String cText;
                String dText;
                String eText;
                if(placeD == -1){
                    cText = questionList.get(i).substring(placeC + 2);
                    dText = null;
                    eText = null;
                    optionNum[i] = 3;
                }
                else if(placeE == -1){
                    cText = questionList.get(i).substring(placeC + 2, placeD);
                    dText = questionList.get(i).substring(placeD + 2);
                    eText = null;
                    optionNum[i] = 4;
                }
                else {
                    cText = questionList.get(i).substring(placeC + 2, placeD);
                    dText = questionList.get(i).substring(placeD + 2, placeE);
                    eText = questionList.get(i).substring(placeE + 2);
                    optionNum[i] = 5;
                }

                optionList[i] = Arrays.asList(aText, bText, cText, dText, eText);
            }
        }
    }
    private void setView(int rank){
        optionGroup.removeAllViews();
        int placeA = questionList.get(rank).indexOf("A.");
        if(placeA == -1)
            placeA = questionList.get(rank).indexOf("A．");
        if(placeA == -1)
            placeA = questionList.get(rank).indexOf("A、");
        problemDescription.setText(questionList.get(rank).substring(0, placeA));
        Log.v("rank", optionNum[rank] + "");
        for(int i = 0; i < optionNum[rank]; i ++){
            View view = setButton(optionList[rank].get(i), letter[i]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < optionGroup.getChildCount(); i++) {
                        View child = optionGroup.getChildAt(i);
                        TextView tvMetaNum = child.findViewById(R.id.tv_meta_num);
                        TextView tvContent = child.findViewById(R.id.tv_content);
                        child.setSelected(false);
                        tvMetaNum.setSelected(false);
                        tvContent.setSelected(false);
                    }

                    TextView tvMetaNum = view.findViewById(R.id.tv_meta_num);
                    TextView tvContent = view.findViewById(R.id.tv_content);
                    if (view.isSelected()) {
                        view.setSelected(false);
                        tvMetaNum.setSelected(false);
                        tvContent.setSelected(false);
                    } else {
                        view.setSelected(true);
                        tvMetaNum.setSelected(true);
                        tvContent.setSelected(true);
                    }
                }
            });
            TextView tv = view.findViewById(R.id.tv_content);
            Log.v("rank", i + " " + tv.getText() + letter[i]);
            LinearLayout.LayoutParams paramsRb = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            optionGroup.addView(view, paramsRb);
        }
    }
    private View setButton(String text, String option){
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.option_item_2, null);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvMetaNum = view.findViewById(R.id.tv_meta_num);
        tvContent.setText(text);
        tvMetaNum.setText(option);
        return view;
    }
    private void setFinish(boolean flag){
        for(int i = 0 ; i < optionGroup.getChildCount(); i ++){
            optionGroup.getChildAt(i).setEnabled(flag);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        cur = 0;
        cnt = 0;
        problemDescription = (TextView) findViewById(R.id.problem_description);
        explanationTv = findViewById(R.id.explanation);
        explanationTv.setVisibility(View.INVISIBLE);
        confirmButton = findViewById(R.id.confirm_answer);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if(confirmButton.getText().equals("确认答案")){
                    int rec = -1;
                    for(int i = 0; i < optionGroup.getChildCount(); i ++){
                        View child = optionGroup.getChildAt(i);
                        if(child.isSelected()){
                            rec = i;
                            break;
                        }
                    }
                    if(rec == -1){
                        Toast.makeText(getBaseContext(), "您尚未作答", 100).show();
                        return;
                    }
                    String choice = letter[rec];
                    String correctAnswer = letter[optionId[cur]];
                    if(choice.equals(correctAnswer)){
                        explanationTv.setText("恭喜您，回答正确");
                        cnt ++;
                    }
                    else{
                        explanationTv.setText("对不起，回答错误。您的答案是"+choice+"，正确答案是" + correctAnswer);
                        Map<String, String> argument = new HashMap<>();
                        argument.put("qBody",questionList.get(cur));
                        argument.put("qAnswer", answerList.get(cur));
                        String mSubject = null;
                        if(!(subjectList.get(cur).equals("nope") || subjectList.get(cur).equals("ignorant"))){
                            mSubject = subjectList.get(cur);
                        }
                        JSONObject obj = JSONObject.parseObject(JSON.toJSONString(argument));
                        JSONObject obj1 = new JSONObject();
                        obj1.put("problem", obj);
                        if(mSubject != null)
                            obj1.put("subject", mSubject);
                        Log.v("wrong", argument.toString());
                        try {
                            RequestBuilder.asyncSendBackendPostRequest("/api/problem/addNewSave", (JSONObject) obj1, true);
                        } catch (BackendTokenExpiredException e) {
                            e.printStackTrace();
                        }
                    }
                    setFinish(false);
                    explanationTv.setVisibility(View.VISIBLE);
                    confirmButton.setText("下一题");
                    if(cur == sum - 1){
                        confirmButton.setText("完成测试");
                    }
                }
                else if(confirmButton.getText().equals("下一题")){
                    setFinish(true);
                    confirmButton.setText("确认答案");
                    explanationTv.setVisibility(View.INVISIBLE);
//                    optionGroup.setEnabled(true);
                    setView(cur + 1);
                    cur ++;
                }
                else if(confirmButton.getText().equals("完成测试")){
                    if(type.equals("list")){
                        Log.v("mtype", "in");
                        AlertDialog mAlertDialog = new AlertDialog.Builder(ProblemActivity.this)
                                .setTitle("")
                                .setMessage("共有" + sum + "道题目，您做对了" + cnt + "道，请继续努力")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).create();
                        mAlertDialog.show();
                    }
                    else
                        finish();
                }
            }
        });
        optionGroup = findViewById(R.id.options);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        type = bundle.getString("type");
        Log.v("mtype", type);
        sum = Integer.parseInt(bundle.getString("sum"));
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        subjectList = new ArrayList<>();
        for(int i = 0; i < sum; i ++){
            String bodyKey = "body " + i;
            String answerKey = "answer " + i;
            String subjectKey = "subject " + i;
            String body = bundle.getString(bodyKey);
            String answer = bundle.getString(answerKey);
            String subject = bundle.getString(subjectKey);
            if(!Character.isAlphabetic(answer.charAt(0)))
                continue;
            questionList.add(body);
            answerList.add(answer);
            subjectList.add(subject);
        }
        sum = questionList.size();
        optionId = new int[sum];
        optionList = new List[sum];
        optionNum = new int[sum];
        optionGroup.removeAllViews();
        initData();
        setView(0);
    }
}
