package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.templates.Templates;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.ApprovalAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.ApproversAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.CommentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.DocumentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.InformationAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.PeopleInvolvedAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.StepsAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class WorkflowDetailFragment extends Fragment {

    @Inject
    WorkflowDetailViewModelFactory workflowViewModelFactory;
    WorkflowDetailViewModel workflowDetailViewModel;
    private FragmentWorkflowDetailBinding binding;
    private MainActivityInterface mainActivityInterface;
    private WorkflowListItem item;
    private List<Preset> presets;
    private CommentsAdapter commentsAdapter = null;
    private static final int FILE_SELECT_CODE = 555;
    //private String encodedFile = null;
    private CommentFile fileRequest = null;
    private List<CommentFile> files;
    private DocumentsAdapter documentsAdapter = null;
    private String token;

    public WorkflowDetailFragment() {
        // Required empty public constructor
    }

    public static WorkflowDetailFragment newInstance(WorkflowListItem item, MainActivityInterface mainActivityInterface) {
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
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        //Utils.showLoading(getContext());
        binding.tvWorkflowproject.setText(item.getTitle());
//        binding.tvWorkflowid.setText(binding.tvWorkflowid.getText()+" "+item.getWorkflowTypeKey());
        binding.detailWorkflowId.setText(item.getWorkflowTypeKey());
        binding.recSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recApprovers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recInfo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recPeopleinvolved.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recApprovalhistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recComments.setLayoutManager(new LinearLayoutManager(getContext()));
        //todo SOLO testing hasta tener el backend live
        binding.recApprovers.setAdapter(new ApproversAdapter());
        binding.recPeopleinvolved.setAdapter(new PeopleInvolvedAdapter());
        binding.recApprovalhistory.setAdapter(new ApprovalAdapter());
        //fin testing
        files = new ArrayList<>();
        subscribe();
        binding.hdrGraph.setOnClickListener(this::headerClicked);
        binding.hdrImportant.setOnClickListener(this::headerClicked);
        binding.hdrNextstep.setOnClickListener(this::headerClicked);
        binding.hdrInfo.setOnClickListener(this::headerClicked);
        binding.hdrPeopleinvolved.setOnClickListener(this::headerClicked);
        binding.hdrApprovalhistory.setOnClickListener(this::headerClicked);
        binding.btnComment.setOnClickListener(this::comment);
        binding.btnAttachment.setOnClickListener(this::showFileChooser);
        binding.btnUpload.setOnClickListener(this::uploadFiles);
        workflowDetailViewModel.getWorkflow(token, item.getWorkflowId());
        workflowDetailViewModel.getWorkflowType(token, item.getWorkflowTypeId());
        workflowDetailViewModel.getComments(token, item.getWorkflowId());
        return view;
    }

    private void headerClicked(View view) {
        switch (view.getId()) {
            case R.id.hdr_graph: {
                if (binding.lytGraph.getVisibility() == View.GONE) {
                    binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytGraph.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytGraph.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_important: {
                if (binding.lytImportant.getVisibility() == View.GONE) {
                    binding.btnArrow2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow2.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytImportant.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow2.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytImportant.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_nextstep: {
                if (binding.lytNextstep.getVisibility() == View.GONE) {
                    binding.btnArrow3.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow3.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytNextstep.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow3.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow3.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytNextstep.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_info: {
                if (binding.recInfo.getVisibility() == View.GONE) {
                    binding.btnArrow4.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow4.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.recInfo.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow4.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow4.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.recInfo.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_peopleinvolved: {
                if (binding.lytPeopleinvolved.getVisibility() == View.GONE) {
                    binding.btnArrow5.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow5.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytPeopleinvolved.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow5.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow5.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytPeopleinvolved.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_approvalhistory: {
                if (binding.recApprovalhistory.getVisibility() == View.GONE) {
                    binding.btnArrow6.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow6.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.recApprovalhistory.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow6.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow6.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.recApprovalhistory.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void comment(View view) {

        String comment = binding.inputComment.getText().toString();
        binding.inputComment.setError(null);
        if (TextUtils.isEmpty(comment)) {
            binding.inputComment.setError(getString(R.string.empty_comment));
        } else {
            Utils.showLoading(getContext());
            workflowDetailViewModel.postComment(token, item.getWorkflowId(), comment, files);
        }
    }

    private void uploadFiles(View view) {

        if ((fileRequest != null) && (documentsAdapter != null)) {
            List<WorkflowPresetsRequest> request = new ArrayList<>();
            List<Integer> presets = new ArrayList<>();
            int i = 0;
            for (Boolean isSelected : documentsAdapter.isSelected) {
                if (isSelected) {
                    presets.add(documentsAdapter.totalDocuments.get(i).getId());
                }
                i++;
            }
            if(presets.isEmpty()){
                Toast.makeText(getContext(), getString(R.string.select_preset),
                        Toast.LENGTH_SHORT).show();
            }else{
                request.add(new WorkflowPresetsRequest(item.getWorkflowId(), presets));
                Utils.showLoading(getContext());
                workflowDetailViewModel.attachFile(token, request, fileRequest);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.select_file),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void showFileChooser(View view) {

        if (fileRequest == null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getContext(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            fileRequest = null;
            binding.btnAttachment.setText(R.string.attach);
            binding.tvFileuploaded.setText(binding.tvFileuploaded.getText());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        File file = new File(data.getData().toString());
                        byte[] bytes = Utils.fileToByte(file);
                        String filename = file.getName();
                        //todo funcion actualizar texto
                        binding.tvFileuploaded.setText(binding.tvFileuploaded.getText() + " " + filename);
                        binding.btnAttachment.setText(R.string.remove_file);
                        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
                        String fileType = Utils.getMimeType(data.getData(),getContext());
                        fileRequest = new CommentFile(encodedFile, fileType, filename, (int)file.length());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        if (config.getShow()) {
                            try {
                                String value = (String) item.getDisplayValue();
                                infoList.add(new Information(item.getWorkflowTypeFieldName(), value));
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                    if (status.getId() == item.getCurrentStatus()) {
                        currentStatus = status;
                        break;
                    }
                }
                if ((currentStatus != null) && (currentStatus.getSteps() != null)) {
                    Collections.sort(currentStatus.getSteps(), (s1, s2) -> {
                        /*For ascending order*/
                        return s1.getOrder() - s2.getOrder();
                    });
                    binding.recSteps.setAdapter(new StepsAdapter(currentStatus.getSteps()));
                } else {
                    binding.hdrImportant.setVisibility(View.GONE);
                }
                if(data.getTemplateId()!= 0){
                    workflowDetailViewModel.getTemplate(token, data.getTemplateId());
                }else{
                    // lyt_documentstitle
                    binding.lytAttach.setVisibility(View.GONE);
                    binding.recDocuments.setVisibility(View.GONE);
                    binding.tvFileuploaded.setVisibility(View.GONE);
                    binding.lytDocumentsheader.setVisibility(View.GONE);
                    binding.lytDocumentstitle.setVisibility(View.GONE);
                }
                presets = data.getPresets();
            }
        });

        final Observer<Templates> templateObserver = ((Templates data) -> {
            if (null != data) {
                binding.tvTemplatetitle.setText(getString(R.string.template) + " " + data.getName());
                workflowDetailViewModel.getFiles(token, item.getWorkflowId());
            }
        });

        final Observer<List<DocumentsFile>> filesObserver = ((List<DocumentsFile> data) -> {
            if (null != data) {
                documentsAdapter = new DocumentsAdapter(presets, data);
                binding.recDocuments.setAdapter(documentsAdapter);
            }
        });

        final Observer<List<Comment>> commentsObserver = ((List<Comment> data) -> {
            if (null != data) {
                commentsAdapter = new CommentsAdapter(data);
                binding.recComments.setAdapter(commentsAdapter);
            }else{
                commentsAdapter = new CommentsAdapter(new ArrayList<>());
                binding.recComments.setAdapter(commentsAdapter);
            }
        });

        final Observer<Comment> commentObserver = ((Comment data) -> {
            Utils.hideLoading();
            if ((null != data) && (null != commentsAdapter)) {
                commentsAdapter.comments.add(0, data);
                commentsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), getString(R.string.error_comment), Toast.LENGTH_LONG).show();
            }
        });

        final Observer<Boolean> attachObserver = ((Boolean data) -> {
            Utils.hideLoading();
            if ((null != data) && (data)) {
                workflowDetailViewModel.getFiles(token, item.getWorkflowId());
            } else {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Utils.hideLoading();
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableWorkflow().observe(this, workflowObserver);
        workflowDetailViewModel.getObservableType().observe(this, typeObserver);
        workflowDetailViewModel.getObservableTemplate().observe(this, templateObserver);
        workflowDetailViewModel.getObservableFiles().observe(this, filesObserver);
        workflowDetailViewModel.getObservableComments().observe(this, commentsObserver);
        workflowDetailViewModel.getObservableComment().observe(this, commentObserver);
        workflowDetailViewModel.getObservableAttach().observe(this, attachObserver);
        workflowDetailViewModel.getObservableError().observe(this, errorObserver);
    }

}