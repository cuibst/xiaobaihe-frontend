package com.java.cuiyikai.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.SearchViewActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DialogFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(DialogFragment.class);
    private MyHandler handler;
    private final SimpleDateFormat setTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final boolean[] jud=new boolean[100000];
    private TextView subjectText;
    private String subject=ConstantUtilities.SUBJECT_CHINESE;
    private boolean flag=false;
    private List<Message> list;
    private MessagesList messagesList;
    private static final String USER_ID ="0";
    private Author author;
    private Author pc;
    private Message pcMessage;
    private Message originMessage;
    private static final String PATHNAME ="questions&answers.txt";
    private MessagesListAdapter<Message> dialogAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_dialog, null);
        MessageInput messageInput = view.findViewById(R.id.messageInputInDialog);
        messagesList= view.findViewById(R.id.messageListInDialog);
        Button clearButton = view.findViewById(R.id.clearbtn);
        ImageView searchButton = view.findViewById(R.id.searchImageView);
        searchButton.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),SearchViewActivity.class);
            startActivity(intent);
        });
        handler =new MyHandler(Looper.getMainLooper());
        clearButton.setOnClickListener(view1 -> {
        File oldFile =new  File(getActivity().getFilesDir(), PATHNAME);
        boolean deleteFlag=oldFile.delete();
        if(deleteFlag)
            logger.info("delete successfully");
        else
            logger.info("failed to delete ");
        dialogAdapter.clear();
        list.clear();
        originMessage = new Message("1", "请输入查询内容:)", pc);
        dialogAdapter.addToStart(originMessage,true);
        messagesList.setAdapter(dialogAdapter);
        });




        Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_subject_select, null);
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(getActivity(), 16f);
        params.bottomMargin = DensityUtilities.dp2px(getActivity(), 8f);
        contentView.setLayoutParams(params);
        buildDialog(getActivity(),bottomDialog,contentView);



        subjectText=view.findViewById(R.id.subjectText);
        subjectText.setText(reverseCheckSubject(subject));
        subjectText.setOnClickListener((View v) -> bottomDialog.show());

        ImageLoader imageLoader = (imageView, url, payload) -> {
        };
        if((list=historyList())==null)
            list=new ArrayList<>();
        logger.info("size: {}", list.size());
        pc= new Author("pc", "1");
        author= new Author("author", "0");
        dialogAdapter=new MessagesListAdapter<>(USER_ID,imageLoader);
        if(!flag) {
            pcMessage = new Message("1", "请输入查询内容:)", pc);
            dialogAdapter.addToStart(pcMessage, true);
            flag=true;
        }
        dialogAdapter.setDateHeadersFormatter(new DateFormat());
        dialogAdapter.setLoadMoreListener(new LoadMoreListener());
        messageInput.setInputListener(input -> {
            Message userSendMessage= new Message(USER_ID, input.toString(), author);
            list.add(userSendMessage);
            jud[list.size()-1]=true;
            dialogAdapter.addToStart(userSendMessage,true);
            storageInfo(input.toString(),"user");
            AskQuestions questions=new AskQuestions(input.toString());
            Thread thread=new Thread(questions);
            thread.start();
            return true;
        });
        messagesList.setAdapter(dialogAdapter);
        return view;
    }

    private void buildDialog(Context context,Dialog bottomDialog, View contentView)
    {
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = context.getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(context, 16f);
        params.bottomMargin = DensityUtilities.dp2px(context, 8f);
        contentView.setLayoutParams(params);
        Button chinese = contentView.findViewById(R.id.chinese);
        Button math = contentView.findViewById(R.id.math);
        Button english = contentView.findViewById(R.id.english);
        Button physics = contentView.findViewById(R.id.physics);
        Button chemistry = contentView.findViewById(R.id.chemistry);
        Button biology = contentView.findViewById(R.id.biology);
        Button geo = contentView.findViewById(R.id.geo);
        Button history = contentView.findViewById(R.id.history);
        Button politics = contentView.findViewById(R.id.politics);
        chinese.setOnClickListener(v -> {
            subject= ConstantUtilities.SUBJECT_CHINESE;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        math.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_MATH;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        english.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_ENGLISH;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        physics.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_PHYSICS;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        chemistry.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_CHEMISTRY;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        biology.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_BIOLOGY;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        geo.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_GEO;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        politics.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_POLITICS;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        history.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_HISTORY;
            subjectText.setText(SearchViewActivity.reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
    }
    public void fixQuestion(Map<String,Object> map)
    {
        Set<String> set=map.keySet();
        StringBuilder ans= new StringBuilder("为您找到以下相关学科的结果：\n");
        if(!set.isEmpty())
        {
            for(int cnt=0;cnt<set.size();cnt++)
            {
                JSONObject ansObject=(JSONObject) ((JSONArray) map.get(set.toArray()[cnt])).get(0);
                ans.append("\t答案： ").append(ansObject.get("value")).append("\n");
                SpannableString  span=new SpannableString("\t相关词条: "+ ansObject.get(ConstantUtilities.ARG_SUBJECT) +"\n");
                ans.append(span.toString());
            }
        }
        else
            ans = new StringBuilder("并没有找到相关结果:( ,试试换个学科再搜索吧( •̀ ω •́ )y");
        pcMessage = new Message("1", ans.toString(), pc);
        storageInfo(ans.toString(),"pc");
        list.add(pcMessage);
        jud[list.size()-1]=true;
        dialogAdapter.addToStart(pcMessage,true);
    }
    public void storageInfo(String s,String id)
    {
        File loadFile = new File(getActivity().getFilesDir(), PATHNAME);
        if(!loadFile.exists())
            try {
                loadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        try(FileWriter writer=new FileWriter(loadFile,true)) {
            writer.write(id+"\n");
            writer.write((new Date()).toString()+"\n");
            writer.write(s+";\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Message> historyList()
    {
        File loadFile = new File(getActivity().getFilesDir(), PATHNAME);
        List<Message> messageList=new ArrayList<>();
        Author user= new Author("author", "0");
        Author localPc= new Author("pc", "1");
        if(!loadFile.exists())
            return new ArrayList<>();
        else{
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)))){
                String id;
                String date;
                while((id=reader.readLine())!=null)
                {
                    date=reader.readLine();
                    String info;
                    StringBuilder ansBuilder = new StringBuilder();
                    while(((info=reader.readLine())!=null)) {
                        logger.info("info : {}", info);
                        if((info.charAt(info.length()-1)==';')) {
                            ansBuilder.append(info.substring(0, info.length() - 1)).append("\n");
                            break;
                        }
                        ansBuilder.append(info).append("\n");
                    }
                    String ans = ansBuilder.toString();
                    logger.info("ans: {}", ans);
                    Message msg;
                    if(id.equals("user"))
                        msg= new Message(id, ans, user, date);
                    else
                        msg= new Message(id, ans, localPc, date);
                    messageList.add(msg);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return messageList;
    }
    public class LoadMoreListener implements MessagesListAdapter.OnLoadMoreListener
    {

        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            logger.info("page {} ", page);
            logger.info("total {}", totalItemsCount);
            List<Message> msg=new ArrayList<>();
            if(list==null||list.isEmpty())
                return ;
            if(page>list.size())
                return ;
            logger.info("list.size() = {}", list.size());
            int count=0;
            for(;(list.size()-page)>=0;page++)
            {
                if(jud[list.size()-page])
                    continue;
                jud[list.size()-page]=true;
                logger.info("num {}", (list.size()-page));
                logger.info(list.get((list.size()-page)).text);
                msg.add(list.get((list.size()-page)));
                count++;
                if(count>=1)
                    break;
            }
            for(Message i:msg)
                logger.info("text {}", i.text);
            dialogAdapter.addToEnd(msg,false);
        }
    }
    public class AskQuestions implements  Runnable
    {
        AskQuestions(String s)
        {
            input=s;
        }
        private final String input;
        @Override
        public void run() {
            Map<String,Object> totalTree=new HashMap<>();
            try {
                Map<String, String> map = new HashMap<>();
                map.put(ConstantUtilities.ARG_COURSE, subject);
                map.put("inputQuestion", input);
                String getAnswerUrl = "typeOpen/open/inputQuestion";
                JSONObject msg = RequestBuilder.sendPostRequest(getAnswerUrl, map);
                if(msg==null||!(msg.get("code").equals("0")))
                {
                    handler.sendEmptyMessage(1);
                }
                else if(!((JSONArray)msg.get(ConstantUtilities.ARG_DATA)).isEmpty()&&!((JSONObject)((JSONArray)msg.get(ConstantUtilities.ARG_DATA)).get(0)).get("value").equals(""))
                    totalTree.put(subject,msg.get(ConstantUtilities.ARG_DATA));
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            android.os.Message message=new android.os.Message();
            message.what=0;
            message.obj=totalTree;
            handler.sendMessage(message);
        }
    }
    private class MyHandler extends Handler {
        MyHandler(Looper looper)
        {
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == 0) {
                Map<String, Object> object = (Map<String, Object>) msg.obj;
                fixQuestion(object);
            } else if (msg.what == 1) {
                Toast.makeText(getActivity(), "网络异常，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public static class Author implements IUser{
        private final String name;
        private final String id;
        Author(String name,String id)
        {
            this.name=name;
            this.id=id;
        }
        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAvatar() {
            return null;
        }
    }
    public static class Message implements IMessage
    {
        private final String id;
        private String text;
        private final Author author;
        private String date="";
        Message(String id,String text,Author a)
        {
            this.id=id;
            this.text=text;
            this.author=a;
        }
        Message(String id,String text,Author a,String d)
        {
            this.date=d;
            this.id=id;
            this.text=text;
            this.author=a;
        }
        public void setText(String t)
        {
            text=t;
        }
        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public IUser getUser() {
            return this.author;
        }

        @Override
        public Date getCreatedAt() {
            if(date.equals("")) {
                this.date=new Date().toString();
            }
            return new Date(this.date);
        }
    }
    public class DateFormat implements DateFormatter.Formatter
    {
        @Override
        public String format(Date date) {
            return setTimeFormat.format(date);
        }
    }

    public String reverseCheckSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case ConstantUtilities.SUBJECT_CHINESE:
                chooseSubject = "语文";
                break;
            case ConstantUtilities.SUBJECT_MATH:
                chooseSubject = "数学";
                break;
            case ConstantUtilities.SUBJECT_ENGLISH:
                chooseSubject = "英语";
                break;
            case ConstantUtilities.SUBJECT_PHYSICS:
                chooseSubject = "物理";
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY:
                chooseSubject = "化学";
                break;
            case ConstantUtilities.SUBJECT_HISTORY:
                chooseSubject = "历史";
                break;
            case ConstantUtilities.SUBJECT_GEO:
                chooseSubject = "地理";
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
                chooseSubject = "政治";
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY:
            default:
                chooseSubject = "生物";
                break;
        }
        return chooseSubject;
    }
}
