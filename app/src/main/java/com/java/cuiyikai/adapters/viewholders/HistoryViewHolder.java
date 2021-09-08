package com.java.cuiyikai.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.SearchViewActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This viewholder is designed for {@link com.java.cuiyikai.adapters.HistoryListAdapter}
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder {

    private static final Logger logger = LoggerFactory.getLogger(HistoryViewHolder.class);

    private final TextView historyRecord;
    private String subject;
    private Context mContext;
    private boolean recommendFlag =false;
    private SearchView searchView;
    private final ImageView editImg;

    public TextView getHistoryRecord() {
        return historyRecord;
    }

    public ImageView getEditImg() {
        return editImg;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRecommendFlag(boolean recommendFlag) {
        this.recommendFlag = recommendFlag;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    public HistoryViewHolder(View view) {
        super(view);
        editImg=view.findViewById(R.id.iv_edit);
        historyRecord=view.findViewById(R.id.historyrecord);
        historyRecord.setLongClickable(true);
        editImg.setVisibility(View.INVISIBLE);
        //if the item is clicked , you need to change the query text and subject.
        historyRecord.setOnClickListener(v -> {
            ((SearchViewActivity)mContext).setSubject(subject);
            ((SearchViewActivity)mContext).getSubjectText().setText(((SearchViewActivity)mContext).reverseCheckSubject(subject));
            searchView.setQuery(historyRecord.getText(),true);
        });

        //When you click the item and keep a longer time , it means that you want to delete the history.
        historyRecord.setOnLongClickListener(v -> {
            //recommend item cannot be deleted.
            if(recommendFlag)
                return false;
            editImg.setVisibility(View.VISIBLE);
            return true;
        });
        editImg.setOnClickListener(v -> {
            historyRecord.setVisibility(View.INVISIBLE);
            editImg.setVisibility(View.INVISIBLE);
            ClearOne clearOne=new ClearOne(historyRecord.getText().toString());
            logger.info("clear: {}", historyRecord.getText());
            Thread thread=new Thread(clearOne);
            thread.start();
        });
    }
}
//used for clear one history.
class ClearOne implements Runnable
{
    private final String s;
    ClearOne(String ss)
    {
        s=ss;
    }
    @Override
    public void run() {
        try{
            Map<String,String> map=new HashMap<>();
            map.put(ConstantUtilities.ARG_CONTENT,s);
            map.put("all","false");
            String removeUrl = "/api/history/removeHistory";
            RequestBuilder.sendBackendGetRequest(removeUrl,map,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
