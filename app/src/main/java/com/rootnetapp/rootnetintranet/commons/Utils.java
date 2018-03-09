package com.rootnetapp.rootnetintranet.commons;

import android.app.ProgressDialog;
import android.content.Context;

import com.rootnetapp.rootnetintranet.R;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Utils {

    public static final String URL = "http://api.rootnetapp.com";

    public static final String remainderOfDomain = ".rootnetapp.com";

    private static ProgressDialog progress;

    public static void showLoading(Context ctx){
        progress = new ProgressDialog(ctx);
        progress.show();
        progress.setMessage(ctx.getString(R.string.loading_message));
        progress.setCancelable(false);
    }

    public static void hideLoading(){
        if (null != progress && progress.isShowing()){
            progress.dismiss();
        }
    }

}
