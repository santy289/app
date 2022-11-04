package com.rootnetapp.rootnetintranet.ui.projectFragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

public class ProjectFragment extends Fragment {

    private ProjectViewModel mViewModel;

    public static ProjectFragment newInstance(MainActivity mainActivity) {
        return new ProjectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        RecyclerView recycleProject = (RecyclerView) view.findViewById(R.id.rec_projects);
        ProjectAdapter projectAdapter = new ProjectAdapter(mViewModel.loadData());
        recycleProject.setAdapter(projectAdapter);
        recycleProject.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }

}