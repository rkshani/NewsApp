package com.andromob.andronews.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.FrameLayout;

import com.andromob.andronews.R;

public class LoadingDialog {
    public static android.app.Dialog dialog;

    public static void startLoadingdialog(Activity activity) {
        dialog = new android.app.Dialog(activity);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().width = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = FrameLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // dismiss method
    public static void dismissdialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}