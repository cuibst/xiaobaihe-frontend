package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

import java.util.ArrayList;
import java.util.List;

public class SelectViewHolder extends RecyclerView.ViewHolder{

    private final RadioButton chooseBtn;

    public RadioButton getChooseBtn() {
        return chooseBtn;
    }

    private boolean flag=true;
    public SelectViewHolder(@NonNull View itemView, List<String> checkMarked, List<String> checkSubject) {
        super(itemView);
        chooseBtn =itemView.findViewById(R.id.ButtonForChoose);
        chooseBtn.setChecked(true);
        chooseBtn.setOnClickListener(view -> {
            String name=(String) chooseBtn.getText();
            boolean b = (name.equals("语文")) || (name.equals("数学")) || (name.equals("英语")) || (name.equals("物理")) || (name.equals("地理")) || (name.equals("政治")) || (name.equals("化学")) || (name.equals("生物")) || (name.equals("历史"));
            if(flag) {
                chooseBtn.setChecked(false);
                if(b) {
                    checkSubject.remove(name);
                }
                else
                if(checkMarked.contains(name))
                    checkMarked.remove(chooseBtn.getText());
            }
            else {
                chooseBtn.setChecked(true);
                if(b) {
                    if (!checkSubject.contains(name))
                        checkSubject.add(name);
                }
                else
                if(!checkMarked.contains(name))
                    checkMarked.add((String) chooseBtn.getText());
            }
            flag=!flag;
        });
    }
}