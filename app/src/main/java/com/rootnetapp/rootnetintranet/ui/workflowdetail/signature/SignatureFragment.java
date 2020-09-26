package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

public class SignatureFragment extends Fragment {

    @Inject
    SignatureViewModelFactory signatureViewModelFactory;
    private SignatureViewModel signatureViewModel;

    public static SignatureFragment newInstance() {
        return new SignatureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        signatureViewModel = ViewModelProviders
                .of(this, signatureViewModelFactory)
                .get(SignatureViewModel.class);
        return inflater.inflate(R.layout.signature_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}