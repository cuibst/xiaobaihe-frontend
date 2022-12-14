package com.java.cuiyikai.activities;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.fragments.DirectoryFragment;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.java.cuiyikai.utilities.DensityUtilities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * {@link android.app.Activity} for Favourite checking.
 */
public class FavouriteCheckActivity extends AppCompatActivity {

    private ViewPager directoryPager;

    private ArrayList<DirectoryFragment> directoryFragments;
    private ArrayList<String> directoryNames;

    /**
     * Modified to remove the buffer of the pages.
     */
    public class DirectoryStatePagerAdapter extends FragmentStatePagerAdapter {

        public DirectoryStatePagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return directoryFragments.get(position);
        }

        /**
         * changed to reload when request
         * {@inheritDoc}
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return directoryFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(directoryNames.get(position).equals("default"))
                return "???????????????";
            return directoryNames.get(position);
        }

        /**
         * clear all the pages in the buffer.
         * To make sure the pages are rebuilt when the activity is reloaded.
         */
        public void clear() {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (DirectoryFragment fragment : directoryFragments)
                if (fragment != null)
                    transaction.remove(fragment);
            transaction.commit();
        }

        @Override
        public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
            //Nullify to avoid state buffer.
        }
    }

    private DirectoryStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_check);

        //set up the pager.
        directoryPager = findViewById(R.id.directoryPager);
        TabLayout directoryNameTab = findViewById(R.id.directoryTabLayout);
        directoryNameTab.setupWithViewPager(directoryPager);

        initPager();

        adapter = new DirectoryStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        //set up the add new directory dialog
        //see EntityActivity for more information, these two are almost the same.
        findViewById(R.id.btnAddNewDirectory).setOnClickListener((View v) -> {
            Dialog addNewDirectoryDialog = new Dialog(FavouriteCheckActivity.this, R.style.BottomDialog);
            View directoryContentView = LayoutInflater.from(this).inflate(R.layout.layout_add_new_directory, null);
            addNewDirectoryDialog.setContentView(directoryContentView);
            addNewDirectoryDialog.getWindow().setGravity(Gravity.CENTER);
            addNewDirectoryDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) directoryContentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
            params.bottomMargin = DensityUtilities.dp2px(this, 8f);
            directoryContentView.setLayoutParams(params);

            Button confirm = directoryContentView.findViewById(R.id.addDirectoryConfirm);
            Button cancel = directoryContentView.findViewById(R.id.addDirectoryCancel);

            cancel.setOnClickListener((View view) -> addNewDirectoryDialog.dismiss());

            confirm.setOnClickListener((View view) -> {
                EditText editText = directoryContentView.findViewById(R.id.newDirectoryName);
                if(editText.getText().toString().equals(""))
                    return;
                JSONObject args = new JSONObject();
                args.put(ConstantUtilities.ARG_DIRECTORY, editText.getText().toString());
                try {
                    RequestBuilder.sendBackendPostRequest("/api/favourite/addDirectory", args, true);
                    ((MainApplication) getApplication()).updateFavourite();
                } catch (BackendTokenExpiredException e) {
                    e.printStackTrace();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                updateDirectories();
                addNewDirectoryDialog.dismiss();
            });

            addNewDirectoryDialog.show();

        });

        directoryPager.setAdapter(adapter);

    }

    /**
     * initialize the pager's fragment.
     */
    private void initPager() {
        JSONObject favourite = ((MainApplication)getApplication()).getFavourite();

        directoryNames = new ArrayList<>(favourite.keySet());

        directoryFragments = new ArrayList<>();
        for(String name : directoryNames) {
            DirectoryFragment fragment = DirectoryFragment.newInstance(name);
            directoryFragments.add(fragment);
        }
    }

    /**
     * reset the pager when the directories are updated.
     */
    public void updateDirectories() {
        directoryPager.setCurrentItem(0);
        directoryPager.removeAllViewsInLayout();
        adapter.clear();
        ((MainApplication)getApplication()).updateFavourite();
        initPager();
        adapter.notifyDataSetChanged();
    }
}