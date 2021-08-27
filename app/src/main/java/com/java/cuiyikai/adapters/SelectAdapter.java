package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.SearchActivity;

import java.util.Vector;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.SelectViewHolder>
{
    public Vector<String> checkMarked=new Vector<>();
    public Vector<String> checkSubject=new Vector<>();
    private Context mContext;
    public Vector<String> subjectType;
    public Vector<String> type;
    public SelectAdapter(Context context)
    {
        checkMarked.clear();
        checkSubject.clear();
        mContext=context;
    }
    @NonNull
    @Override
    public SelectAdapter.SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectAdapter.SelectViewHolder(LayoutInflater.from(mContext).inflate(R.layout.select_content,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAdapter.SelectViewHolder holder, int position) {
        if(position<subjectType.size()) {
            holder.ChooseBtn.setChecked(true);
            holder.ChooseBtn.setText(((SearchActivity) mContext).reverseCheckSubject(subjectType.get(position)));
            checkSubject.add(((SearchActivity) mContext).reverseCheckSubject(subjectType.get(position)));
        }
        else {
            holder.ChooseBtn.setChecked(true);
            position-=subjectType.size();
            holder.ChooseBtn.setText(type.get(position));
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
    class SelectViewHolder extends RecyclerView.ViewHolder{
        private RadioButton ChooseBtn;
        private boolean flag=true;
        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            ChooseBtn=itemView.findViewById(R.id.ButtonForChoose);
            ChooseBtn.setChecked(true);
            ChooseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(flag) {
                        String name=(String) ChooseBtn.getText();
                        ChooseBtn.setChecked(false);
                        if((name=="语文")||(name=="数学")||(name=="英语")||(name=="物理")||(name=="地理")||(name=="政治")||(name=="化学")||(name=="生物")||(name=="历史")) {
                            if (checkSubject.contains(name))
                                checkSubject.remove(name);
                        }
                        else
                            if(checkMarked.contains(name))
                                checkMarked.remove((String)ChooseBtn.getText());
                        flag=!flag;
                    }
                    else if(!flag){
                        String name=(String) ChooseBtn.getText();
                        ChooseBtn.setChecked(true);
                        if((name=="语文")||(name=="数学")||(name=="英语")||(name=="物理")||(name=="地理")||(name=="政治")||(name=="化学")||(name=="生物")||(name=="历史")) {
                            if (!checkSubject.contains(name))
                                checkSubject.add(name);
                        }
                        else
                            if(!checkMarked.contains(name))
                                checkMarked.add((String)ChooseBtn.getText());
                        flag=!flag;
                    }
                }
            });
        }
    }
}
