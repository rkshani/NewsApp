package com.andromob.andronews.fragments;

import static com.andromob.andronews.utils.Constant.defaultTabs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.andromob.andronews.R;
import com.andromob.andronews.adapter.TabPagerAdapter;
import com.andromob.andronews.interfaces.CategoryListener;
import com.andromob.andronews.models.Category;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Presenter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class HomeFragment extends Fragment implements CategoryListener {
    public View view;
    Presenter presenter;
    TabLayout tabLayout;
    TabPagerAdapter pagerAdapter;
    ProgressBar progressBar;
    ViewPager2 viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.app_name));
        presenter = new Presenter(this);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        progressBar = view.findViewById(R.id.progressBar);

        presenter.getCategories();
        return view;
    }

    private View getTabIcon(String imgUri, int i) {
        ImageView imageView = new ImageView(getActivity());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        TabLayout.LayoutParams params = new TabLayout.LayoutParams(new ViewGroup.LayoutParams(
                width, height));
        imageView.setLayoutParams(params);
        Glide.with(imageView.getContext()).load(imgUri).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                tabLayout.getTabAt(i).setIcon(resource);
                return false;
            }
        }).into(imageView);
        return imageView;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onErrorLoading(String message) {

    }

    @Override
    public void setCategory(List<Category> dataList) {
        if (getActivity() != null) {
            if (dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    tabLayout.addTab(tabLayout.newTab().setText(dataList.get(i).getCategory_name()));
                    getTabIcon(dataList.get(i).getCategory_image(),i + defaultTabs);
                }
                viewPager.setOffscreenPageLimit(dataList.size() + defaultTabs);
                pagerAdapter = new TabPagerAdapter(getChildFragmentManager(), getLifecycle(), dataList, tabLayout.getTabCount());
                viewPager.setAdapter(pagerAdapter);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        pagerAdapter = new TabPagerAdapter(getChildFragmentManager(), getLifecycle(), dataList, tabLayout.getTabCount());
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        pagerAdapter = new TabPagerAdapter(getChildFragmentManager(), getLifecycle(), dataList, tabLayout.getTabCount());
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                });
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (tabLayout.getTabAt(position) != null) {
                            tabLayout.getTabAt(position).select();
                        }
                        super.onPageSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                    }
                });
                tabLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
