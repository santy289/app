package com.rootnetapp.rootnetintranet.ui.quickactions;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public interface QuickActionsInterface {

    void showFragment(Fragment fragment, boolean addToBackStack);

    void showActivity(Intent intent);
}