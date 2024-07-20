package com.andromob.andronews.fragments;

import static com.andromob.andronews.utils.Constant.isCommentMadeFinal;
import static com.andromob.andronews.utils.Constant.newCommentCount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.LoginActivity;
import com.andromob.andronews.adapter.NewsAdapter;
import com.andromob.andronews.interfaces.NewsListener;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Presenter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements NewsListener {
    RecyclerView recyclerView;
    Presenter presenter;
    LinearLayout lyt_loading;
    RelativeLayout lyt_no_internet, emptyList, layout_login;
    TextView txtNoConnection, txtErorMsg;
    MaterialButton btnRetry, btnLogin;
    NewsAdapter adapter;
    String type;
    int cat_id;
    List<News> newsArrayList = new ArrayList<>();
    ProgressBar loadMoreProgress;
    private int currentpage = 1;
    private Boolean isOver = false, isScroll = false, isLoading = false, isScrollable = false;
    User user;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(String type, int cat_id) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putInt("cat_id", cat_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.app_name));
        user = LoginPrefManager.getInstance(getActivity()).getUser();
        initViews(view);
        presenter = new Presenter(this);

        loadNews();
        return view;
    }

    void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        lyt_loading = view.findViewById(R.id.lyt_loading);
        emptyList = view.findViewById(R.id.emptyList);
        layout_login = view.findViewById(R.id.layout_login);
        btnLogin = view.findViewById(R.id.btnLogin);
        lyt_no_internet = view.findViewById(R.id.no_internet);
        btnRetry = view.findViewById(R.id.btnRetry);
        txtNoConnection = view.findViewById(R.id.txtNoConnection);
        txtErorMsg = view.findViewById(R.id.txtErorMsg);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        NestedScrollView scroller = view.findViewById(R.id.scroller);
        scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (isScrollable) {
                        if (!isOver) {
                            if (!isLoading) {
                                isLoading = true;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isScroll = true;
                                        loadNews();
                                        Log.i("scroller", "Loading scroll : " + currentpage);
                                    }
                                }, 0);
                            }
                        }
                    }
                    Log.i("scroller", "THE END");
                }

                if (scrollY > oldScrollY) {
                    // Log.i("scroller", "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
                    //Log.i("scroller", "Scroll UP");
                }

                if (scrollY == 0) {
                    // Log.i("scroller", "TOP SCROLL");
                }

                if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
                    // Log.i("scroller", "BOTTOM SCROLL");
                }
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Methods(requireContext()).isNetworkAvailable()) {
                    loadNews();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    currentpage = 1;
                    loadNews();
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
    }

    private void loadNews() {
        if (getArguments() != null) {
            type = getArguments().getString("type");
            cat_id = getArguments().getInt("cat_id");
            if (type != null && !type.equals("")) {
                isScrollable = false;
                switch (type) {
                    case "latest":
                        presenter.getMethodNews("latest");
                        break;
                    case "trending":
                        presenter.getMethodNews("trending");
                        break;
                    case "favorite":
                        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.favorites));
                        if (!LoginPrefManager.getInstance(getActivity()).isLoggedIn()) {
                            layout_login.setVisibility(View.VISIBLE);
                        } else {
                            presenter.getMethodNewsFavorite(user.getUser_id());
                        }
                        break;
                    case "category":
                        if (cat_id != 0 && cat_id != -1) {
                            isScrollable = true;
                            presenter.getNewsByCategory(cat_id, currentpage);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                if (type.equals("favorite")) {
                    if (!LoginPrefManager.getInstance(getActivity()).isLoggedIn()) {
                        layout_login.setVisibility(View.VISIBLE);
                    }
                } else {
                    layout_login.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showLoading() {
        lyt_no_internet.setVisibility(View.GONE);
        if (isScroll) {
            loadMoreProgress.setVisibility(View.VISIBLE);
        } else {
            lyt_loading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        lyt_no_internet.setVisibility(View.GONE);
        lyt_loading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        isLoading = false;
        loadMoreProgress.setVisibility(View.GONE);
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
            isOver = true;
        } else {
            newsArrayList.clear();
            newsArrayList.addAll(dataList);
            setAdapter();
        }
        checkEmpty();
    }

    void checkEmpty() {
        if (newsArrayList.isEmpty()) {
            lyt_loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        if (!isOver) {
            currentpage = currentpage + 1;
            if (!isScroll) {
                adapter = new NewsAdapter(getActivity(), false, user);
                adapter.setData(newsArrayList);
                recyclerView.setAdapter(adapter);
            } else {
                /*if (adapter == null){
                    adapter = new NewsAdapter(getActivity(), false, user);
                    adapter.setData(newsArrayList);
                    recyclerView.setAdapter(adapter);
                } else {*/
                    adapter.setData(newsArrayList);
                    adapter.notifyDataSetChanged();
                //}
            }
        }
        recyclerView.setVisibility(View.VISIBLE);
        checkEmpty();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCommentMadeFinal) {
            newCommentCount = 0;
            isCommentMadeFinal = false;
            loadNews();
        }
    }
}