package com.andromob.andronews.fragments;

import static com.andromob.andronews.utils.Constant.isCommentMadeFinal;
import static com.andromob.andronews.utils.Constant.newCommentCount;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.MainActivity;
import com.andromob.andronews.adapter.VideosAdapter;
import com.andromob.andronews.interfaces.VideosListener;
import com.andromob.andronews.models.Videos;
import com.andromob.andronews.utils.EndlessRecyclerViewScrollListener;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.Presenter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment implements VideosListener {
    RecyclerView recyclerView;
    Presenter presenter;
    LinearLayout lyt_loading;
    RelativeLayout lyt_no_internet, emptyList;
    TextView txtNoConnection, txtErorMsg;
    MaterialButton btnRetry;
    VideosAdapter adapter;
    ProgressBar loadMoreProgress;
    List<Videos> videosArrayList = new ArrayList<>();
    private int currentpage = 1;
    private Boolean isOver = false, isScroll = false, isLoading = false;

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        new Methods(requireContext()).getActionBar().setTitle(getString(R.string.videos));
        initViews(view);
        presenter = new Presenter(this);
        presenter.getMethodVideos(currentpage);
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
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                    if (!isOver) {
                        if (!isLoading) {
                            isLoading = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isScroll = true;
                                    presenter.getMethodVideos(currentpage);
                                }
                            }, 0);
                        }
                    }
            }
        });
        btnRetry.setOnClickListener(v -> {
            if (new Methods(requireContext()).isNetworkAvailable()){
                presenter.getMethodVideos(currentpage);
            }
        });
    }

    @Override
    public void showLoading() {
        lyt_no_internet.setVisibility(View.GONE);
        if (isScroll){
            loadMoreProgress.setVisibility(View.VISIBLE);
        }else {
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
    public void setVideos(List<Videos> dataList) {
        if (dataList.size() == 0) {
            isOver = true;
        } else {
            videosArrayList.clear();
            videosArrayList.addAll(dataList);
            setAdapter();
        }
        checkEmpty();
    }

    void checkEmpty(){
        if (videosArrayList.isEmpty()){
            lyt_loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        if (!isOver) {
            currentpage = currentpage+1;
            if (!isScroll) {
                adapter = new VideosAdapter(getActivity());
                adapter.setData(videosArrayList);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setData(videosArrayList);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
        checkEmpty();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCommentMadeFinal){
            newCommentCount = 0;
            isCommentMadeFinal = false;
            presenter.getMethodVideos(currentpage);
        }
        MainActivity.setBottomItemHome("video");
    }
}