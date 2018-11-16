package com.rootnetapp.rootnetintranet.ui.main;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public interface MainActivityInterface {

    void showActivity(Class<?> activityClass);

    void showFragment(Fragment fragment, boolean addtobackstack);

    void showDialog(DialogFragment dialog);

    void disposeDialog();

    void showWorkflow(int id);

}