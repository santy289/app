package com.rootnetapp.rootnetintranet.ui.main;

import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public interface MainActivityInterface {

    void showActivity(Class<?> activityClass);

    void showActivity(Intent intent);

    void showFragment(Fragment fragment, boolean addtobackstack);

    void showDialog(DialogFragment dialog);

    void disposeDialog();

    void showWorkflow(int id);

}