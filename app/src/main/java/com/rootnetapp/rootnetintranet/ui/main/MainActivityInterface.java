package com.rootnetapp.rootnetintranet.ui.main;

import android.support.v4.app.DialogFragment;

/**
 * Created by Propietario on 15/03/2018.
 */

public interface MainActivityInterface {

    void changeTitle(String title);

    void showActivity(Class<?> activityClass);

    void showDialog(DialogFragment dialog);

    void disposeDialog();

}
