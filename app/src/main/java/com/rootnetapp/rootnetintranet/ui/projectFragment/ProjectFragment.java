package com.rootnetapp.rootnetintranet.ui.projectFragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;
import com.rootnetapp.rootnetintranet.ui.projectFragment.models.List;

public class ProjectFragment extends Fragment {

    private ProjectViewModel mViewModel;

    public static ProjectFragment newInstance(MainActivity mainActivity) {
        return new ProjectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
        for (List list : mViewModel.loadData().list) {
            
        }

    }

}