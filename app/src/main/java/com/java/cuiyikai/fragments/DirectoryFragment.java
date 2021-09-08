package com.java.cuiyikai.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.activities.MindMapActivity;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.adapters.BottomFavouriteAdapter;
import com.java.cuiyikai.adapters.FavouriteAdapter;
import com.java.cuiyikai.entities.BottomFavouriteEntity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.widgets.ListViewForScrollView;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryFragment extends Fragment {

    private static final String ARG_DIRECTORY_NAME = ConstantUtilities.ARG_DIRECTORY_NAME;

    private static final Logger logger = LoggerFactory.getLogger(DirectoryFragment.class);

    private String directoryName;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param directoryName Parameter 1.
     * @return A new instance of fragment DirectoryFragment.
     */
    public static DirectoryFragment newInstance(String directoryName) {
        DirectoryFragment fragment = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIRECTORY_NAME, directoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            directoryName = getArguments().getString(ARG_DIRECTORY_NAME);
        }
    }

    private void updateDirectoryBackend(JSONArray jsonArray) {
        JSONObject args = new JSONObject();
        args.put(ConstantUtilities.ARG_DIRECTORY, directoryName);
        args.put("json", jsonArray);
        try {
            RequestBuilder.asyncSendBackendPostRequest("/api/favourite/updateDirectory", args, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View view;
    private FavouriteAdapter favouriteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_directory, container, false);

        if(directoryName.equals("default"))
            view.findViewById(R.id.btnDeleteDirectory).setVisibility(View.GONE);

        SwipeRecyclerView favouriteItemList = view.findViewById(R.id.directoryNameList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favouriteItemList.setLayoutManager(layoutManager);

        SwipeMenuCreator swipeMenuCreator = (SwipeMenu leftMenu, SwipeMenu rightMenu, int position) -> {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = DensityUtilities.dp2px(getActivity(), 70);
            SwipeMenuItem moveItem = new SwipeMenuItem(getActivity()).setBackground(R.color.xui_config_color_light_blue)
                    .setText("移动")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(moveItem);
            SwipeMenuItem copyItem = new SwipeMenuItem(getActivity()).setBackground(R.color.xui_btn_green_normal_color)
                    .setText("复制")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(copyItem);
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackground(R.color.bbl_ff0000)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(deleteItem);
        };

        favouriteAdapter = new FavouriteAdapter(DirectoryFragment.this, ((MainApplication)getActivity().getApplication()).getFavourite().getJSONArray(directoryName));

        favouriteItemList.setSwipeMenuCreator(swipeMenuCreator);

        Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_favourite, null);
        contentView.findViewById(R.id.bottomAddNewFavourite).setVisibility(View.GONE);
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(getActivity(), 16f);
        params.bottomMargin = DensityUtilities.dp2px(getActivity(), 8f);
        contentView.setLayoutParams(params);

        ListViewForScrollView bottomFavouriteView = contentView.findViewById(R.id.bottomFavouriteListView);
        JSONObject favouriteJson = ((MainApplication)getActivity().getApplication()).getFavourite();
        List<BottomFavouriteEntity> entityList = new ArrayList<>();
        for(String key : favouriteJson.keySet()) {
            if(!key.equals(directoryName))
                entityList.add(new BottomFavouriteEntity(false, key));
        }

        BottomFavouriteAdapter adapter = new BottomFavouriteAdapter(getActivity(), R.layout.bottom_dialog_favourite_item, entityList);

        bottomFavouriteView.setAdapter(adapter);

        favouriteItemList.setOnItemMenuClickListener((SwipeMenuBridge menuBridge, int position) -> {
            menuBridge.closeMenu();
            logger.info("Menu {} clicked", menuBridge.getPosition());
            int menuPosition = menuBridge.getPosition();
            JSONArray moveArray = new JSONArray();
            moveArray.add(favouriteAdapter.getFavouriteArray().get(position));
            if(menuPosition == 2) {
                favouriteAdapter.getFavouriteArray().remove(position);
                favouriteAdapter.notifyItemRemoved(position);
                updateDirectoryBackend(favouriteAdapter.getFavouriteArray());
            } else if(menuPosition == 1) {
                bottomDialog.show();
                Button btnBottomFinish = contentView.findViewById(R.id.buttonBottomFinish);
                btnBottomFinish.setOnClickListener((View vi) -> {
                    Set<String> checkedSet = adapter.getCheckedSet();
                    for(String targetName : checkedSet) {
                        JSONObject args = new JSONObject();
                        args.put(ConstantUtilities.ARG_DIRECTORY, targetName);
                        args.put("json", moveArray);
                        try {
                            RequestBuilder.sendBackendPostRequest("/api/favourite/moveDirectory", args, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ((MainApplication)getActivity().getApplication()).updateFavourite();
                    bottomDialog.dismiss();
                });
            } else if(menuPosition == 0) {
                bottomDialog.show();
                Button btnBottomFinish = contentView.findViewById(R.id.buttonBottomFinish);
                btnBottomFinish.setOnClickListener((View vi) -> {
                    Set<String> checkedSet = adapter.getCheckedSet();
                    for(String targetName : checkedSet) {
                        JSONObject args = new JSONObject();
                        args.put(ConstantUtilities.ARG_DIRECTORY, targetName);
                        args.put("json", moveArray);
                        try {
                            RequestBuilder.sendBackendPostRequest("/api/favourite/moveDirectory", args, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    favouriteAdapter.getFavouriteArray().remove(position);
                    favouriteAdapter.notifyItemRemoved(position);
                    updateDirectoryBackend(favouriteAdapter.getFavouriteArray());
                    bottomDialog.dismiss();
                });
            }
        });

        favouriteItemList.setLongPressDragEnabled(true);

        favouriteItemList.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(favouriteAdapter.getFavouriteArray(), fromPosition, toPosition);
                boolean flag1 = favouriteAdapter.getSelected()[fromPosition];
                boolean flag2 = favouriteAdapter.getSelected()[toPosition];
                favouriteAdapter.getSelected()[fromPosition] = flag2;
                favouriteAdapter.getSelected()[toPosition] = flag1;
                favouriteAdapter.notifyItemMoved(fromPosition, toPosition);
                updateDirectoryBackend(favouriteAdapter.getFavouriteArray());
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                throw new UnsupportedOperationException("No swipe dismiss!!");
            }
        });

        favouriteItemList.setOnItemLongClickListener((View v, int adapterPosition) -> {
            favouriteAdapter.setEditable(true);
            favouriteAdapter.getSelected()[adapterPosition] = true;
            favouriteAdapter.notifyDataSetChanged();
            view.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
        });

        favouriteItemList.setOnItemClickListener((View v, int adapterPosition) -> {
            if(favouriteAdapter.isEditable()) {
                favouriteAdapter.getSelected()[adapterPosition] = !favouriteAdapter.getSelected()[adapterPosition];
                favouriteAdapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.btnSelectAll).setOnClickListener((View v) -> {
            favouriteAdapter.selectAll();
            favouriteAdapter.notifyDataSetChanged();
        });

        view.findViewById(R.id.btnMoveFavourite).setOnClickListener((View v) -> {
            JSONArray newArray = new JSONArray();
            JSONArray moveArray = new JSONArray();
            for(int i=0;i<favouriteAdapter.getSelected().length;i++)
                if(!favouriteAdapter.getSelected()[i])
                    newArray.add(favouriteAdapter.getFavouriteArray().get(i));
                else
                    moveArray.add(favouriteAdapter.getFavouriteArray().get(i));

            Button btnBottomFinish = contentView.findViewById(R.id.buttonBottomFinish);
            btnBottomFinish.setOnClickListener((View vi) -> {
                Set<String> checkedSet = adapter.getCheckedSet();
                for(String targetName : checkedSet) {
                    JSONObject args = new JSONObject();
                    args.put(ConstantUtilities.ARG_DIRECTORY, targetName);
                    args.put("json", moveArray);
                    try {
                        RequestBuilder.sendBackendPostRequest("/api/favourite/moveDirectory", args, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                favouriteAdapter.setFavouriteArray(newArray);
                updateDirectoryBackend(newArray);
                favouriteAdapter.notifyDataSetChanged();
                bottomDialog.dismiss();
            });

            bottomDialog.show();
        });
        view.findViewById(R.id.btnGenerateProblems).setOnClickListener((View v) ->{
            JSONObject js = ((MainApplication) getActivity().getApplication()).getFavourite();
            int cnt = 0;
            JSONArray jsArray = (JSONArray) js.get(directoryName);
            if(jsArray == null) {
                Toast.makeText(getActivity(), "空文件夹", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> qBodyList = new ArrayList<>();
            List<String> qAnswerList = new ArrayList<>();
            List<String> subjectList = new ArrayList<>();

            LoadingDialog loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.setLoadingText("正在生成")
                    .setInterceptBack(false)
                    .setFailedText("加载失败")
                    .show();

            loadingFlag = true;

            loadingDialog.setDimissListener(() -> loadingFlag = false);

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(!loadingFlag)
                        return;
                    if(msg.what == 1) {
                        if(qBodyList.isEmpty()) {
                            loadingDialog.loadFailed();
                            Toast.makeText(getActivity(), "没有相关题目", Toast.LENGTH_LONG).show();
                            return;
                        }
                        loadingDialog.close();
                        Log.v("answer", qAnswerList.toString());
                        Intent mIntent = new Intent(getActivity(), ProblemActivity.class);
                        for(int j = 0; j < qBodyList.size(); j ++){
                            mIntent.putExtra("body" + " " + j, qBodyList.get(j));
                            mIntent.putExtra("answer" + " " + j, qAnswerList.get(j));
                            mIntent.putExtra(ConstantUtilities.ARG_SUBJECT + " " + j, subjectList.get(j));
                        }
                        mIntent.putExtra("type", "list");
                        mIntent.putExtra("sum", qBodyList.size() + "");
                        startActivity(mIntent);
                    } else if(msg.what == 2) {
                        loadingFlag = false;
                        loadingDialog.loadFailed();
                    }
                }
            };

            Thread thread = new Thread(new GenerateProblemThread(handler, qAnswerList, qBodyList, subjectList, jsArray));
            thread.start();

        });


        view.findViewById(R.id.btnCopyFavourite).setOnClickListener((View v) -> {
            JSONArray moveArray = new JSONArray();
            for(int i=0;i<favouriteAdapter.getSelected().length;i++)
                moveArray.add(favouriteAdapter.getFavouriteArray().get(i));

            Button btnBottomFinish = contentView.findViewById(R.id.buttonBottomFinish);
            btnBottomFinish.setOnClickListener((View vi) -> {
                Set<String> checkedSet = adapter.getCheckedSet();
                for(String targetName : checkedSet) {
                    JSONObject args = new JSONObject();
                    args.put(ConstantUtilities.ARG_DIRECTORY, targetName);
                    args.put("json", moveArray);
                    try {
                        RequestBuilder.sendBackendPostRequest("/api/favourite/moveDirectory", args, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                bottomDialog.dismiss();
            });

            bottomDialog.show();
        });


        view.findViewById(R.id.btnDeleteItems).setOnClickListener((View v) -> {
            JSONArray newArray = new JSONArray();
            for(int i=0;i<favouriteAdapter.getSelected().length;i++)
                if(!favouriteAdapter.getSelected()[i])
                    newArray.add(favouriteAdapter.getFavouriteArray().get(i));
            favouriteAdapter.setFavouriteArray(newArray);
            updateDirectoryBackend(newArray);
            favouriteAdapter.notifyDataSetChanged();
        });

        view.findViewById(R.id.btnFinish).setOnClickListener((View v) -> {
            favouriteAdapter.setEditable(false);
            favouriteAdapter.notifyDataSetChanged();
            view.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        });

        view.findViewById(R.id.btnDeleteDirectory).setOnClickListener((View v) -> {
            JSONObject args = new JSONObject();
            args.put(ConstantUtilities.ARG_DIRECTORY, directoryName);
            try {
                RequestBuilder.asyncSendBackendPostRequest("/api/favourite/removeDirectory", args, true);
                ((MainApplication) getActivity().getApplication()).updateFavourite();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((FavouriteCheckActivity)getActivity()).updateDirectories();

        });

        view.findViewById(R.id.btnGenerateMap).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MindMapActivity.class);
            intent.putExtra(ConstantUtilities.ARG_DIRECTORY_NAME, directoryName);
            startActivity(intent);
        });

        favouriteItemList.setAdapter(favouriteAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainApplication)getActivity().getApplication()).updateFavourite();
        favouriteAdapter.setFavouriteArray(((MainApplication)getActivity().getApplication()).getFavourite().getJSONArray(directoryName));
        favouriteAdapter.notifyDataSetChanged();
    }

    private boolean loadingFlag = false;

    private class GenerateProblemThread implements Runnable {

        private final Handler handler;
        private final List<String> qAnswerList;
        private final List<String> qBodyList;
        private final List<String> subjectList;
        private final JSONArray jsArray;

        public GenerateProblemThread(Handler handler, @NonNull List<String> qAnswerList, @NonNull List<String> qBodyList,
                                     @NonNull List<String> subjectList, @NonNull JSONArray jsArray) {
            this.handler = handler;
            this.qAnswerList = qAnswerList;
            this.qBodyList = qBodyList;
            this.subjectList = subjectList;
            this.jsArray = jsArray;
        }

        @Override
        public void run() {
            for(int i = 0; i < jsArray.size(); i ++){
                JSONObject jsObject = (JSONObject) jsArray.get(i);
                Log.v("lzgsm", jsObject.toJSONString());
                String uriname = (String) jsObject.get(ConstantUtilities.ARG_NAME);
                Map<String, String> request = new HashMap<>();
                request.put("uriName", uriname);
                JSONObject tmp = null;
                try {
                    tmp = RequestBuilder.sendGetRequest(
                            "typeOpen/open/questionListByUriName", request);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
                Log.v("tmp", tmp.toJSONString());

                JSONArray mJSONArray;
                mJSONArray = (JSONArray) tmp.get(ConstantUtilities.ARG_DATA);
                if(mJSONArray != null)
                    for(Object obj : mJSONArray) {
                        JSONObject object = JSON.parseObject(obj.toString());
                        String qBody = object.getString("qBody");
                        String answer = object.getString("qAnswer");
                        qAnswerList.add(answer);
                        qBodyList.add(qBody);
                        subjectList.add((String) jsObject.get(ConstantUtilities.ARG_SUBJECT));
                    }
            }
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

}