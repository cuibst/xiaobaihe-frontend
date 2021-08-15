package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.OptionAdapter;

import java.util.Arrays;
import java.util.List;

public class ProblemActivity extends AppCompatActivity {

    List<String> optionList;

    ListView optionView;

    int optionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String questionBody = bundle.getString("body");
        String questionAnswer = bundle.getString("answer");

        optionId = questionAnswer.charAt(0) - 'A';

        int placeA = questionBody.indexOf("A.");
        int placeB = questionBody.indexOf("B.");
        int placeC = questionBody.indexOf("C.");
        int placeD = questionBody.indexOf("D.");

        TextView problemDescription = (TextView) findViewById(R.id.problem_description);

        problemDescription.setText(questionBody.substring(0, placeA));

        String aText = questionBody.substring(placeA + 2, placeB);
        String bText = questionBody.substring(placeB + 2, placeC);
        String cText = questionBody.substring(placeC + 2, placeD);
        String dText = questionBody.substring(placeD + 2);

        optionList = Arrays.asList(aText, bText, cText, dText);

        optionView = (ListView) findViewById(R.id.problem_options);

        optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList, -1, -1));
    }

    public class OptionOnClickListener implements View.OnClickListener {

        final int id;

        public OptionOnClickListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            optionView.setAdapter(new OptionAdapter(ProblemActivity.this, R.layout.option_item, optionList, optionId, id));
        }
    }
}