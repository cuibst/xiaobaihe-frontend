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

    ListView []optionView;

    int []optionId;

    private int type;

    public List<String> questionList;

    private List<String> answerList;

    public int getType(){return type;}

    public void setView(int i){
        if(Character.isAlphabetic(answerList.get(i).charAt(0))) {
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

            TextView problemDescription = (TextView) findViewById(R.id.problem_description);

            problemDescription.setText(questionList.get(i).substring(0, placeA));

            String aText = questionList.get(i).substring(placeA + 2, placeB);
            String bText = questionList.get(i).substring(placeB + 2, placeC);
            String cText = questionList.get(i).substring(placeC + 2, placeD);
            String dText = questionList.get(i).substring(placeD + 2);

            optionList[i] = Arrays.asList(aText, bText, cText, dText);

            optionView[i] = (ListView) findViewById(R.id.problem_options);

            optionView[i].setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList[i], -1, -1));

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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String questionBody = bundle.getString("body");
        String questionAnswer = bundle.getString("answer");
        int sum = Integer.parseInt(bundle.getString("sum"));
        optionId = new int[sum];
        optionList = new List[sum];
        optionView = new ListView[sum];
        questionList = Arrays.asList(questionAnswer);
        answerList = Arrays.asList(questionAnswer);
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


        System.out.println(questionBody);
        System.out.println(questionAnswer);
        for(int i = 0; i < sum; i ++){

        }

    }

    public class OptionOnClickListener implements View.OnClickListener {

        final int id;

        public OptionOnClickListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            Log.v("mTag", "inClick");

            optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList, optionId, id));
            finish();
            return;
        }
    }
}