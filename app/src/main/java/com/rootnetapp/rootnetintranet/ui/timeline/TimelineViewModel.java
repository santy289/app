package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Comment;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimelineViewModel extends ViewModel {

    private MutableLiveData<List<TimelineItem>> mTypeLiveData;
    private MutableLiveData<List<User>> mUsersLiveData;
    private MutableLiveData<List<WorkflowUser>> mWorkflowUsersLiveData;
    private MutableLiveData<List<Interaction>> mCommentsLiveData;
    private MutableLiveData<List<Comment>> mSubCommentsLiveData;
    private MutableLiveData<Interaction> mPostCommentsLiveData;
    private MutableLiveData<Comment> mPostSubCommentsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private TimelineRepository repository;

    public TimelineViewModel(TimelineRepository repository) {
        this.repository = repository;
    }

    public void getTimeline(String auth, String start, String end,
                            List<String> users, List<String> modules) {
        repository.getTimeline(auth, start, end, users, modules).subscribe(this::onTimelineSuccess, this::onFailure);
    }

    public void getUsers(String auth) {
        repository.getUsers(auth).subscribe(this::onUsersSuccess, this::onFailure);
    }

    public void getWorkflowUsers(String auth) {
        repository.getWorkflowUsers(auth).subscribe(this::onWorkflowUsersSuccess, this::onFailure);
    }

    public void getComments(String auth) {
        repository.getComments(auth).subscribe(this::onCommentsSuccess, this::onFailure);
    }

    public void getSubComment(String auth, int associate, int level) {
        repository.getSubComment(auth, associate, level).subscribe(this::onSubCommentsSuccess, this::onFailure);
    }

    public void postComment(String auth, int interactionId, int entity,
                            String entityType, String description,
                            int author) {
        repository.postComment(auth, interactionId, entity, entityType, description, author).subscribe(this::onPostCommentSuccess, this::onFailure);
    }

    public void postSubComment(String auth, int interaction,
                               int associate, String description,
                               int author) {
        repository.postSubComment(auth, interaction, associate, description, author).subscribe(this::onPostSubCommentSuccess, this::onFailure);
    }

    private void onTimelineSuccess(TimelineResponse timelineResponse) {
        this.mTypeLiveData.setValue(timelineResponse.getList());
    }

    private void onUsersSuccess(UserResponse userResponse) {
        mUsersLiveData.setValue(userResponse.getProfiles());
    }

    private void onWorkflowUsersSuccess(WorkflowUserResponse workflowUserResponse) {
        mWorkflowUsersLiveData.setValue(workflowUserResponse.getUsers());
    }

    private void onCommentsSuccess(InteractionResponse interactionResponse) {
        mCommentsLiveData.setValue(interactionResponse.getList());
    }

    private void onSubCommentsSuccess(SubCommentsResponse subCommentsResponse) {
        mSubCommentsLiveData.setValue(subCommentsResponse.getList());
    }

    private void onPostCommentSuccess(PostCommentResponse postCommentResponse) {
        mPostCommentsLiveData.setValue(postCommentResponse.getInteraction());
    }

    private void onPostSubCommentSuccess(PostSubCommentResponse postSubCommentResponse) {
        mPostSubCommentsLiveData.setValue(postSubCommentResponse.getComment());
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<List<TimelineItem>> getObservableTimeline() {
        if (mTypeLiveData == null) {
            mTypeLiveData = new MutableLiveData<>();
        }
        return mTypeLiveData;
    }

    protected LiveData<List<User>> getObservableUsers() {
        if (mUsersLiveData == null) {
            mUsersLiveData = new MutableLiveData<>();
        }
        return mUsersLiveData;
    }

    protected LiveData<List<WorkflowUser>> getObservableWorkflowUsers() {
        if (mWorkflowUsersLiveData == null) {
            mWorkflowUsersLiveData = new MutableLiveData<>();
        }
        return mWorkflowUsersLiveData;
    }

    protected LiveData<List<Interaction>> getObservableComments() {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
        }
        return mCommentsLiveData;
    }

    public LiveData<List<Comment>> getObservableSubComments() {
        if (mSubCommentsLiveData == null) {
            mSubCommentsLiveData = new MutableLiveData<>();
        }
        return mSubCommentsLiveData;
    }

    public LiveData<Interaction> getObservablePostComments() {
        if (mPostCommentsLiveData == null) {
            mPostCommentsLiveData = new MutableLiveData<>();
        }
        return mPostCommentsLiveData;
    }

    public LiveData<Comment> getObservablePostSubComments() {
        if (mPostSubCommentsLiveData == null) {
            mPostSubCommentsLiveData = new MutableLiveData<>();
        }
        return mPostSubCommentsLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public void clearSubComments() {
        mSubCommentsLiveData = new MutableLiveData<>();
    }

    public void clearPostComments() {
        mPostCommentsLiveData = new MutableLiveData<>();
    }

    public void clearPostSubComments() {
        mPostSubCommentsLiveData = new MutableLiveData<>();
    }
}