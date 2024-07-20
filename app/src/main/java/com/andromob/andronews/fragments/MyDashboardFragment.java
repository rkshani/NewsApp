package com.andromob.andronews.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.adapter.MyDashboardAdapter;
import com.andromob.andronews.interfaces.NewsListener;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Presenter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MyDashboardFragment extends Fragment implements NewsListener {
    User user;
    RecyclerView recyclerView;
    Presenter presenter;
    LinearLayout lyt_loading;
    RelativeLayout lyt_no_internet, emptyList;
    TextView txtNoConnection, txtErorMsg;
    MaterialButton btnRetry;
    MyDashboardAdapter adapter;
    public static boolean isNewsEdited = false;

    public MyDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_dashboard, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.my_dashboard));
        user = LoginPrefManager.getInstance(getActivity()).getUser();
        initViews(view);
        presenter = new Presenter(this);
        presenter.getMethodNewsByUser("user", user.getUser_id());
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        lyt_loading = view.findViewById(R.id.lyt_loading);
        emptyList = view.findViewById(R.id.emptyList);
        lyt_no_internet = view.findViewById(R.id.no_internet);
        btnRetry = view.findViewById(R.id.btnRetry);
        txtNoConnection = view.findViewById(R.id.txtNoConnection);
        txtErorMsg = view.findViewById(R.id.txtErorMsg);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(false);
        recyclerView.setLayoutManager(llm);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Methods(requireContext()).isNetworkAvailable()) {
                    presenter.getMethodNewsByUser("user", user.getUser_id());
                }
            }
        });
    }

    @Override
    public void showLoading() {
        lyt_no_internet.setVisibility(View.GONE);
        lyt_loading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        lyt_no_internet.setVisibility(View.GONE);
        lyt_loading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorLoading(String message) {
        String sub_msg = "";
        if (!new Methods(requireContext()).isNetworkAvailable()) {
            message = getString(R.string.no_internet_msg);
            sub_msg = getString(R.string.check_internet);
        } else {
            message = getString(R.string.whoops);
            sub_msg = getString(R.string.try_again);
        }
        lyt_no_internet.setVisibility(View.VISIBLE);
        txtNoConnection.setText(message);
        txtErorMsg.setText(sub_msg);
    }

    @Override
    public void setNews(List<News> dataList) {
        if (dataList.isEmpty()) {
            lyt_loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            lyt_no_internet.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
            return;
        }
        adapter = new MyDashboardAdapter(getActivity(), user);
        adapter.setData(dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewsEdited) {
            isNewsEdited = false;
            presenter.getMethodNewsByUser("user", user.getUser_id());
        }
    }
}