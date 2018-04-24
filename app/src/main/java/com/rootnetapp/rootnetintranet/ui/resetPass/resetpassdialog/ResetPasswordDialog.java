package com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DialogResetPassBinding;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordDialog extends DialogFragment{

    private DialogResetPassBinding resetPassBinding;

    public static ResetPasswordDialog newInstance() {
        ResetPasswordDialog fragment = new ResetPasswordDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resetPassBinding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_reset_pass, container, false);
        View view = resetPassBinding.getRoot();
        setCancelable(true);
        getDialog().getWindow().setTitle(getString(R.string.recover_password));
        ResetPasswordViewpagerAdapter adapter = new ResetPasswordViewpagerAdapter(this, getChildFragmentManager());
        resetPassBinding.viewpager.setAdapter(adapter);
        resetPassBinding.slidingTabs.setupWithViewPager(resetPassBinding.viewpager);
        return view;
    }

    public void showLoading(){
        resetPassBinding.layoutProgress.setVisibility(View.VISIBLE);
        resetPassBinding.layoutPager.setVisibility(View.GONE);
    }

    public void hideLoading(){
        resetPassBinding.layoutPager.setVisibility(View.VISIBLE);
        resetPassBinding.layoutProgress.setVisibility(View.GONE);
    }

    public void nextTab(){
        resetPassBinding.viewpager.setCurrentItem(resetPassBinding.viewpager.getCurrentItem()+1);
    }

}
