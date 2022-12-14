package com.java.cuiyikai.activities;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.java.cuiyikai.utilities.ConstantUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This activity is used to display problems.</p>
 * <p>When the app needs to display problems, it will send information to {@link ProblemActivity}.
 * {@link ProblemActivity} will display a problem page according to the information.</p>
 */
public class ProblemActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(ProblemActivity.class);

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
                        logger.info("Answer: {} {}", j, answerList.get(i).charAt(j));
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
                    placeA = questionList.get(i).indexOf("A???");
                    placeB = questionList.get(i).indexOf("B???");
                    placeC = questionList.get(i).indexOf("C???");
                    placeD = questionList.get(i).indexOf("D???");
                    placeE = questionList.get(i).indexOf("E???");
                }

                if(placeA == -1)
                {
                    placeA = questionList.get(i).indexOf("A???");
                    placeB = questionList.get(i).indexOf("B???");
                    placeC = questionList.get(i).indexOf("C???");
                    placeD = questionList.get(i).indexOf("D???");
                    placeE = questionList.get(i).indexOf("E???");
                }

                if(placeA == -1) {
                    placeA = questionList.get(i).indexOf("A");
                    placeB = questionList.get(i).indexOf("B");
                    placeC = questionList.get(i).indexOf("C");
                    placeD = questionList.get(i).indexOf("D");
                    placeE = questionList.get(i).indexOf("E");
                }

                logger.info("Answer places: {} {} {} {}", placeA, placeB, placeC, placeD);

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
            placeA = questionList.get(rank).indexOf("A???");
        if(placeA == -1)
            placeA = questionList.get(rank).indexOf("A???");
        if(placeA == -1)
            placeA = questionList.get(rank).indexOf("A");
        problemDescription.setText(questionList.get(rank).substring(0, placeA));
        Log.v("rank", optionNum[rank] + "");
        for(int i = 0; i < optionNum[rank]; i ++){
            View view = setButton(optionList[rank].get(i), letter[i]);
            view.setOnClickListener((View v) -> {
                for (int j = 0; j < optionGroup.getChildCount(); j++) {
                    View child = optionGroup.getChildAt(j);
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
            });
            TextView tv = view.findViewById(R.id.tv_content);
            Log.v("rank", i + " " + tv.getText() + letter[i]);
            LinearLayout.LayoutParams paramsRb = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        problemDescription = findViewById(R.id.problem_description);
        explanationTv = findViewById(R.id.explanation);
        explanationTv.setVisibility(View.INVISIBLE);
        confirmButton = findViewById(R.id.confirm_answer);
        confirmButton.setOnClickListener(v -> {
            if(confirmButton.getText().equals("????????????")){
                int rec = -1;
                for(int i = 0; i < optionGroup.getChildCount(); i ++){
                    View child = optionGroup.getChildAt(i);
                    if(child.isSelected()){
                        rec = i;
                        break;
                    }
                }
                if(rec == -1){
                    Toast.makeText(getBaseContext(), "???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                String choice = letter[rec];
                String correctAnswer = letter[optionId[cur]];
                if(choice.equals(correctAnswer)){
                    explanationTv.setText("????????????????????????");
                    cnt ++;
                }
                else{
                    String text = "??????????????????????????????????????????"+choice+"??????????????????" + correctAnswer;
                    explanationTv.setText(text);
                    Map<String, String> argument = new HashMap<>();
                    argument.put("qBody",questionList.get(cur));
                    argument.put("qAnswer", answerList.get(cur));
                    String mSubject = null;
                    if(!(subjectList.get(cur).equals("nope") || subjectList.get(cur).equals("ignorant"))){
                        mSubject = subjectList.get(cur);
                    }
                    JSONObject obj = JSON.parseObject(JSON.toJSONString(argument));
                    JSONObject obj1 = new JSONObject();
                    obj1.put(ConstantUtilities.ARG_PROBLEM, obj);
                    if(mSubject != null)
                        obj1.put(ConstantUtilities.ARG_SUBJECT, mSubject);
                    Log.v("wrong", argument.toString());
                    if(mSubject != null){
                        try {
                            RequestBuilder.asyncSendBackendPostRequest("/api/problem/addNewSave", obj1, true);
                        } catch (BackendTokenExpiredException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setFinish(false);
                explanationTv.setVisibility(View.VISIBLE);
                confirmButton.setText("?????????");
                if(cur == sum - 1){
                    confirmButton.setText("????????????");
                }
            }
            else if(confirmButton.getText().equals("?????????")){
                setFinish(true);
                confirmButton.setText("????????????");
                explanationTv.setVisibility(View.INVISIBLE);
                setView(cur + 1);
                cur ++;
            }
            else if(confirmButton.getText().equals("????????????")){
                if(type.equals("list")){
                    Log.v("mType", "in");
                    //??????"Yes"??????
                    AlertDialog mAlertDialog = new AlertDialog.Builder(ProblemActivity.this)
                            .setTitle("")
                            .setMessage("??????" + sum + "????????????????????????" + cnt + "?????????????????????")
                            .setPositiveButton("??????", (dialogInterface, i) -> finish()).create();
                    mAlertDialog.show();
                }
                else
                    finish();
            }
        });
        optionGroup = findViewById(R.id.options);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        type = bundle.getString("type");
        Log.v("mType", type);
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
