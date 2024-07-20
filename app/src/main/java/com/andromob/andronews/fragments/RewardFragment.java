package com.andromob.andronews.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.activity.RedeemActivity;
import com.andromob.andronews.adapter.RewardTabAdapter;
import com.andromob.andronews.models.User;
import com.andromob.andronews.models.UserPoints;
import com.andromob.andronews.utils.Constant;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Prefs;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardFragment extends Fragment {
    User user;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    LinearLayout pointsDashboard;
    ProgressBar pointsProgress;
    TextView points, available_amount, format_points, format_amount;
    Button btnRedeem;
    Methods methods;
    Prefs prefs;

    public RewardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reward, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.reward_points_history));
        prefs = Prefs.getInstance(getContext());
        user = LoginPrefManager.getInstance(getContext()).getUser();
        methods = new Methods(getContext());
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        pointsDashboard = view.findViewById(R.id.pointsDashboard);
        pointsProgress = view.findViewById(R.id.pointsProgress);
        points = view.findViewById(R.id.points);
        available_amount = view.findViewById(R.id.available_amount);
        format_points = view.findViewById(R.id.format_points);
        format_amount = view.findViewById(R.id.format_amount);
        btnRedeem = view.findViewById(R.id.btnRedeem);
        setupViewPager();
        fetchUserPoints();
        return view;
    }

    void fetchUserPoints() {
        pointsDashboard.setVisibility(View.INVISIBLE);
        pointsProgress.setVisibility(View.VISIBLE);
        Call<List<UserPoints>> getCall = Methods.getApi().getUserPoints("getUserPoints", BuildConfig.API_KEY, user.getUser_id());
        getCall.enqueue(new Callback<List<UserPoints>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<UserPoints>> call, @NonNull Response<List<UserPoints>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserPoints row = response.body().get(0);
                    points.setText(String.valueOf(row.getPoints()));
                    DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");
                    df.setDecimalSeparatorAlwaysShown(true);
                    df.setMinimumFractionDigits(2);
                    double getamount = (row.getPoints() * prefs.getAppSetting().get(0).getRedeem_amount()) / prefs.getAppSetting().get(0).getRedeem_points();
                    available_amount.setText(prefs.getAppSetting().get(0).getRedeem_currency() + " " + df.format(getamount));
                    format_amount.setText(prefs.getAppSetting().get(0).getRedeem_currency() + " " + df.format(prefs.getAppSetting().get(0).getRedeem_amount()));
                    format_points.setText(String.valueOf(prefs.getAppSetting().get(0).getRedeem_points()));
                    btnRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (row.getPoints() >= prefs.getAppSetting().get(0).getMinimum_redeem_points()) {
                                Intent intent = new Intent(getActivity(), RedeemActivity.class);
                                intent.putExtra("points", row.getPoints());
                                intent.putExtra("amount", getamount);
                                intent.putExtra("balance", prefs.getAppSetting().get(0).getRedeem_currency() + " " + df.format(getamount));
                                startActivity(intent);
                            } else {
                                methods.showDialog(getActivity(), getString(R.string.not_eligible), getString(R.string.not_eligible_msg_before) + " " + prefs.getAppSetting().get(0).getMinimum_redeem_points() + " " + getString(R.string.not_eligible_msg_after), "alert", "dismiss");
                            }
                        }
                    });
                    pointsProgress.setVisibility(View.GONE);
                    pointsDashboard.setVisibility(View.VISIBLE);
                } else {
                    pointsProgress.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserPoints>> call, @NonNull Throwable t) {
                pointsDashboard.setVisibility(View.INVISIBLE);
                pointsProgress.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Error : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void setupViewPager() {
        RewardTabAdapter adapter = new RewardTabAdapter(getChildFragmentManager(), getLifecycle(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserPoints();
    }
}