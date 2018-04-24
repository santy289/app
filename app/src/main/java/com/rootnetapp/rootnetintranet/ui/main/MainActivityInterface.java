package com.rootnetapp.rootnetintranet.ui.main;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Propietario on 15/03/2018.
 */

public interface MainActivityInterface {

    void showActivity(Class<?> activityClass);

    void showFragment(Fragment fragment, boolean addtobackstack);

    void showDialog(DialogFragment dialog);

    void disposeDialog();

}
