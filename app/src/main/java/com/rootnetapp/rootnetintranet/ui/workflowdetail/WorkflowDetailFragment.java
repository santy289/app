package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.ApprovalHistoryAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.ApproversAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.CommentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.DocumentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.InformationAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.PeopleInvolvedAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.StepsAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        binding.tvWorkflowproject.setText(item.getTitle());
        binding.detailWorkflowId.setText(item.getWorkflowTypeKey());

        binding.recDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recComments.setLayoutManager(new LinearLayoutManager(getContext()));

        setClickListeners();
        files = new ArrayList<>();
        subscribe();
        showLoading(true);
        workflowDetailViewModel.initDetails(token, item);
        return view;
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

    private void setClickListeners() {
        binding.hdrGraph.setOnClickListener(this::headerClicked);
        binding.hdrImportant.setOnClickListener(this::headerClicked);
        binding.hdrNextstep.setOnClickListener(this::headerClicked);
        binding.hdrInfo.setOnClickListener(this::headerClicked);
        binding.hdrPeopleinvolved.setOnClickListener(this::headerClicked);
        binding.hdrApprovalhistory.setOnClickListener(this::headerClicked);
        binding.btnComment.setOnClickListener(this::comment);
        binding.btnAttachment.setOnClickListener(this::showFileChooser);
        binding.btnUpload.setOnClickListener(this::uploadFiles);
        binding.switchPrivatePublic.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            updateSwitchUi(isChecked);
            workflowDetailViewModel.commentIsPrivate(isChecked);
        }));
        binding.btnApprove.setOnClickListener(this::approveRejectedAction);
    }

    /**
     * Click listener function that listens to clicks in the approve and reject buttons.
     *
     * @param view Button on the layout. This is either approve or reject buttons.
     */
    private void approveRejectedAction(View view) {
        workflowDetailViewModel.handleApproveRejectAction(view.getId(), approveSpinnerItemSelection);
    }

    private void updateSwitchUi(boolean isChecked) {
        String state;
        if (isChecked) {
            state = getString(R.string.private_comment);
            binding.switchPrivatePublic.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            state = getString(R.string.public_comment);
            binding.switchPrivatePublic.setTextColor(getResources().getColor(R.color.dark_gray));
        }
        binding.switchPrivatePublic.setText(state);
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
                if (binding.peopleinvolvedLayout.lytPeopleinvolved.getVisibility() == View.GONE) {
                    binding.btnArrow5.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow5.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.peopleinvolvedLayout.lytPeopleinvolved.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow5.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow5.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.peopleinvolvedLayout.lytPeopleinvolved.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.hdr_approvalhistory: {
                if (binding.lytHistory.getVisibility() == View.GONE) {
                    binding.btnArrow6.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    binding.btnArrow6.setColorFilter(ContextCompat.getColor(getContext(), R.color.arrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytHistory.setVisibility(View.VISIBLE);
                } else {
                    binding.btnArrow6.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    binding.btnArrow6.setColorFilter(ContextCompat.getColor(getContext(), R.color.transparentArrow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.lytHistory.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void setWorkflowIsOpen(boolean open) {
        if (open) {
            binding.txtOpenClosed.setText(getString(R.string.open));
            binding.txtOpenClosed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
        } else {
            binding.txtOpenClosed.setText(getString(R.string.closed));
            binding.txtOpenClosed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));

        }
    }

    private void updateApprovalHistoryList(List<ApproverHistory> approverHistoryList) {
        binding.recApprovalhistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recApprovalhistory.setAdapter(new ApprovalHistoryAdapter(approverHistoryList));
    }

    /**
     * Shows the approval history list.
     * @param hide
     *  Boolean that decides if we are showing this list or not.
     */
    private void hideHistoryApprovalList(boolean hide) {
        if (hide) {
            binding.recApprovalhistory.setVisibility(View.GONE);
            binding.noHistory.setVisibility(View.VISIBLE);
        } else {
            binding.recApprovalhistory.setVisibility(View.VISIBLE);
            binding.noHistory.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the profile involve section. Profiles will include the ones coming from the workflow type
     * configuration, and also profiles coming from specific status configuration that are coming
     * from the current workflow configurations.
     *
     * @param currentApprovers List of current approvers to be displayed.
     */
    private void updateCurrentApproversList(List<Approver> currentApprovers) {
        binding.recApprovers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recApprovers.setAdapter(new ApproversAdapter(currentApprovers));
    }

    /**
     * Updates the profiles involved.
     *
     * @param profiles List of profiles to display in People Involved recyclerView.
     */
    private void updateProfilesInvolved(List<ProfileInvolved> profiles) {
        binding.peopleinvolvedLayout.recPeopleinvolved.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.peopleinvolvedLayout.recPeopleinvolved.setAdapter(new PeopleInvolvedAdapter(profiles));
    }

    /**
     * Updates the global approver list. Profiles will include the ones coming from a workflow
     * object in the View Model.
     *
     * @param profiles List of profiles to display in People Involved recyclerView.
     */
    private void updateGlobalApproverList(List<ProfileInvolved> profiles) {
        binding.peopleinvolvedLayout.recGlobalApprovers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.peopleinvolvedLayout.recGlobalApprovers.setAdapter(new PeopleInvolvedAdapter(profiles));
    }

    /**
     * Updates specific approver list. This is coming from the Worklow object in the View Model.
     * @param profiles
     */
    private void updateSpecificApproverList(List<ProfileInvolved> profiles) {
        binding.peopleinvolvedLayout.recSpecificApprovers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.peopleinvolvedLayout.recSpecificApprovers.setAdapter(new PeopleInvolvedAdapter(profiles));
    }

    /**
     * Hides list of people involved and shows a text message instead.
     *
     * @param hide Action to take.
     */
    private void hideProfilesInvolvedList(boolean hide) {
        if (hide) {
            binding.peopleinvolvedLayout.lytPeopleinvolved.setVisibility(View.GONE);
            binding.peopleinvolvedLayout.noPeopleInvolved.setVisibility(View.VISIBLE);
        } else {
            binding.peopleinvolvedLayout.lytPeopleinvolved.setVisibility(View.VISIBLE);
            binding.peopleinvolvedLayout.noPeopleInvolved.setVisibility(View.GONE);
        }
    }

    /**
     * Hides global text title and global recycler view.
     *
     * @param hide
     */
    private void hideGlobalApprovers(boolean hide) {
        if (hide) {
            binding.peopleinvolvedLayout.recGlobalApprovers.setVisibility(View.GONE);
            binding.peopleinvolvedLayout.txtGlobalApprovers.setVisibility(View.GONE);
        } else {
            binding.peopleinvolvedLayout.recGlobalApprovers.setVisibility(View.VISIBLE);
            binding.peopleinvolvedLayout.txtGlobalApprovers.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides specific approvers title and recycler view.
     *
     * @param hide
     */
    private void hideSpecificApprovers(boolean hide) {
        if (hide) {
            binding.peopleinvolvedLayout.recSpecificApprovers.setVisibility(View.GONE);
            binding.peopleinvolvedLayout.txtSpecificApprovers.setVisibility(View.GONE);
        } else {
            binding.peopleinvolvedLayout.recSpecificApprovers.setVisibility(View.VISIBLE);
            binding.peopleinvolvedLayout.txtSpecificApprovers.setVisibility(View.VISIBLE);
        }
    }



    /**
     * Hides the spinner in the case that we don't have any next status. When the spinner is hidden.
     * It will replaced it with a message text view.
     *
     * @param hide Hides or shows the spinner view.
     */
    private void hideApproveSpinnerOnEmptyData(boolean hide) {
        if (hide) {
            binding.detailNoMoreStatus.setVisibility(View.VISIBLE);
            binding.detailApproveSpinnerBackground.setVisibility(View.GONE);
            binding.btnApprove.setVisibility(View.GONE);
            binding.btnReject.setVisibility(View.GONE);
        } else {
            binding.detailNoMoreStatus.setVisibility(View.GONE);
            binding.detailApproveSpinnerBackground.setVisibility(View.VISIBLE);
            binding.btnApprove.setVisibility(View.VISIBLE);
            binding.btnReject.setVisibility(View.VISIBLE);
        }

    }

    private void hideApproverListOnEmptyData(boolean hide) {
        if (hide) {
            binding.txtApprovers.setVisibility(View.GONE);
            binding.recApprovers.setVisibility(View.GONE);
            binding.detailMassApproval.setVisibility(View.GONE);
        } else {
            binding.txtApprovers.setVisibility(View.VISIBLE);
            binding.recApprovers.setVisibility(View.VISIBLE);
            binding.detailMassApproval.setVisibility(View.VISIBLE);
        }

    }

    private int approveSpinnerItemSelection;
    private void updateApproveSpinner(List<String> nextStatuses) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_selectable_list_item,
                nextStatuses
        );
        binding.detailApproveSpinner.setAdapter(adapter);

        binding.detailApproveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                approveSpinnerItemSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showImportantInfoSection(boolean show) {
        if (show) {
            binding.hdrImportant.setVisibility(View.VISIBLE);
        } else {
            binding.hdrImportant.setVisibility(View.GONE);
        }
    }

    private void loadImportantInfoSection(List<Step> steps) {
        binding.recSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recSteps.setAdapter(
                new StepsAdapter(steps)
        );
    }

    private void showTemplateDocumentsUi(boolean show) {
        if (show) {
            binding.lytAttach.setVisibility(View.VISIBLE);
            binding.recDocuments.setVisibility(View.VISIBLE);
            binding.tvFileuploaded.setVisibility(View.VISIBLE);
            binding.lytDocumentsheader.setVisibility(View.VISIBLE);
            binding.lytDocumentstitle.setVisibility(View.VISIBLE);
        } else {
            binding.lytAttach.setVisibility(View.GONE);
            binding.recDocuments.setVisibility(View.GONE);
            binding.tvFileuploaded.setVisibility(View.GONE);
            binding.lytDocumentsheader.setVisibility(View.GONE);
            binding.lytDocumentstitle.setVisibility(View.GONE);
        }
    }

    private void setTemplateTitleWith(String name) {
        String title = getString(R.string.template) +
                " " +
                name;
        binding.tvTemplatetitle.setText(title);
    }

    private void setDocumentsView(List<DocumentsFile> documents) {
        DocumentsAdapter documentsAdapter = new DocumentsAdapter(
                workflowDetailViewModel.getPresets(),
                documents
        );
        binding.recDocuments.setAdapter(documentsAdapter);
    }



    private void comment(View view) {
        binding.inputComment.setError(null);
        String comment = binding.inputComment.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            binding.inputComment.setError(getString(R.string.empty_comment));
            return;
        }
        workflowDetailViewModel.postComment(comment, files);
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

    private void updateInformationListUi(List<Information> informationList) {
        binding.recInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recInfo.setAdapter(new InformationAdapter(informationList));
    }


    private void subscribe() {
        final Observer<List<Comment>> commentsObserver = ((List<Comment> data) -> {
            showLoading(false);
            if (null != data) {
                commentsAdapter = new CommentsAdapter(data);
                binding.recComments.setAdapter(commentsAdapter);
            }else{
                commentsAdapter = new CommentsAdapter(new ArrayList<>());
                binding.recComments.setAdapter(commentsAdapter);
            }
        });

        final Observer<Comment> commentObserver = ((Comment data) -> {
            if ((null != data) && (null != commentsAdapter)) {
                commentsAdapter.comments.add(0, data);
                commentsAdapter.notifyItemChanged(0);
            } else {
                Toast.makeText(getContext(), getString(R.string.error_comment), Toast.LENGTH_LONG).show();
            }
            binding.inputComment.setText("");
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
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableComments().observe(this, commentsObserver);
        workflowDetailViewModel.getObservableComment().observe(this, commentObserver);
        workflowDetailViewModel.getObservableAttach().observe(this, attachObserver);
        workflowDetailViewModel.getObservableError().observe(this, errorObserver);

        workflowDetailViewModel.showLoading.observe(this, this::showLoading);
        workflowDetailViewModel.setCommentHeaderCounter.observe(this, this::updateHeaderCommentCounter);
        workflowDetailViewModel.showImportantInfoSection.observe(this, this::showImportantInfoSection);
        workflowDetailViewModel.loadImportantInfoSection.observe(this, this::loadImportantInfoSection);
        workflowDetailViewModel.showTemplateDocumentsUi.observe(this, this::showTemplateDocumentsUi);
        workflowDetailViewModel.setTemplateTitleWith.observe(this, this::setTemplateTitleWith);
        workflowDetailViewModel.setDocumentsView.observe(this, this::setDocumentsView);
        workflowDetailViewModel.updateCurrentApproversList.observe(this, this::updateCurrentApproversList);
        workflowDetailViewModel.updateApproveSpinner.observe(this, this::updateApproveSpinner);
        workflowDetailViewModel.hideApproverListOnEmptyData.observe(this, this::hideApproverListOnEmptyData);
        workflowDetailViewModel.hideApproveSpinnerOnEmptyData.observe(this, this::hideApproveSpinnerOnEmptyData);
        workflowDetailViewModel.updateProfilesInvolved.observe(this, this::updateProfilesInvolved);
        workflowDetailViewModel.hideProfilesInvolvedList.observe(this, this::hideProfilesInvolvedList);
        workflowDetailViewModel.hideGlobalApprovers.observe(this, this::hideGlobalApprovers);
        workflowDetailViewModel.hideSpecificApprovers.observe(this, this::hideSpecificApprovers);
        workflowDetailViewModel.updateGlobalApproverList.observe(this, this::updateGlobalApproverList);
        workflowDetailViewModel.updateSpecificApproverList.observe(this, this::updateSpecificApproverList);
        workflowDetailViewModel.updateApprovalHistoryList.observe(this, this::updateApprovalHistoryList);
        workflowDetailViewModel.hideHistoryApprovalList.observe(this, this::hideHistoryApprovalList);
        workflowDetailViewModel.setWorkflowIsOpen.observe(this, this::setWorkflowIsOpen);
        workflowDetailViewModel.updateInformationListUi.observe(this, this::updateInformationListUi);
    }

    private void updateHeaderCommentCounter(String count) {
        binding.detailMessageText.setText(count);
    }

}