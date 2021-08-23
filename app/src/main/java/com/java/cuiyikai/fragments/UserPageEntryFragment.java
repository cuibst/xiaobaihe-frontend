package com.java.cuiyikai.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.network.RequestBuilder;

public class UserPageEntryFragment extends Fragment {


    public UserPageEntryFragment() {
        // Required empty public constructor
    }

    private TextView textView;

    private Button checkFavourite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_page_entry, container, false);

        textView = (TextView) view.findViewById(R.id.loginTextView);

        checkFavourite = (Button) view.findViewById(R.id.btnCheckFavourite);

        checkFavourite.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), FavouriteCheckActivity.class);
            startActivity(intent);
        });


        if(RequestBuilder.checkedLogin()) {
            checkFavourite.setVisibility(View.VISIBLE);

        } else {
            textView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(RequestBuilder.checkedLogin()) {
            checkFavourite.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            checkFavourite.setVisibility(View.GONE);
        }
    }
}