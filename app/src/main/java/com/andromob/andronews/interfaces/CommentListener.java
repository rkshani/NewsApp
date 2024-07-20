package com.andromob.andronews.interfaces;

public interface CommentListener {
    void showLoading();
    void hideLoading();
    void onCommentSuccess();
    void onCommentError();
}
