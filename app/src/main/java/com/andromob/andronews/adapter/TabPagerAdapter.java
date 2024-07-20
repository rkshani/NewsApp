package com.andromob.andronews.adapter;

import static com.andromob.andronews.utils.Constant.defaultTabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.andromob.andronews.fragments.NewsFragment;
import com.andromob.andronews.models.Category;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStateAdapter {
    private int numOfTabs;
    private List<Category> categoryList = new ArrayList<>();

    public TabPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Category> categoryList, int numOfTabs) {
        super(fragmentManager, lifecycle);
        this.categoryList = categoryList;
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return NewsFragment.newInstance("latest",-1);
        } else if (position == 1) {
            return NewsFragment.newInstance("trending",-1);
        } else {
            return NewsFragment.newInstance("category",categoryList.get(position-defaultTabs).getCat_id());
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
