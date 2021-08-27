package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.OptionAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemActivity extends AppCompatActivity {

    List<String> []optionList;

    ListView optionView;

    int []optionId;

    TextView problemDescription;

    private int type;

    private int sum;

    private int cur;

    private List<String> questionList;

    private List<String> answerList;

    public int getType(){return type;}


    public void initView(){
        if(Character.isAlphabetic(answerList.get(0).charAt(0))) {

            for(int j=0;j<answerList.get(0).length();j++)
                if(Character.isLowerCase(answerList.get(0).charAt(j)) || Character.isUpperCase(answerList.get(0).charAt(j))) {
                    System.out.printf("%d %c%n", i, answerList.get(0).charAt(j));
                    optionId[i] = Character.toUpperCase(answerList.get(0).charAt(j)) - 'A';
                    break;
                }
            optionId[i] = answerList.get(i).charAt(0) - 'A';

            int placeA = questionList.get(i).indexOf("A.");
            int placeB = questionList.get(i).indexOf("B.");
            int placeC = questionList.get(i).indexOf("C.");
            int placeD = questionList.get(i).indexOf("D.");

            if(placeA == -1)
            {
                placeA = questionList.get(i).indexOf("A．");
                placeB = questionList.get(i).indexOf("B．");
                placeC = questionList.get(i).indexOf("C．");
                placeD = questionList.get(i).indexOf("D．");
            }

            System.out.printf("%d %d %d %d%n", placeA, placeB, placeC, placeD);

            String tmp = questionList.get(i).substring(0, placeA);
            Log.v("mNew", tmp);
            if(problemDescription != null)
                problemDescription.setText(questionList.get(i).substring(0, placeA));
            else
                Log.v("HHHH", "az");

            String aText = questionList.get(i).substring(placeA + 2, placeB);
            String bText = questionList.get(i).substring(placeB + 2, placeC);
            String cText = questionList.get(i).substring(placeC + 2, placeD);
            String dText = questionList.get(i).substring(placeD + 2);

            optionList[i] = Arrays.asList(aText, bText, cText, dText);

            optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList[i], -1, -1));
//            optionView.getAdapter().
            findViewById(R.id.fill_blank).setVisibility(View.GONE);
        }
        else
        {
            findViewById(R.id.problem_options).setVisibility(View.GONE);
            EditText answerInput = (EditText) findViewById(R.id.answer_input);
            Button submitAnswer = (Button) findViewById(R.id.submit_ans);
            ImageView answerImage = (ImageView) findViewById(R.id.answer_image);
            submitAnswer.setOnClickListener((View view) -> {
                if(answerInput.getText().toString().equals(answerList.get(i))) {
                    answerImage.setImageResource(R.drawable.correct);
                }
                else
                    answerImage.setImageResource(R.drawable.wrong);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        cur = 0;
        problemDescription = (TextView) findViewById(R.id.problem_description);
        if(problemDescription == null)
            Log.v("HHHH", "b");
        optionView = (ListView) findViewById(R.id.problem_options);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

//        String questionBody = bundle.getString("body");
////        Log.v("mtag", questionBody.toString());
//        String questionAnswer = bundle.getString("answer");
        sum = Integer.parseInt(bundle.getString("sum"));
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        for(int i = 0; i < sum; i ++){
            String bodyKey = "body " + i;
            String answerKey = "answer " + i;
            questionList.add(bundle.getString(bodyKey));
            answerList.add(bundle.getString(answerKey));
        }
        optionId = new int[sum];
        optionList = new List[sum];
//        questionBody = questionBody.substring(1, questionBody.length() - 1);
//        questionAnswer = questionAnswer.substring(1, questionAnswer.length() - 1);
//        questionList = Arrays.asList(questionBody.split(","));
//        Log.v("mTag", questionBody.toString());
//        questionList = Arrays.asList(questionBody);
//        answerList = Arrays.asList(questionAnswer);
//        for(int i = 0; i < questionList.size(); i ++)
//            questionList.set(i, questionList.get(i).substring(1, questionList.get(0).length() - 1));
//        Log.v("mtag", answerList.get(0).getClass().toString());
//        Log.v("mtag", questionAnswer.toString());
//        Log.v("mtag", answerList.get(0).substring(1, questionList.get(0).length() - 1));

//        String mType = bundle.getString("list");
//        if(mType == null){
//            Log.v("mTag", "what");
//        }
//        Log.v("mTag", mType + "LNO");
//        if(mType == null || mType.equals("single")){
//            type = 0;
//        }
//        if(mType.equals("list")){
//            type = 1;
//        }
//        Log.v("bbzl",questionList.get(0));
//        problem
//        System.out.println(questionBody);
//        System.out.println(questionAnswer);
        initView();
//        Log.v("bbzl", problemDescription.getText().toString());

    }

    public class OptionOnClickListener implements View.OnClickListener {

        final int id;

        public OptionOnClickListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            Log.v("mTag", "inClick");

            optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList[cur], optionId[cur], id));
            if(cur == sum - 1)
                finish();
            else {
                cur ++;
                setView(cur);
            }
//            finish();
//            return;
        }
    }
}