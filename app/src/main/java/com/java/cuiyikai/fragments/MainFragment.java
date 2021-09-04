package com.java.cuiyikai.fragments;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
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
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.activities.SearchViewActivity;
import com.java.cuiyikai.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment {
    private ViewPager viewPager;
    private ItemFragment[] itemFragment;
    private ImageView searchImageView;
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter ;
    private List<String> allSubjectItem = new ArrayList<>(Arrays.asList("语文","数学","英语","物理","化学","生物","历史","地理","政治"));

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_main, null);
        initViewPager(view);
        itemFragment=new ItemFragment[allSubjectItem.size()];

        TabLayout tabLayout = view.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(viewPager);
        ImageView tabAdd = view.findViewById(R.id.tab_add);
        ImageView searchImageView = view.findViewById(R.id.searchImageView);
        searchImageView.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), SearchViewActivity.class);
            startActivity(intent);
        });
        tabAdd.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), CategoryActivity.class);
            startActivityForResult(intent, 1);
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            viewPagerFragmentAdapter.clear();
            itemFragment=new ItemFragment[allSubjectItem.size()];
            initViewPager(getView());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.viewpgr1);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        allSubjectItem = ((MainApplication)getActivity().getApplication()).getSubjects();
        viewPager.setAdapter(viewPagerFragmentAdapter);
    }

    public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            initFragment(position);
            return itemFragment[position];
        }

        @Override
        public int getCount() {
            return allSubjectItem.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return allSubjectItem.get(position);
        }

        public void clear() {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (ItemFragment fragment : itemFragment)
                if (fragment != null)
                    transaction.remove(fragment);
            transaction.commit();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
            //Nullified this function to avoid page buffer!!!
        }
    }

    public void initFragment(int position)
    {
        String chooseSubject = ((MainActivity)getActivity()).checkSubject(allSubjectItem.get(position));
        ItemFragment fragment=new ItemFragment(chooseSubject,getActivity());
        itemFragment[position]=fragment;
    }

}
