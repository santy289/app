package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentResetPasswordBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;

import javax.inject.Inject;

public class ResetPasswordFragment extends Fragment {

    @Inject
    ResetPasswordViewModelFactory resetPasswordViewModelFactory;
    ResetPasswordViewModel resetPasswordViewModel;
    private FragmentResetPasswordBinding resetPasswordBinding;
    private ResetPasswordDialog dialog;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance(ResetPasswordDialog dialog) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.dialog = dialog;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        resetPasswordBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_reset_password, container, false);
        View view = resetPasswordBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        resetPasswordViewModel = ViewModelProviders
                .of(this, resetPasswordViewModelFactory)
                .get(ResetPasswordViewModel.class);
        resetPasswordBinding.btnAccept.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        resetPasswordBinding.btnCancel.setOnClickListener(view12 -> {
            dialog.dismiss();
        });
        return view;
    }

}
