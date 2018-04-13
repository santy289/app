package com.rootnetapp.rootnetintranet.ui.timeline;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.timeline.Comment;
import com.rootnetapp.rootnetintranet.models.responses.timeline.Interaction;
import com.rootnetapp.rootnetintranet.models.responses.timeline.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.ItemComments;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;

import java.util.List;

/**
 * Created by root on 10/04/18.
 */

public class TimelineViewModel extends ViewModel {

    private MutableLiveData<List<TimelineItem>> mTypeLiveData;
    private MutableLiveData<List<User>> mUsersLiveData;
    private MutableLiveData<List<ItemComments>> mCommentsLiveData;
    private MutableLiveData<List<Comment>> mSubCommentsLiveData;
    private MutableLiveData<Interaction> mPostCommentsLiveData;
    private MutableLiveData<Comment> mPostSubCommentsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private TimelineRepository repository;

    public TimelineViewModel(TimelineRepository repository) {
        this.repository = repository;
    }

    public void getTimeline(String auth, String start, String end) {
        repository.getTimeline(auth, start, end).subscribe(this::onTimelineSuccess, this::onFailure);
    }

    public void getUsers(String auth) {
        repository.getUsers(auth).subscribe(this::onUsersSuccess, this::onFailure);
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
        this.mTypeLiveData.setValue(timelineResponse.getItems());
    }

    private void onUsersSuccess(UserResponse userResponse) {
        mUsersLiveData.setValue(userResponse.getProfiles());
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

    protected LiveData<List<ItemComments>> getObservableComments() {
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