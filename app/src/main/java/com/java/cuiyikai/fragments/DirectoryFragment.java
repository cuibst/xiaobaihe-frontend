package com.java.cuiyikai.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.FavouriteCheckActivity;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.adapters.BottomFavouriteAdapter;
import com.java.cuiyikai.adapters.FavouriteAdapter;
import com.java.cuiyikai.entities.BottomFavouriteEntity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.widgets.ListViewForScrollView;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

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

    private static final String ARG_DIRECTORY_NAME = "directoryName";

    // TODO: Rename and change types of parameters
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
    // TODO: Rename and change types and number of parameters
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
        args.put("directory", directoryName);
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

        SwipeRecyclerView favouriteItemList = (SwipeRecyclerView) view.findViewById(R.id.directoryNameList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favouriteItemList.setLayoutManager(layoutManager);

        SwipeMenuCreator swipeMenuCreator = (SwipeMenu leftMenu, SwipeMenu rightMenu, int position) -> {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = DensityUtilities.dp2px(getActivity(), 70);
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackground(R.color.bbl_ff0000)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(deleteItem);
        };

        favouriteAdapter = new FavouriteAdapter(DirectoryFragment.this, ((MainApplication)getActivity().getApplication()).getFavourite().getJSONArray(directoryName));

        favouriteItemList.setSwipeMenuCreator(swipeMenuCreator);

        favouriteItemList.setOnItemMenuClickListener((SwipeMenuBridge menuBridge, int position) -> {
            menuBridge.closeMenu();
            favouriteAdapter.getFavouriteArray().remove(position);
            favouriteAdapter.notifyItemRemoved(position);
            updateDirectoryBackend(favouriteAdapter.getFavouriteArray());
        });

        favouriteItemList.setLongPressDragEnabled(true);

        favouriteItemList.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(favouriteAdapter.getFavouriteArray(), fromPosition, toPosition);
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

        ListViewForScrollView bottomFavouriteView = (ListViewForScrollView) contentView.findViewById(R.id.bottomFavouriteListView);
        JSONObject favouriteJson = ((MainApplication)getActivity().getApplication()).getFavourite();
        List<BottomFavouriteEntity> entityList = new ArrayList<>();
        for(String key : favouriteJson.keySet()) {
            if(!key.equals(directoryName))
                entityList.add(new BottomFavouriteEntity(false, key));
        }

        BottomFavouriteAdapter adapter = new BottomFavouriteAdapter(getActivity(), R.layout.bottom_dialog_favourite_item, entityList);

        bottomFavouriteView.setAdapter(adapter);

        view.findViewById(R.id.btnMoveFavourite).setOnClickListener((View v) -> {
            //TODO: the logic for move!

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
                    args.put("directory", targetName);
                    args.put("json", moveArray);
                    try {
                        RequestBuilder.asyncSendBackendPostRequest("/api/favourite/moveDirectory", args, true);
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
//            Log.v("paper1", );
//            ((MainApplication) getApplication()).getFavourite().toJSONString()
//            Intent intent = new Intent(getActivity(), PaperActivity.class);

//            intent.putExtra("containing", containing);
//
//            startActivity(intent);
//            getActivity().finish();
            JSONObject js = ((MainApplication) getActivity().getApplication()).getFavourite();
            int cnt = 0;
            JSONArray jsArray = (JSONArray) js.get(directoryName);
            List<String> qBodyList = new ArrayList<>();
            List<String> qAnswerList = new ArrayList<>();
            for(int i = 0; i < jsArray.size(); i ++){
                Log.v("num", i + "");
                JSONObject jsObject = (JSONObject) jsArray.get(i);
                String uriname = (String) jsObject.get("name");
                Map<String, String> request = new HashMap<String, String>();
                request.put("uriName", uriname);
                Log.v("mtag", "in");
                JSONObject tmp = null;
                try {
                    tmp = (JSONObject) RequestBuilder.sendGetRequest(
                            "typeOpen/open/questionListByUriName", request);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                List<String> mList = new ArrayList<>();
                Log.v("tmp", tmp.toJSONString());
                Log.v("tmp", tmp.getClass().toString());
//                onPause();
                JSONArray mJSONArray = null;
                mJSONArray = (JSONArray) tmp.get("data");
                if(mJSONArray.isEmpty()){
                    Log.v("tmp", "empty");
                    continue;
                }
                Log.v("tmp", mJSONArray.toString());
                Log.v("tmp", mJSONArray.get(i).toString());
                Map<String , String> mMap = (Map<String, String>) mJSONArray.get(i);
                String qBody = mMap.get("qBody");
                String answer = mMap.get("qAnswer");
                qAnswerList.add(answer);
                qBodyList.add(qBody);
                cnt ++;
            }
            Intent mIntent = new Intent(getActivity(), ProblemActivity.class);
            mIntent.putExtra("body", qBodyList.toString());
            mIntent.putExtra("answer", qAnswerList.toString());
            mIntent.putExtra("type", "list");
            mIntent.putExtra("sum", cnt + "");
//                String questionBody
            startActivity(mIntent);
        });
        view.findViewById(R.id.btnCopyFavourite).setOnClickListener((View v) -> {
            //TODO: the logic for move!

            JSONArray moveArray = new JSONArray();
            for(int i=0;i<favouriteAdapter.getSelected().length;i++)
                moveArray.add(favouriteAdapter.getFavouriteArray().get(i));

            Button btnBottomFinish = contentView.findViewById(R.id.buttonBottomFinish);
            btnBottomFinish.setOnClickListener((View vi) -> {
                Set<String> checkedSet = adapter.getCheckedSet();
                for(String targetName : checkedSet) {
                    JSONObject args = new JSONObject();
                    args.put("directory", targetName);
                    args.put("json", moveArray);
                    try {
                        RequestBuilder.asyncSendBackendPostRequest("/api/favourite/moveDirectory", args, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                bottomDialog.dismiss();
            });

            bottomDialog.show();
        });


        view.findViewById(R.id.btnDeleteItems).setOnClickListener((View v) -> {
            //TODO: the logic for delete!
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
            args.put("directory", directoryName);
            try {
                RequestBuilder.asyncSendBackendPostRequest("/api/favourite/removeDirectory", args, true);
                ((MainApplication) getActivity().getApplication()).updateFavourite();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((FavouriteCheckActivity)getActivity()).updateDirectories(false);

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

}