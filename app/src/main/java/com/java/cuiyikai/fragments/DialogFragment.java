package com.java.cuiyikai.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;


public class DialogFragment extends Fragment {
    SimpleDateFormat setTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean jud[]=new boolean[100000];
    private boolean flag=false;
    private int count=0;
    private List<Message> list;
    private final String all_subject_item[]={"chinese","math","english","physics","chemistry","biology","history","politics","geo"};
    private final String getAnswerUrl="typeOpen/open/inputQuestion";
    private MessagesList messagesList;
    private MessageInput messageInput;
    private String UserID="0";
    public  Author author;
    public  Author pc;
    private Message pcmessage;
    private Message orignmessage;
    private final String pathname="questions&answers.txt";
    private MessagesListAdapter<Message> dialogAdapter;
    private Button clearbtn;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_dialog, null);
        messageInput=(MessageInput)view.findViewById(R.id.messageInputInDialog);
        messagesList=(MessagesList) view.findViewById(R.id.messageListInDialog);
        clearbtn=view.findViewById(R.id.clearbtn);
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            File oldFile =new  File(getActivity().getFilesDir(),pathname);
            oldFile.delete();
            if(oldFile.exists())
                System.out.println("yes");
            else
                System.out.println("no");
            dialogAdapter.clear();
            list.clear();
            orignmessage=new Message("1","请输入查询内容:)",pc);
            dialogAdapter.addToStart(orignmessage,true);
            messagesList.setAdapter(dialogAdapter);
            }
        });



        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
            }
        };
        if((list=historyList())==null)
            list=new ArrayList<>();
        System.out.println("size: "+list.size());
        pc=new Author("pc","1");
        author=new Author("author","0");
        dialogAdapter=new MessagesListAdapter<>(UserID,imageLoader);
        if(!flag) {
            pcmessage = new Message("1", "请输入查询内容:)", pc);
            dialogAdapter.addToStart(pcmessage, true);
            flag=true;
        }
        dialogAdapter.setDateHeadersFormatter(new DateFormat());
        dialogAdapter.setLoadMoreListener(new LoadMoreListener());
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                Message userSendMessage=new Message(UserID,input.toString(),author);
                list.add(userSendMessage);
                jud[list.size()-1]=true;
                dialogAdapter.addToStart(userSendMessage,true);
                storageInfo(input.toString(),"user");
                askQuestions questions=new askQuestions(input.toString());
                Thread thread=new Thread(questions);
                thread.start();
                return true;
            }
        });
        messagesList.setAdapter(dialogAdapter);
        return view;
    }
    public void fixQuestion(Map<String,Object> map)
    {
        Set<String> set=map.keySet();
        String ans="为您找到以下相关学科的结果：\n";
        if(!set.isEmpty())
        {
            for(int cnt=0;cnt<set.size();cnt++)
            {
                JSONObject ansObject=(JSONObject) ((JSONArray) map.get(set.toArray()[cnt])).get(0);
                ans+=cnt+"."+((MainActivity)getActivity()).reverseCheckSubject((String) set.toArray()[cnt])+":\n";
                ans+="\t答案： "+ansObject.get("value")+"\n";
                SpannableString  span=new SpannableString("\t相关词条: "+(String)ansObject.get("subject")+"\n");
                ans+=span.toString();
            }
        }
        else
            ans="并没有找到相关结果:(";
        pcmessage = new Message("1", ans, pc);
        storageInfo(ans,"pc");
        list.add(pcmessage);
        jud[list.size()-1]=true;
        dialogAdapter.addToStart(pcmessage,true);
    }
    public void storageInfo(String s,String id)
    {
        File loadFile = new File(getActivity().getFilesDir(), pathname);
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
        File loadFile = new File(getActivity().getFilesDir(), pathname);
        List<Message> list=new ArrayList<>();
        Author user=new Author("author","0");
        Author pc=new Author("pc","1");
        if(!loadFile.exists())
            return null;
        else{
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
                String id;
                String date;
                while((id=reader.readLine())!=null)
                {
                    date=reader.readLine();
                    String info;
                    String ans="";
                    while(((info=reader.readLine())!=null)) {
                        System.out.println("info : "+info);
                        if((info.charAt(info.length()-1)==';')) {
                            ans += info.substring(0,info.length()-1)+"\n";
                            break;
                        }
                        ans += info+"\n";
                    }
                    System.out.println("ans: "+ans);
                    Message msg;
                    if(id.equals("user"))
                        msg=new Message(id,ans,user,date);
                    else
                        msg=new Message(id,ans,pc,date);
                    list.add(msg);
                }
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }
    public class LoadMoreListener implements MessagesListAdapter.OnLoadMoreListener
    {

        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            System.out.println("page "+page);
            System.out.println("total "+totalItemsCount);
            List<Message> msg=new ArrayList<>();
            if(list==null||list.isEmpty())
                return ;
            if(page>list.size())
                return ;
            System.out.println(list.size());
            int count=0;
            for(;(list.size()-page)>=0;page++)
            {
                if(jud[list.size()-page])
                    continue;
                jud[list.size()-page]=true;
                System.out.println("num "+(list.size()-page));
                System.out.println(list.get((list.size()-page)).text);
                msg.add(list.get((list.size()-page)));
                count++;
                if(count>=4)
                    break;
            }
            for(Message i:msg)
                System.out.println("text "+i.text);
            dialogAdapter.addToEnd(msg,false);
        }
    }
    MyHandler handler =new MyHandler();
    public class askQuestions implements  Runnable
    {
        askQuestions(String s)
        {
            input=s;
        }
        private String input;
        @Override
        public void run() {
            Map<String,Object> totalTree=new HashMap<>();
            for (String str:all_subject_item)
            {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("course", str);
                    map.put("inputQuestion", input);
                    JSONObject msg = RequestBuilder.sendPostRequest(getAnswerUrl, map);
                    if(msg==null||!(msg.get("code").equals("0")))
                    {
                        handler.sendEmptyMessage(1);
                        break;
                    }
                    else if(!((JSONArray)msg.get("data")).isEmpty()&&!((JSONObject)((JSONArray)msg.get("data")).get(0)).get("value").equals(""))
                        totalTree.put(str,msg.get("data"));
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            android.os.Message message=new android.os.Message();
            message.what=0;
            message.obj=totalTree;
            handler.sendMessage(message);
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch(msg.what)
            {
                case 0:
                    Map<String, Object> object =(Map<String,Object>) msg.obj;
                    fixQuestion(object);
                    break;
                case 1:
                    Toast.makeText(getActivity(), "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }



    public class Author implements IUser{
        private String name,id;
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
    public class Message implements IMessage
    {
        Message(){}
        private String id="msgid";
        private String text;
        private Author author;
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
                return new Date(this.date);
            }
            else
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
}
