package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailCommentsBinding;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters.AttachmentsAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters.CommentsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
    private boolean isFromDetails;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates an instance for this fragment. Can be instantiated form any view.
     *
     * @param item          workflow to display its comments.
     * @param isFromDetails whether this fragment was created from {@link WorkflowDetailActivity}.
     *
     * @return instance of this fragment.
     */
    public static CommentsFragment newInstance(WorkflowListItem item, boolean isFromDetails) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.mWorkflowListItem = item;
        fragment.isFromDetails = isFromDetails;
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

        if (getParentFragment() != null && isFromDetails) {
            workflowDetailViewModel = ViewModelProviders
                    .of(getParentFragment())
                    .get(WorkflowDetailViewModel.class);
        }

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        setupSwitch();
        setOnClickListeners();
        setupCommentsRecycler();
        setupAttachmentsRecycler();
        subscribe();
        commentsViewModel.initDetails(token, mWorkflowListItem, loggedUserId, permissionsString);

        return view;
    }

    private void subscribe() {
        commentsViewModel.getObservableToastMessage().observe(this, this::showToastMessage);
        commentsViewModel.getObservableComments().observe(this, this::updateCommentsList);
        commentsViewModel.getObservableHideCommentsEmpty().observe(this, this::hideCommentsListEmpty);
        commentsViewModel.getObservableHideCommentsPermissions().observe(this, this::hideCommentsListPermissions);
        commentsViewModel.getObservableComment().observe(this, this::addNewComment);
        commentsViewModel.getObservableCommentsTabCounter().observe(this, this::updateTabCounter);
        commentsViewModel.getObservableEnableCommentButton()
                .observe(this, this::enableCommentButton);
        commentsViewModel.getObservableNewCommentFile()
                .observe(this, this::addNewAttachment);
        commentsViewModel.getObservableClearAttachments().observe(this, this::clearAttachmentsList);
        commentsViewModel.getObservableOpenDownloadedAttachment()
                .observe(this, this::openDownloadedFile);
        commentsViewModel.getObservableExitEditMode().observe(this, this::exitEditModeUi);
        commentsViewModel.getObservableHideCommentInput().observe(this, this::hideCommentInput);
        commentsViewModel.getObservableHideSwitchPrivatePublic().observe(this, this::hideSwitchPrivatePublic);
        commentsViewModel.getObservableHideEditCommentOption().observe(this, this::hideEditCommentOption);
        commentsViewModel.getObservableHideDeleteCommentOption().observe(this, this::hideDeleteCommentOption);

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
        mCommentsAdapter = new CommentsAdapter(this, getContext(), new ArrayList<>(), commentsViewModel.getLoggedUserId());
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
        mBinding.btnCancelEdit.setOnClickListener(v -> exitEditModeUi(true));
    }

    private void sendComment() {
        mBinding.etComment.setError(null);
        String commentText = mBinding.etComment.getText().toString();

        if (TextUtils.isEmpty(commentText)) {
            mBinding.etComment.setError(getString(R.string.empty_comment));
            return;
        }

        if (commentsViewModel.getActiveEditModeComment() == null) {
            //post new comment
            commentsViewModel.postComment(commentText);
        } else {
            //edit comment
            Comment commentToEdit = commentsViewModel.getActiveEditModeComment();
            commentToEdit.setDescription(commentText);
            commentsViewModel.editComment(commentToEdit);
        }

        hideSoftInputKeyboard();
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
//        mCommentsAdapter.setData(commentList);
        mCommentsAdapter = new CommentsAdapter(this, getContext(), commentList, commentsViewModel.getLoggedUserId());
        mCommentsAdapter.setHideEditOption(!commentsViewModel.hasEditPermissions());
        mCommentsAdapter.setHideDeleteOption(!commentsViewModel.hasDeletePermissions());
        mBinding.rvComments.setAdapter(mCommentsAdapter);
    }

    @UiThread
    private void addNewComment(Comment comment) {
        if (comment != null && mCommentsAdapter != null) {
            mCommentsAdapter.addItem(comment);
            hideCommentsListEmpty(false);
        } else {
            showToastMessage(R.string.error_comment);
        }
        mBinding.etComment.setText(null);
    }

    /**
     * Hides or shows the comments list depending on the data.
     *
     * @param hide boolean that decides if we are showing this list or not.
     */
    @UiThread
    private void hideCommentsListEmpty(boolean hide) {
        mBinding.tvNoPermissions.setVisibility(View.INVISIBLE);

        if (hide) {
            mBinding.rvComments.setVisibility(View.INVISIBLE);
            mBinding.tvNoComments.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvComments.setVisibility(View.VISIBLE);
            mBinding.tvNoComments.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Hides or shows the comments list depending on the permissions.
     *
     * @param hide boolean that decides if we are showing this list or not.
     */
    @UiThread
    private void hideCommentsListPermissions(boolean hide) {
        mBinding.tvNoComments.setVisibility(View.INVISIBLE);

        if (hide) {
            mBinding.rvComments.setVisibility(View.INVISIBLE);
            mBinding.tvNoPermissions.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvComments.setVisibility(View.VISIBLE);
            mBinding.tvNoPermissions.setVisibility(View.INVISIBLE);
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

        //specify multiple MIME types
        intent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.ALLOWED_MIME_TYPES);

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

    //region CommentsFragmentInterface

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

    /**
     * Sends a request to the ViewModel to retrieve the specified file in order to be opened by the
     * device. Should check WRITE/READ external storage permissions before requesting.
     *
     * @param commentFileResponse file object.
     */
    @Override
    public void downloadCommentAttachment(CommentFileResponse commentFileResponse) {
        if (checkExternalStoragePermissions()) {
            commentsViewModel.downloadAttachment(commentFileResponse);
        } else {
            commentsViewModel.setQueuedFile(commentFileResponse);
        }
    }

    @Override
    public void editComment(Comment comment) {
        enterEditModeUi(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentsViewModel.deleteComment(comment);
    }
    //endregion

    /**
     * Verify whether the user has granted permissions to read/write the external storage.
     *
     * @return whether the permissions are granted.
     */
    private boolean checkExternalStoragePermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    CommentsViewModel.REQUEST_EXTERNAL_STORAGE_PERMISSIONS);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        commentsViewModel.handleRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Creates an {@link Intent} chooser the downloaded file. If the device is not suitable to read
     * the file, will display a {@link Toast} message. Uses a {@link FileProvider} to create the
     * file URI, instead of using the {@link Uri#fromFile(File)} method.
     *
     * @param attachmentUiData the file data containing the file to be opened.
     *
     * @see <a href="https://developer.android.com/reference/android/support/v4/content/FileProvider">FileProvider</a>
     */
    @UiThread
    private void openDownloadedFile(AttachmentUiData attachmentUiData) {
        if (attachmentUiData.getFile() == null) return;

        Intent target = new Intent(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(getContext(),
                getContext().getApplicationContext().getPackageName() + ".fileprovider",
                attachmentUiData.getFile());

        target.setDataAndType(fileUri, attachmentUiData.getMimeType());
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target,
                getString(R.string.workflow_detail_comments_fragment_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here
            showToastMessage(R.string.workflow_detail_comments_fragment_cannot_open_file);
        }
    }

    @UiThread
    private void enterEditModeUi(Comment comment) {
        mBinding.lytCommentInput.setBackgroundResource(R.drawable.edit_text_bg_edit_mode);
        mBinding.btnAttach.setBackgroundTintList(
                ContextCompat.getColorStateList(getContext(), R.color.low_yellow));
        mBinding.etComment.setText(comment.getDescription());
        mBinding.etComment.setSelection(comment.getDescription().length());
        mBinding.etComment.requestFocus();
        mBinding.btnCancelEdit.setVisibility(View.VISIBLE);
        mBinding.btnAttach.setVisibility(View.GONE);

        commentsViewModel.setActiveEditModeComment(comment);
    }

    @UiThread
    private void exitEditModeUi(boolean exit) {
        if (!exit) return;

        mBinding.lytCommentInput.setBackgroundResource(R.drawable.edit_text_bg);
        mBinding.btnAttach.setBackgroundTintList(
                ContextCompat.getColorStateList(getContext(), R.color.white));
        mBinding.etComment.setText(null);
        mBinding.etComment.setSelection(0);
        mBinding.etComment.clearFocus();
        mBinding.btnCancelEdit.setVisibility(View.GONE);
        mBinding.btnAttach.setVisibility(View.VISIBLE);

        commentsViewModel.setActiveEditModeComment(null);
    }

    @UiThread
    private void hideCommentInput(boolean hide){
        mBinding.lytCommentInput.setVisibility(hide ? View.GONE : View.VISIBLE);
        mBinding.btnComment.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void hideSwitchPrivatePublic(boolean hide){
        mBinding.switchPrivatePublic.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @UiThread
    private void hideEditCommentOption(boolean hide){
        if (mCommentsAdapter == null) return;

        mCommentsAdapter.setHideEditOption(hide);
    }

    @UiThread
    private void hideDeleteCommentOption(boolean hide){
        if (mCommentsAdapter == null) return;

        mCommentsAdapter.setHideDeleteOption(hide);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}