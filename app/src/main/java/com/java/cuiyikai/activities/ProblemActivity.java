package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.OptionAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemActivity extends AppCompatActivity {

    List<String> []optionList;

//    ListView optionView;

    int []optionId;

    TextView problemDescription;

    private int type;

    private int sum;

    private int cur;

    private int[] optionNum;

    private String letter[] = {"A", "B", "C", "D","E"};

    private List<String> questionList;

    private List<String> answerList;

    private RadioGroup optionGroup;

    private TextView explanationTv;

    private Button confirmButton;

//    private List<String> nowList;

    public int getType(){return type;}

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

//                String dText = questionList.get(i).substring(placeD + 2);


                optionList[i] = Arrays.asList(aText, bText, cText, dText, eText);
            }
//            else
//            {
//                findViewById(R.id.problem_options).setVisibility(View.GONE);
//                EditText answerInput = (EditText) findViewById(R.id.answer_input);
//                Button submitAnswer = (Button) findViewById(R.id.submit_ans);
//                ImageView answerImage = (ImageView) findViewById(R.id.answer_image);
//                submitAnswer.setOnClickListener((View view) -> {
//                    if(answerInput.getText().toString().equals(answerList.get(0))) {
//                        answerImage.setImageResource(R.drawable.correct);
//                    }
//                    else
//                        answerImage.setImageResource(R.drawable.wrong);
//                });
//            }
        }
    }
    private void setView(int rank){
        optionGroup.removeAllViews();
        int placeA = questionList.get(rank).indexOf("A.");
        if(placeA == -1)
            placeA = questionList.get(rank).indexOf("A．");
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        cur = 0;
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
                        Toast.makeText(getBaseContext(), "您尚未作答", 100);
                        return;
                    }
                    String choice = letter[rec];
                    String correctAnswer = letter[optionId[cur]];
                    if(choice.equals(correctAnswer)){
                        explanationTv.setText("恭喜您，回答正确");
                    }
                    else{
                        explanationTv.setText("对不起，回答错误。您的答案是"+choice+"，正确答案是" + correctAnswer);
                    }
                    explanationTv.setVisibility(View.VISIBLE);
                    confirmButton.setText("下一题");
                    if(cur == sum - 1){
                        confirmButton.setText("完成测试");
                    }
                }
                else if(confirmButton.getText().equals("下一题")){

                    confirmButton.setText("确认答案");
                    explanationTv.setVisibility(View.INVISIBLE);
                    setView(cur + 1);
                    cur ++;
                }
                else if(confirmButton.getText().equals("完成测试")){
                    finish();
                }
            }
        });
        optionGroup = findViewById(R.id.options);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sum = Integer.parseInt(bundle.getString("sum"));
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        for(int i = 0; i < sum; i ++){
            String bodyKey = "body " + i;
            String answerKey = "answer " + i;
            String body = bundle.getString(bodyKey);
            String answer = bundle.getString(answerKey);
            if(!Character.isAlphabetic(answer.charAt(0)))
                continue;
            questionList.add(body);
            answerList.add(answer);
        }
        sum = questionList.size();
        optionId = new int[sum];
        optionList = new List[sum];
        optionNum = new int[sum];
        optionGroup.removeAllViews();
        initData();
//        Log.v("button",optionGroup.get)
        setView(0);
    }
}