package com.java.cuiyikai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.java.cuiyikai.MainApplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;
import com.java.cuiyikai.activities.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment {
    private ViewPager viewpgr;
    private ImageView tabAdd;
    private ItemFragment[] itemFragment;
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter ;
    private List<String> all_subject_item=new ArrayList<>(Arrays.asList("语文","数学","英语","物理","化学","生物","历史","地理","政治"));
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_main, null);
        initViewPager(view);
        itemFragment=new ItemFragment[all_subject_item.size()];


        tabLayout=view.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(viewpgr);
        tabAdd = view.findViewById(R.id.tab_add);

        tabAdd.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), CategoryActivity.class);
            startActivityForResult(intent, 1);
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            viewPagerFragmentAdapter.clear(viewpgr);
            itemFragment=new ItemFragment[all_subject_item.size()];
            initViewPager(getView());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViewPager(View view) {
        viewpgr = view.findViewById(R.id.viewpgr1);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getActivity().getSupportFragmentManager());
        all_subject_item = ((MainApplication)getActivity().getApplication()).getSubjects();
        viewpgr.setAdapter(viewPagerFragmentAdapter);
    }

    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
        ViewPagerFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            initfragment(position);
            return itemFragment[position];
        }

        @Override
        public int getCount() {
            return all_subject_item.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return all_subject_item.get(position);
        }

        public void clear(ViewGroup container) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (ItemFragment fragment : itemFragment)
                if (fragment != null)
                    transaction.remove(fragment);
            transaction.commit();
        }
    }


    public void initfragment(int position)
    {
        System.out.println("position: "+position);
        String TITLE=all_subject_item.get(position);
        String chooseSubject = ((MainActivity)getActivity()).checkSubject(TITLE);
        ItemFragment fragment=new ItemFragment(chooseSubject,getActivity());
        itemFragment[position]=fragment;
    }
}
