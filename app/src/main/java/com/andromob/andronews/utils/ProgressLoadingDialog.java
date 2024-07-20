package com.andromob.andronews.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.FrameLayout;

import com.andromob.andronews.R;

public class ProgressLoadingDialog {
    private final Context mContext;

    public ProgressLoadingDialog(Context context){
        this.mContext = context;
    }
    private static android.app.Dialog dialog;

    public void startLoadingdialog() {
        dialog = new android.app.Dialog((Activity) mContext);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // dismiss method
    public void dismissdialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}