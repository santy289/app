package com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentRequestTokenBinding;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;

import javax.inject.Inject;

import androidx.lifecycle.Observer;
import android.widget.Toast;

public class RequestTokenFragment extends Fragment {

    @Inject
    RequestTokenViewModelFactory requestTokenViewModelFactory;
    RequestTokenViewModel requestTokenViewModel;
    private ResetPasswordDialog dialog;
    private FragmentRequestTokenBinding requestTokenBinding;

    public RequestTokenFragment() {
        // Required empty public constructor
    }

    public static RequestTokenFragment newInstance(ResetPasswordDialog dialog) {
        RequestTokenFragment fragment = new RequestTokenFragment();
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
        requestTokenBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_request_token, container, false);
        View view = requestTokenBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        requestTokenViewModel = ViewModelProviders
                .of(this, requestTokenViewModelFactory)
                .get(RequestTokenViewModel.class);
        requestTokenBinding.btnAccept.setOnClickListener(view1 -> {
            requestToken();
        });
        requestTokenBinding.btnCancel.setOnClickListener(view12 -> {
            dialog.dismiss();
        });
        subscribe();
        return view;
    }

    private void requestToken() {
        String username = requestTokenBinding.inputUser.getText().toString().trim();
        requestTokenBinding.tilUsername.setError(null);
        if(TextUtils.isEmpty(username)){
            requestTokenBinding.tilUsername.setError(getString(R.string.empty_user));
        }else{
            dialog.showLoading();
            requestTokenViewModel.requestToken(username);
        }
    }

    private void subscribe() {
        final Observer<ResetPasswordResponse> tokenObserver = ((ResetPasswordResponse data) -> {
            dialog.hideLoading();
            if (null != data) {
                if (data.getCode() == 200){
                    dialog.nextTab();
                    Toast.makeText(getContext(), getString(R.string.check_email), Toast.LENGTH_LONG).show();
                }else{
                    //TODO mejorar toast
                    Toast.makeText(getContext(), getString(R.string.failure_connect), Toast.LENGTH_LONG).show();
                }
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            dialog.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        requestTokenViewModel.getObservableToken().observe(this, tokenObserver);
        requestTokenViewModel.getObservableError().observe(this, errorObserver);
    }

}
