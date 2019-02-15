package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
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

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TimelineViewModel extends ViewModel {

    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<TimelineUiData> mTimelineLiveData;
    private MutableLiveData<List<Comment>> mSubCommentsLiveData;
    private MutableLiveData<Interaction> mPostCommentsLiveData;
    private MutableLiveData<Comment> mPostSubCommentsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private TimelineRepository mRepository;
    private String mToken;
    private int mWebCount, mWebCompleted;
    private String mStartDate, mEndDate;
    private List<String> mSelectedUsers, mSelectedModules;
    private List<String> mAllUsers, mAllModules;
    private TimelineUiData mTimelineUiData;

    protected TimelineViewModel(TimelineRepository repository) {
        this.mRepository = repository;
    }

    protected void init(String token, String startDate, String endDate, List<String> modules) {
        mToken = token;
        setAllModules(modules);

        mWebCount = mWebCompleted = 0;
        mTimelineUiData = new TimelineUiData();

        showLoading.setValue(true);

        getUsers();
        mWebCount++;

        getWorkflowUsers();
        mWebCount++;

        updateTimeline(startDate, endDate, null, modules);
        mWebCount++;
    }

    private void updateTimeline(String startDate, String endDate, List<String> users,
                                  List<String> modules) {
        setStartDate(startDate);
        setEndDate(endDate);
        setSelectedUsers(users);
        setSelectedModules(modules);

        getTimeline();
    }

    protected void updateTimeline(String startDate, String endDate) {
        updateTimeline(startDate, endDate, getSelectedUsers(), getSelectedModules());
    }

    protected void updateTimelineWithUsers(List<String> users) {
        updateTimeline(getStartDate(), getEndDate(), users, getSelectedModules());
    }

    protected void updateTimelineWithModules(List<String> modules) {
        updateTimeline(getStartDate(), getEndDate(), getSelectedUsers(), modules);
    }

    protected void updateTimeline() {
        updateTimeline(getStartDate(), getEndDate(), getSelectedUsers(), getSelectedModules());
    }

    /**
     * Verifies whether all of the requested services are completed before dismissing the loading
     * view.
     */
    private void updateCompleted() {
        mWebCompleted++;

        if (mWebCompleted >= mWebCount) {
            showLoading.setValue(false);

            mWebCount = mWebCompleted = 0;

            mTimelineLiveData.setValue(mTimelineUiData);
        }
    }

    protected List<WorkflowUser> getAllWorkflowUsers(){
        return mTimelineUiData.getWorkflowUsers();
    }

    protected List<String> getAllUsers() {
        if (mAllUsers == null) mAllUsers = new ArrayList<>();

        return mAllUsers;
    }

    protected void setAllUsers(List<String> allUsers) {
        this.mAllUsers = allUsers;
    }

    protected List<String> getAllModules() {
        if (mAllModules == null) mAllModules = new ArrayList<>();

        return mAllModules;
    }

    protected void setAllModules(List<String> allModules) {
        this.mAllModules = allModules;
    }

    //region Filters
    protected String getStartDate() {
        return Utils.getFormattedDate(mStartDate, "yyyy-MM-dd", Utils.SERVER_DATE_FORMAT);
    }

    private void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    protected String getEndDate() {
        return Utils.getFormattedDate(mEndDate, "yyyy-MM-dd", Utils.SERVER_DATE_FORMAT);
    }

    private void setEndDate(String endDate) {
        this.mEndDate = endDate;
    }

    protected List<String> getSelectedUsers() {
        if (mSelectedUsers == null) mSelectedUsers =  new ArrayList<>();

        return mSelectedUsers;
    }

    private void setSelectedUsers(List<String> selectedUsers) {
        this.mSelectedUsers = selectedUsers;
    }

    protected List<String> getSelectedModules() {
        if (mSelectedModules == null) mSelectedModules = new ArrayList<>();

        return mSelectedModules;
    }

    private void setSelectedModules(List<String> selectedModules) {
        this.mSelectedModules = selectedModules;
    }
    //endregion

    //region Repo Calls
    //region Timeline
    private void getTimeline() {
        Disposable disposable = mRepository
                .getTimeline(mToken, getStartDate(), getEndDate(), getSelectedUsers(),
                        getSelectedModules())
                .subscribe(this::onTimelineSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onTimelineSuccess(TimelineResponse timelineResponse) {
        if (timelineResponse.getList() == null) {
            mErrorLiveData.setValue(R.string.error);
            return;
        }

        mTimelineUiData.setTimelineItems(timelineResponse.getList());

        getTimelineComments();
        mWebCount++;

        updateCompleted();
    }
    //endregion

    //region Users
    private void getUsers() {
        Disposable disposable = mRepository.getUsers(mToken)
                .subscribe(this::onUsersSuccess, this::onFailure);

        mDisposables.add(disposable);

    }

    private void onUsersSuccess(UserResponse userResponse) {
        if (userResponse.getProfiles() == null) {
            mErrorLiveData.setValue(R.string.error);
            return;
        }

        mTimelineUiData.setUsers(userResponse.getProfiles());

        updateCompleted();
    }
    //endregion

    //region Workflow Users
    private void getWorkflowUsers() {
        Disposable disposable = mRepository.getWorkflowUsers(mToken)
                .subscribe(this::onWorkflowUsersSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowUsersSuccess(WorkflowUserResponse workflowUserResponse) {
        if (workflowUserResponse.getUsers() == null) {
            mErrorLiveData.setValue(R.string.error);
            return;
        }

        List<WorkflowUser> users = workflowUserResponse.getUsers();

        for (WorkflowUser user : users) {
            getAllUsers().add(String.valueOf(user.getId()));
        }

        mTimelineUiData.setWorkflowUsers(users);

        updateCompleted();
    }
    //endregion

    //region Comments
    private List<Integer> getTimelineEntityList(){
        List<Integer> timelineEntityList = new ArrayList<>();

        for (TimelineItem item : mTimelineUiData.getTimelineItems()) {
            timelineEntityList.add(item.getEntityId());
        }

        return timelineEntityList;
    }

    protected void getTimelineComments() {
        Disposable disposable = mRepository.getTimelineComments(mToken, getSelectedModules(), getTimelineEntityList())
                .subscribe(this::onTimelineCommentsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onTimelineCommentsSuccess(InteractionResponse interactionResponse) {
        mTimelineUiData.setInteractionComments(interactionResponse.getList());

        updateCompleted();
    }
    //endregion

    //region Sub-Comment
    public void getSubComment(int associate, int level) {
        showLoading.setValue(true);

        Disposable disposable = mRepository.getSubComment(mToken, associate, level)
                .subscribe(this::onSubCommentsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onSubCommentsSuccess(SubCommentsResponse subCommentsResponse) {
        showLoading.setValue(false);
        mSubCommentsLiveData.setValue(subCommentsResponse.getList());
    }
    //endregion

    //region Post Comment
    public void postComment(int interactionId, int entity, String entityType, String description,
                               int author) {
        showLoading.setValue(true);

        Disposable disposable = mRepository
                .postComment(mToken, interactionId, entity, entityType, description, author)
                .subscribe(this::onPostCommentSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onPostCommentSuccess(PostCommentResponse postCommentResponse) {
        showLoading.setValue(false);
        mPostCommentsLiveData.setValue(postCommentResponse.getInteraction());
    }
    //endregion

    //region Post Sub-Comment
    public void postSubComment(int interaction, int associate, String description, int author) {
        showLoading.setValue(true);

        Disposable disposable = mRepository
                .postSubComment(mToken, interaction, associate, description, author)
                .subscribe(this::onPostSubCommentSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onPostSubCommentSuccess(PostSubCommentResponse postSubCommentResponse) {
        showLoading.setValue(false);
        mPostSubCommentsLiveData.setValue(postSubCommentResponse.getComment());
    }
    //endregion

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }
    //endregion

    //region LiveData Declarations
    protected LiveData<TimelineUiData> getObservableTimeline() {
        if (mTimelineLiveData == null) {
            mTimelineLiveData = new MutableLiveData<>();
        }
        return mTimelineLiveData;
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

    protected LiveData<Boolean> getObservableShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
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
    //endregion

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

}