package com.java.cuiyikai.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.java.cuiyikai.adapters.SelectAdapter;

import com.java.cuiyikai.R;

import java.util.List;

/**
 * This fragment is used for select the results of searching.
 */
public class SelectFragment extends Fragment {

    private final SelectAdapter selectAdapter;

    public SelectFragment(SelectAdapter a)
    {
        super();
        selectAdapter=a;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_select, null);
        RecyclerView recyclerViewForSelect = view.findViewById(R.id.recyclerviewForSelect);
        //To set the flowlayoutmanager.
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getActivity())
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver(position -> Gravity.CENTER)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .withLastRow(true)
                .build();
        recyclerViewForSelect.setLayoutManager(chipsLayoutManager);
        recyclerViewForSelect.setAdapter(selectAdapter);
        return view;
    }
    public void getSubjectType(List<String> v)
    {
        selectAdapter.getCheckSubject().clear();
        if(selectAdapter.getItemCount()!=0)
            selectAdapter.notifyItemRangeRemoved(0,selectAdapter.getItemCount());
        selectAdapter.setSubjectType(v);
    }
    public void getType(List<String> v)
    {
        selectAdapter.getCheckMarked().clear();
        selectAdapter.setType(v);
        selectAdapter.notifyDataSetChanged();
    }
    public List<String> returnCheckMarked()
    {
        return selectAdapter.getCheckMarked();
    }
    public List<String> returnCheckSubject()
    {
        return selectAdapter.getCheckSubject();
    }
}


