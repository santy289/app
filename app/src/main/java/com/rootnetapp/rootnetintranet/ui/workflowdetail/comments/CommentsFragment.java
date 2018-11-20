package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailCommentsBinding;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.CommentsAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CommentsFragment extends Fragment {

    @Inject
    CommentsViewModelFactory commentsViewModelFactory;
    CommentsViewModel commentsViewModel;
    private FragmentWorkflowDetailCommentsBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private String mToken;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(WorkflowListItem item) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.mWorkflowListItem = item;
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
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_comments, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        commentsViewModel = ViewModelProviders
                .of(this, commentsViewModelFactory)
                .get(CommentsViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        mToken = "Bearer " + prefs.getString("token", "");

        subscribe();
        commentsViewModel.initDetails(mToken, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        commentsViewModel.getObservableError().observe(this, errorObserver);
        commentsViewModel.getObservableComments().observe(this, this::updateCommentsList);
        commentsViewModel.getObservableHideComments().observe(this, this::hideCommentsList);
        commentsViewModel.getObservableCommentHeaderCounter().observe(this, this::updateTabCounter);
//
        commentsViewModel.showLoading.observe(this, this::showLoading);
    }

    private void setupSwitch(){
        mBinding.switchPrivatePublic.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            commentsViewModel.setPrivateComment(!isChecked);
        }));
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
        mBinding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvComments.setAdapter(new CommentsAdapter(commentList));
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
    private void updateTabCounter(String counter) {
        String title = getString(R.string.workflow_detail_comments_fragment_title);
        title += " " + counter;
        //todo update TabLayout tab title
    }
}