package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.SearchActivity;
import com.java.cuiyikai.activities.SearchViewActivity;
import com.java.cuiyikai.adapters.viewholders.SelectViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectViewHolder>
{
    private final List<String> checkMarked=new ArrayList<>();
    private final List<String> checkSubject=new ArrayList<>();
    private final Context mContext;
    private List<String> subjectType;
    private List<String> type;

    public List<String> getCheckSubject() {
        return checkSubject;
    }

    public List<String> getCheckMarked() {
        return checkMarked;
    }

    public void setSubjectType(List<String> subjectType) {
        this.subjectType = subjectType;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public SelectAdapter(Context context)
    {
        checkMarked.clear();
        checkSubject.clear();
        mContext=context;
    }
    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectViewHolder(LayoutInflater.from(mContext).inflate(R.layout.select_content,parent,false), checkMarked, checkSubject);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder holder, int position) {
        if(position<subjectType.size()) {
            holder.getChooseBtn().setChecked(true);
            holder.getChooseBtn().setText(SearchViewActivity.reverseCheckSubject(subjectType.get(position)));
            checkSubject.add(SearchViewActivity.reverseCheckSubject(subjectType.get(position)));
        }
        else {
            holder.getChooseBtn().setChecked(true);
            position-=subjectType.size();
            holder.getChooseBtn().setText(type.get(position));
            checkMarked.add(type.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(subjectType==null&&type==null)
            return 0;
        else if(subjectType==null)
            return type.size();
        else if (type==null)
            return subjectType.size();
        else
            return subjectType.size()+type.size();
    }
}
