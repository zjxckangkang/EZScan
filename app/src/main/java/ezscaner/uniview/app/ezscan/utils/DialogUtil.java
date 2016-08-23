package ezscaner.uniview.app.ezscan.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Administrator on 2016/8/22.
 */
public class DialogUtil {
    public static void showDialog(final Context context, String title
            , String confirmBtnText, MaterialDialog.SingleButtonCallback singleButtonCallback1
            , String cancelBtnText, MaterialDialog.SingleButtonCallback singleButtonCallback2) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(title)
                .positiveText(confirmBtnText)
                .onPositive(singleButtonCallback1)
                .negativeText(cancelBtnText)
                .onNegative(singleButtonCallback2)
                .show();
    }

    public static MaterialDialog showDialog(final Context context, String title, View contentView) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .customView(contentView, false);
        MaterialDialog materialDialog = builder.build();
        materialDialog.show();
        return materialDialog;
    }

    public static void showDialog(final Context context, String title
            , String confirmBtnText, MaterialDialog.SingleButtonCallback singleButtonCallback1
            , String cancelBtnText, MaterialDialog.SingleButtonCallback singleButtonCallback2, View contentView) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .positiveText(confirmBtnText)
                .onPositive(singleButtonCallback1)
                .negativeText(cancelBtnText)
                .onNegative(singleButtonCallback2)
                .customView(contentView, false)
                .cancelable(true);
        MaterialDialog materialDialog = builder.build();
        materialDialog.show();
        materialDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

//        create.setContentView(contentView);
    }

    public static ProgressDialog showProgressDialog(Context context,
                                                    String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return progressDialog;
    }

    public static ProgressDialog showProgressDialog(Context context,
                                                    String message, int style) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {

            progressDialog.setProgressStyle(style);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.setProgressNumberFormat("%1d/%2d");

            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return progressDialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    interface OnClickListener {
        void onClick(String text);
    }
}
