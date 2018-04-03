package com.rootnetapp.rootnetintranet.ui.workflowdetail;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.ApproversAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.InformationAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.StepsAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class WorkflowDetailFragment extends Fragment {

    @Inject
    WorkflowDetailViewModelFactory workflowViewModelFactory;
    WorkflowDetailViewModel workflowDetailViewModel;
    private FragmentWorkflowDetailBinding binding;
    private MainActivityInterface mainActivityInterface;
    private Workflow item;

    public WorkflowDetailFragment() {
        // Required empty public constructor
    }

    public static WorkflowDetailFragment newInstance(Workflow item, MainActivityInterface mainActivityInterface) {
        WorkflowDetailFragment fragment = new WorkflowDetailFragment();
        fragment.item = item;
        fragment.mainActivityInterface = mainActivityInterface;
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
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail, container, false);
        View view = binding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowDetailViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowDetailViewModel.class);
        //Utils.showLoading(getContext());
        binding.recSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recApprovers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recInfo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //todo SOLO testing hasta tener el backend live
        binding.recApprovers.setAdapter(new ApproversAdapter());
        //fin testing
        subscribe();
        binding.hdrGraph.setOnClickListener(this::headerClicked);
        binding.hdrImportant.setOnClickListener(this::headerClicked);
        binding.hdrNextstep.setOnClickListener(this::headerClicked);
        binding.hdrInfo.setOnClickListener(this::headerClicked);
        workflowDetailViewModel.getWorkflow(item.getId());
        workflowDetailViewModel.getWorkflowType(item.getWorkflowType().getId());
        return view;
    }

    private void headerClicked(View view) {
        switch (view.getId()) {
            case R.id.hdr_graph: {
                if (binding.lytGraph.getVisibility() == View.GONE) {
                    binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.lytGraph.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.lytGraph.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_important: {
                if (binding.lytImportant.getVisibility() == View.GONE) {
                    binding.btnArrow2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.lytImportant.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.lytImportant.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_nextstep: {
                if (binding.lytNextstep.getVisibility() == View.GONE) {
                    binding.btnArrow3.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.lytNextstep.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow3.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.lytNextstep.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_info: {
                if (binding.recInfo.getVisibility() == View.GONE) {
                    binding.btnArrow4.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.recInfo.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow4.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.recInfo.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void subscribe() {
        final Observer<Workflow> workflowObserver = ((Workflow data) -> {
            if (null != data) {
                List<Information> infoList = new ArrayList<>();
                infoList.add(new Information(getString(R.string.description),
                        data.getDescription()));
                infoList.add(new Information(getString(R.string.start_date),
                        data.getStart()));
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
                for (Meta item : data.getMetas()) {
                    FieldConfig config = null;
                    try {
                        config = jsonAdapter.fromJson(item.getWorkflowTypeFieldConfig());
                        if (config.getShow()){
                            Log.d("test", "subscribe: "+item.getDisplayValue());
                            try{
                                String value = (String) item.getDisplayValue();
                                infoList.add(new Information(item.getWorkflowTypeFieldName(), value));
                            }catch (Exception ex){
                                Log.d("test", "subscribe: "+ex.getMessage());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            binding.recInfo.setAdapter(new InformationAdapter(infoList));
            }
        });

        final Observer<WorkflowType> typeObserver = ((WorkflowType data) -> {
            if (null != data) {
                Status currentStatus = null;
                for (Status status : data.getStatus()) {
                    if (status.getId() == item.getWorkflowStateInfo().getId()) {
                        currentStatus = status;
                        break;
                    }
                }
                Log.d("test", "subscribe: "+currentStatus.getSteps());
                if ((currentStatus != null) && (currentStatus.getSteps()!=null)){
                    Collections.sort(currentStatus.getSteps(), (s1, s2) -> {
                        /*For ascending order*/
                        return s1.getOrder() - s2.getOrder();
                    });
                    binding.recSteps.setAdapter(new StepsAdapter(currentStatus.getSteps()));
                } else {
                    binding.hdrImportant.setVisibility(View.GONE);
                }
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableWorkflow().observe(this, workflowObserver);
        workflowDetailViewModel.getObservableType().observe(this, typeObserver);
        workflowDetailViewModel.getObservableError().observe(this, errorObserver);

    }

}
