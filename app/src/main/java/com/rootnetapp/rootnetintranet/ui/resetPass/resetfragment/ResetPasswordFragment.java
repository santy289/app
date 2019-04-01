package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentResetPasswordBinding;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;

import javax.inject.Inject;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class ResetPasswordFragment extends Fragment {

    @Inject
    ResetPasswordViewModelFactory resetPasswordViewModelFactory;
    ResetPasswordViewModel resetPasswordViewModel;
    private FragmentResetPasswordBinding resetPasswordBinding;
    private ResetPasswordDialog dialog;
    private String token = null;

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
        resetPasswordBinding.btnValidate.setOnClickListener(view1 -> {
            validateToken();
        });
        resetPasswordBinding.btnChangePassword.setOnClickListener(view1 -> {
            changePassword();
        });
        resetPasswordBinding.btnCancel.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        resetPasswordBinding.btnCancelPass.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        subscribe();
        return view;
    }

    private void subscribe() {
        final Observer<ResetPasswordResponse> tokenObserver = ((ResetPasswordResponse data) -> {
            dialog.hideLoading();
            if (null != data) {
                resetPasswordBinding.layoutNewPassword.setVisibility(View.VISIBLE);
                resetPasswordBinding.layoutToken.setVisibility(View.GONE);
                token = resetPasswordBinding.inputToken.getText().toString().trim();
                Toast.makeText(getContext(), getString(R.string.enter_password), Toast.LENGTH_LONG)
                        .show();
            }
        });
        final Observer<ResetPasswordResponse> resetObserver = ((ResetPasswordResponse data) -> {
            dialog.hideLoading();
            if (null != data) {
                Toast.makeText(getContext(), getString(R.string.password_changed),
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            dialog.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        resetPasswordViewModel.getObservableValidate().observe(this, tokenObserver);
        resetPasswordViewModel.getObservableReset().observe(this, resetObserver);
        resetPasswordViewModel.getObservableError().observe(this, errorObserver);
    }

    private void validateToken() {
        String token = resetPasswordBinding.inputToken.getText().toString().trim();
        resetPasswordBinding.tilToken.setError(null);
        if (TextUtils.isEmpty(token)) {
            resetPasswordBinding.tilToken.setError(getString(R.string.empty_token));
        } else {
            dialog.showLoading();
            resetPasswordViewModel.validateToken(token);
        }
    }

    private void changePassword() {
        String password = resetPasswordBinding.inputPassword.getText().toString().trim(),
                repassword = resetPasswordBinding.inputRepassword.getText().toString().trim();
        boolean canRequest = true;
        resetPasswordBinding.tilPassword.setError(null);
        resetPasswordBinding.tilRepassword.setError(null);
        if (TextUtils.isEmpty(password)) {
            canRequest = false;
            resetPasswordBinding.tilPassword.setError(getString(R.string.empty_password));
        }
        if (TextUtils.isEmpty(repassword)) {
            canRequest = false;
            resetPasswordBinding.tilRepassword.setError(getString(R.string.empty_password));
        } else {
            if (!password.equals(repassword)) {
                canRequest = false;
                resetPasswordBinding.tilRepassword.setError(getString(R.string.password_mismatch));
            }
        }
        if (canRequest) {
            dialog.showLoading();
            resetPasswordViewModel.resetPassword(token, password, repassword);
        }
    }

}
