package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.SubjectAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private static final String DATA_FILE = "subject_data.json";
    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();
    private GridView userGv;
    private GridView otherGv;
    private SubjectAdapter userAdapter;
    private SubjectAdapter otherAdapter;
    private TextView mEditTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initView();
    }

    public static String getSubjectData(){
        return DATA_FILE;
    }

    private void initView() {
        mEditTextView = findViewById(R.id.edit_event);
        userGv = findViewById(R.id.user_gv);
        otherGv = findViewById(R.id.other_gv);
        try {
            InputStream is = getAssets().open(DATA_FILE);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf-8");
            Log.v("newtag", result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray userArray = jsonObject.optJSONArray("user");
            JSONArray otherArray = jsonObject.optJSONArray("other");
            for (int i = 0; i <= userArray.length() - 1; i ++)
                userList.add(userArray.optString(i));
            for(int i = 0; i <= otherArray.length() - 1; i ++)
                otherList.add(otherArray.optString(i));

        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }

        userAdapter = new SubjectAdapter(this, userList, 0);
        otherAdapter = new SubjectAdapter(this, otherList, 1);
        userGv.setAdapter(userAdapter);
        otherGv.setAdapter(otherAdapter);

        mEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectAdapter.setEdit(!SubjectAdapter.getEdit());

                mEditTextView.setText(SubjectAdapter.getEdit() ? "完成" : "编辑");
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
//                if(!SubjectAdapter.getEdit()){
//                    Log.v("err", "in");
//                    File f = new File(DATA_FILE);
//                    Map<String, List<String>> map = new HashMap<>();
//                    map.put("user", userList);
//                    map.put("other", otherList);
////                    Log.v("newtag", map);
//                    JSONObject json = new JSONObject(map);
//                    try (Writer write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8")) {
//                        write.write(json.toString());
//                        write.flush();
//                        write.close();
//                    } catch ( Exception e){
//                        e.printStackTrace();
//                    }
//                }
            }
        });

        userGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("mytag", i + " " + l + " " + userList.get(i));
                if(!SubjectAdapter.getEdit())
                    return;
                otherList.add(userList.get(i));
                userList.remove(i);
//                Log.v("mytag", userList.get(i));
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
            }
        });

        otherGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!SubjectAdapter.getEdit())
                    return;
                userList.add(otherList.get(i));
                otherList.remove(i);
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
            }
        });
    }
}