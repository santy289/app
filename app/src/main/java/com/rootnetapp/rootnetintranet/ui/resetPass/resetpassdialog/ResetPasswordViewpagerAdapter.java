package com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenFragment;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordViewpagerAdapter extends FragmentPagerAdapter {

    private ResetPasswordDialog dialog;

    public ResetPasswordViewpagerAdapter(ResetPasswordDialog dialog, FragmentManager fm) {
        super(fm);
        this.dialog = dialog;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:{
                return RequestTokenFragment.newInstance(dialog);
            }
            case 1:{
                return ResetPasswordFragment.newInstance(dialog);
            }
            default:{
                return null;
            }
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return dialog.getString(R.string.request_token_fragment);
            case 1:
                return dialog.getString(R.string.reset_password_fragment);
            default:
                return null;
        }
    }

}