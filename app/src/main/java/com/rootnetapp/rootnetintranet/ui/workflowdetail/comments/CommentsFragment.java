package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailCommentsBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters.AttachmentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters.CommentsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsViewModel.REQUEST_FILE_TO_ATTACH;

public class CommentsFragment extends Fragment implements CommentsFragmentInterface {

    @Inject
    CommentsViewModelFactory commentsViewModelFactory;
    private CommentsViewModel commentsViewModel;
    private FragmentWorkflowDetailCommentsBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    private CommentsAdapter mCommentsAdapter;
    private AttachmentsAdapter mAttachmentsAdapter;

    private WorkflowDetailViewModel workflowDetailViewModel;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(WorkflowListItem item) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_comments, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        commentsViewModel = ViewModelProviders
                .of(this, commentsViewModelFactory)
                .get(CommentsViewModel.class);

        if (getParentFragment() != null) {
            workflowDetailViewModel = ViewModelProviders
                    .of(getParentFragment())
                    .get(WorkflowDetailViewModel.class);
        }

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setupSwitch();
        setOnClickListeners();
        setupCommentsRecycler();
        setupAttachmentsRecycler();
        subscribe();
        commentsViewModel.initDetails(token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        commentsViewModel.getObservableToastMessage().observe(this, this::showToastMessage);
        commentsViewModel.getObservableComments().observe(this, this::updateCommentsList);
        commentsViewModel.getObservableHideComments().observe(this, this::hideCommentsList);
        commentsViewModel.getObservableComment().observe(this, this::addNewComment);
        commentsViewModel.getObservableCommentsTabCounter().observe(this, this::updateTabCounter);
        commentsViewModel.getObservableEnableCommentButton()
                .observe(this, this::enableCommentButton);
        commentsViewModel.getObservableNewCommentFile()
                .observe(this, this::addNewAttachment);
        commentsViewModel.getObservableClearAttachments().observe(this, this::clearAttachmentsList);

        commentsViewModel.showLoading.observe(this, this::showLoading);
    }

    private void setupSwitch() {
        mBinding.switchPrivatePublic.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    updateSwitchUi(isChecked);
                    commentsViewModel.setPrivateComment(isChecked);
                });
    }

    private void setupCommentsRecycler() {
        mCommentsAdapter = new CommentsAdapter(new ArrayList<>());
        mBinding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvComments.setAdapter(mCommentsAdapter);
        mBinding.rvComments.setNestedScrollingEnabled(false);
    }

    private void setupAttachmentsRecycler() {
        mAttachmentsAdapter = new AttachmentsAdapter(this, new ArrayList<>());
        mBinding.rvAttachments.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);
    }

    private void setOnClickListeners() {
        mBinding.btnComment.setOnClickListener(v -> sendComment());
        mBinding.btnAttach.setOnClickListener(v -> showFileChooser());
    }

    private void sendComment() {
        mBinding.etComment.setError(null);
        String comment = mBinding.etComment.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            mBinding.etComment.setError(getString(R.string.empty_comment));
            return;
        }

        commentsViewModel.postComment(comment);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    private void updateCommentsList(List<Comment> commentList) {
        mCommentsAdapter = new CommentsAdapter(commentList);
        mBinding.rvComments.setAdapter(mCommentsAdapter);
    }

    @UiThread
    private void addNewComment(Comment comment) {
        if (comment != null && mCommentsAdapter != null) {
            mCommentsAdapter.addItem(comment);
            hideCommentsList(false);
        } else {
            showToastMessage(R.string.error_comment);
        }
        mBinding.etComment.setText(null);
    }

    /**
     * Shows the comment list.
     *
     * @param hide boolean that decides if we are showing this list or not.
     */
    @UiThread
    private void hideCommentsList(boolean hide) {
        if (hide) {
            mBinding.rvComments.setVisibility(View.GONE);
            mBinding.tvNoComments.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvComments.setVisibility(View.VISIBLE);
            mBinding.tvNoComments.setVisibility(View.GONE);
        }
    }

    @UiThread
    private void updateTabCounter(Integer counter) {
        if (workflowDetailViewModel != null) {
            workflowDetailViewModel.setCommentsTabCounter(counter);
        }
    }

    @UiThread
    private void enableCommentButton(boolean enable) {
        mBinding.btnComment.setEnabled(enable);
    }

    @UiThread
    private void updateSwitchUi(boolean isChecked) {
        String state;
        if (isChecked) {
            state = getString(R.string.private_comment);
            mBinding.switchPrivatePublic.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            state = getString(R.string.public_comment);
            mBinding.switchPrivatePublic.setTextColor(getResources().getColor(R.color.dark_gray));
        }
        mBinding.switchPrivatePublic.setText(state);
    }

    /**
     * If there is no file selected, displays a native file chooser, the user must select which file
     * they wish to upload. In case that the file chooser cannot be opened, shows a Toast message.
     * Otherwise, clears the current selected file and allows the user to select a new file.
     */
    @UiThread
    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(
                    intent,
                    getString(R.string.workflow_detail_comments_fragment_select_file)),
                    REQUEST_FILE_TO_ATTACH);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToastMessage(R.string.workflow_detail_comments_fragment_no_file_manager);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        commentsViewModel.handleFileSelectedResult(getContext(), requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Receives the chosen file from the user and adds to the UI list.
     *
     * @param commentFile the file that the user chose to attach.
     */
    @UiThread
    private void addNewAttachment(CommentFile commentFile) {
        if (commentFile != null && mAttachmentsAdapter != null) {
            mAttachmentsAdapter.addItem(commentFile);
            mBinding.rvAttachments.scrollToPosition(mAttachmentsAdapter.getItemCount() - 1);
        } else {
            showToastMessage(R.string.error);
        }
    }

    /**
     * Removes all of the attachments from the list. This should be called after the comment is
     * sent.
     *
     * @param clear unused param, needed by the LiveData.
     */
    @UiThread
    private void clearAttachmentsList(boolean clear) {
        if (!clear) return;

        mAttachmentsAdapter = new AttachmentsAdapter(this, new ArrayList<>());
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);
    }

    /**
     * Removes the attachment from the UI list and the ViewModel list. This is called after user
     * interaction.
     *
     * @param commentFile the attachment that the user selected.
     */
    @Override
    public void removeAttachment(CommentFile commentFile) {
        mAttachmentsAdapter.removeItem(commentFile);
        commentsViewModel.removeCommentAttachment(commentFile);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}