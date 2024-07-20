package com.andromob.andronews.fragments;

import static com.andromob.andronews.utils.LoadingDialog.dismissdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.adapter.EarningAdapter;
import com.andromob.andronews.adapter.WithdrawalAdapter;
import com.andromob.andronews.models.EarnHistory;
import com.andromob.andronews.models.User;
import com.andromob.andronews.models.WithdrawalHistory;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarnWithdrwalFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noData;
    EarningAdapter adapter;
    WithdrawalAdapter withdrawalAdapter;
    User user;
    String type;

    public EarnWithdrwalFragment() {
        // Required empty public constructor
    }

    public static EarnWithdrwalFragment newInstance(String type) {
        EarnWithdrwalFragment fragment = new EarnWithdrwalFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earn_withdrwal, container, false);
        user = LoginPrefManager.getInstance(getActivity()).getUser();

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noData = view.findViewById(R.id.noData);
        checkType();
        return view;
    }

    void checkType() {
        if (getArguments() != null) {
            type = getArguments().getString("type");
            if (type != null && !type.equals("")) {
                if (type.equals("earning")) {
                    fetchEarningHistory();
                } else if (type.equals("withdrawal")) {
                    fetchWithdrawalHistory();
                }
            }
            return;
        }
        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
    }

    void fetchEarningHistory() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<EarnHistory>> dataCall = Methods.getApi().getEarnHistory("earnHistory", BuildConfig.API_KEY, user.getUser_id());
        dataCall.enqueue(new Callback<List<EarnHistory>>() {
            @Override
            public void onResponse(@NonNull Call<List<EarnHistory>> call, @NonNull Response<List<EarnHistory>> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()
                                , LinearLayoutManager.VERTICAL, false));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        adapter = new EarningAdapter(getActivity());
                        adapter.setData(response.body());
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EarnHistory>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
                noData.setText(getString(R.string.something_went_wrong));
            }
        });
    }

    void fetchWithdrawalHistory() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<WithdrawalHistory>> dataCall = Methods.getApi().getWithdrawalHistory("withdrawalHistory", BuildConfig.API_KEY, user.getUser_id());
        dataCall.enqueue(new Callback<List<WithdrawalHistory>>() {
            @Override
            public void onResponse(@NonNull Call<List<WithdrawalHistory>> call, @NonNull Response<List<WithdrawalHistory>> response) {
                dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()
                                , LinearLayoutManager.VERTICAL, false));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        withdrawalAdapter = new WithdrawalAdapter(getActivity());
                        withdrawalAdapter.setData(response.body());
                        recyclerView.setAdapter(withdrawalAdapter);
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WithdrawalHistory>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
                noData.setText(getString(R.string.something_went_wrong));
            }
        });
    }
}