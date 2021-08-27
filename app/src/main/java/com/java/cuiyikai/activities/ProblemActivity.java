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

    private List<String> nowList;

    public int getType(){return type;}

    public void initData(){
        for(int i = 0; i < sum; i ++){
            if(!Character.isAlphabetic(answerList.get(i).charAt(0))){

            }
        }
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

                if(placeA == -1)
                {
                    placeA = questionList.get(i).indexOf("A．");
                    placeB = questionList.get(i).indexOf("B．");
                    placeC = questionList.get(i).indexOf("C．");
                    placeD = questionList.get(i).indexOf("D．");
                }

                System.out.printf("%d %d %d %d%n", placeA, placeB, placeC, placeD);
//                problemDescription.setText(questionList.get(i).substring(0, placeA));

                String aText = questionList.get(i).substring(placeA + 2, placeB);
                String bText = questionList.get(i).substring(placeB + 2, placeC);
                String cText = questionList.get(i).substring(placeC + 2, placeD);
                String dText = questionList.get(i).substring(placeD + 2);

                optionList[i] = Arrays.asList(aText, bText, cText, dText);

//                optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList[i], -1, -1));
//            optionView.getAdapter().

            }
            else
            {
                findViewById(R.id.problem_options).setVisibility(View.GONE);
                EditText answerInput = (EditText) findViewById(R.id.answer_input);
                Button submitAnswer = (Button) findViewById(R.id.submit_ans);
                ImageView answerImage = (ImageView) findViewById(R.id.answer_image);
                submitAnswer.setOnClickListener((View view) -> {
                    if(answerInput.getText().toString().equals(answerList.get(0))) {
                        answerImage.setImageResource(R.drawable.correct);
                    }
                    else
                        answerImage.setImageResource(R.drawable.wrong);
                });
            }
        }
    }
    private void setView(int rank){
        int placeA = questionList.get(rank).indexOf("A.");
        problemDescription.setText(questionList.get(rank).substring(0, placeA));
        Log.v("flag", optionList[rank].toString());
        optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList[rank], -1 ,-1));
        findViewById(R.id.fill_blank).setVisibility(View.GONE);
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
        initData();
//        if(optionList[1] == null){
//            Log.v("flag1", "null");
//            return;
//        }
//        Log.v("flag1", optionList[1].toString());
        setView(0);

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
        }
    }
}