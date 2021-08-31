package com.java.cuiyikai.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.activities.VisitHistoryActivity;
import com.java.cuiyikai.network.RequestBuilder;

public class UserPageEntryFragment extends Fragment {


    public UserPageEntryFragment() {
        // Required empty public constructor
    }
    private RelativeLayout settings;
    private RelativeLayout logout;
    private RelativeLayout clearCache;
    private TextView tvCollect;
    private TextView tvHistory;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_page_entry, container, false);
        settings = view.findViewById(R.id.setting);
        logout = view.findViewById(R.id.logout);
        clearCache = view.findViewById(R.id.clear_cache);
        tvCollect = view.findViewById(R.id.tv_collect);
        tvHistory = view.findViewById(R.id.tv_history);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), VisitHistoryActivity.class);
                startActivity(intent);
            }
        });
        tvCollect.setOnClickListener((View v) -> {
            if(!RequestBuilder.checkedLogin()){
                Toast.makeText(getContext(), "您尚未登录", 100).show();
            }
            else {
                Intent intent = new Intent(getActivity(), FavouriteCheckActivity.class);
                startActivity(intent);
            }

        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
