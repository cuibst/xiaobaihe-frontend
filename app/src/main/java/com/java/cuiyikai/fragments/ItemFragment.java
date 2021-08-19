package com.java.cuiyikai.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.java.cuiyikai.R;

public class ItemFragment extends Fragment {

    private static final String TITLE = "tile";
    public ItemFragment() {
    }

    public static ItemFragment newInstance(String item) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        TextView tv = view.findViewById(R.id.fi_text);
        Bundle bundle = getArguments();
        tv.setText(bundle.getString(TITLE));
        return view;
    }
}