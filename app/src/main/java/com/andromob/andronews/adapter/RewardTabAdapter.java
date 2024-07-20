package com.andromob.andronews.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.andromob.andronews.fragments.EarnWithdrwalFragment;

public class RewardTabAdapter extends FragmentStateAdapter {
    private int numOfTabs;

    public RewardTabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numOfTabs) {
        super(fragmentManager, lifecycle);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return EarnWithdrwalFragment.newInstance("earning");
        } else if (position == 1) {
            return EarnWithdrwalFragment.newInstance("withdrawal");
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
