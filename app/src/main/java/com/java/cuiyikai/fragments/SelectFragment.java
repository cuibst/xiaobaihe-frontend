package com.java.cuiyikai.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.adapters.SelectAdapter;

import com.java.cuiyikai.R;

import java.util.Vector;

public class SelectFragment extends Fragment {
    public  SelectFragment(){super();}
    public  RecyclerView RecyclerViewForSelect;
    private SelectAdapter selectAdapter;
    @SuppressLint("ValidFragment")
    public SelectFragment(SelectAdapter a)
    {
        super();
        selectAdapter=a;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_select, null);
        RecyclerViewForSelect=view.findViewById(R.id.recyclerviewForSelect);
        RecyclerViewForSelect.setAdapter(selectAdapter);
        return view;
    }
    public void getSubjectType(Vector<String> v)
    {
        selectAdapter.checkSubject.clear();
        if(selectAdapter.getItemCount()!=0)
            selectAdapter.notifyItemRangeRemoved(0,selectAdapter.getItemCount());
        selectAdapter.subjectType=v;
    }
    public void getType(Vector<String> v)
    {
        selectAdapter.checkMarked.clear();
        System.out.println(v.toString());
        selectAdapter.type=v;
        selectAdapter.notifyDataSetChanged();
    }
    public Vector<String> returnCheckMarked()
    {
        System.out.println(selectAdapter.checkMarked.toString());
        return selectAdapter.checkMarked;
    }
    public Vector<String> returnCheckSubject()
    {
        return selectAdapter.checkSubject;
    }
}


