package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Comment;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostInteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostLikeDislike;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.TIMELINE_CRUD;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.TIMELINE_CRUD_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.TIMELINE_VIEW;

public class TimelineViewModel extends ViewModel {

    protected static final int MONTH_AGO_DAYS = -30;
    protected static final int WEEK_AGO_DAYS = -7;
    protected static final int DAY_AGO_DAYS = -1;

    protected static final String USER_ALL = "all";

    protected static final String MODULE_ALL = "all";
    protected static final String MODULE_WORKFLOWS = "intranet_workflow_reports";
    protected static final String MODULE_WORKFLOW_APPROVALS = "intranet_workflow_status_approve";
    protected static final String MODULE_WORKFLOW_FILES = "intranet_workflow_file_record";
    protected static final String MODULE_WORKFLOW_COMMENTS = "intranet_workflow_comment";

    private static final int TIMELINE_PAGE_LIMIT = 20;
    private static final String THUMB_ACTION_UP = "up";
    private static final String THUMB_ACTION_DOWN = "down";

    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<TimelineUiData> mTimelineLiveData;
    private MutableLiveData<List<Comment>> mSubCommentsLiveData;
    private MutableLiveData<Interaction> mPostInteractionLiveData;
    private MutableLiveData<Comment> mPostSubCommentsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> mHideMoreButtonLiveData;
    private MutableLiveData<Boolean> mHideTimelineListEmptyLiveData;
    private MutableLiveData<Boolean> mHideTimelineListPermissionsLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private TimelineRepository mRepository;
    private String mToken;
    private int mWebCount, mWebCompleted;
    private int mCurrentPage;
    private String mStartDate, mEndDate;
    private List<String> mSelectedUsers, mSelectedModules;
    private List<String> mAllUsers, mAllModules;
    private TimelineUiData mTimelineUiData;
    private boolean hasViewPermissions;
    private boolean hasInteractionsPermissions;
    private boolean hasInteractionsOwnPermissions;
    private int mUserId;

    protected TimelineViewModel(TimelineRepository repository) {
        this.mRepository = repository;
        mCurrentPage = 1;

        List<String> modules = new ArrayList<>();
        modules.add(MODULE_ALL);
        modules.add(MODULE_WORKFLOWS);
        modules.add(MODULE_WORKFLOW_APPROVALS);
        modules.add(MODULE_WORKFLOW_FILES);
        modules.add(MODULE_WORKFLOW_COMMENTS);
        setAllModules(modules);
    }

    protected void init(String token, String startDate, String endDate, String userId,
                        String userPermissions) {
        mToken = token;
        mUserId = userId == null ? 0 : Integer.parseInt(userId);

        checkPermissions(userPermissions);

        if (!hasViewPermissions) return;

        mWebCount = mWebCompleted = 0;
        mTimelineUiData = new TimelineUiData();

        updateTimeline(startDate, endDate, getAllUsers(), getAllModules());

        getUsers();

        getWorkflowUsers();

        //fixme temporary setup of Workflows as the initial tab (uncomment following line)
//        showLoading.setValue(false); //do not show loading on init
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link TimelineFragment}.
     * Hide the UI related to the unauthorized actions.
     *
     * @param permissionsString users permissions.
     */
    private void checkPermissions(String permissionsString) {
        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        hasViewPermissions = permissionsUtils.hasPermission(TIMELINE_VIEW);
        hasInteractionsPermissions = permissionsUtils.hasPermission(TIMELINE_CRUD);
        hasInteractionsOwnPermissions = permissionsUtils.hasPermission(TIMELINE_CRUD_OWN);

        mHideTimelineListPermissionsLiveData.setValue(!hasViewPermissions);
    }

    protected boolean hasViewPermissions() {
        return hasViewPermissions;
    }

    private void updateTimeline(String startDate, String endDate, List<String> users,
                                List<String> modules) {
        if (!hasViewPermissions) return;

        mWebCount = mWebCompleted = 0;
        showLoading.setValue(true);

        setStartDate(startDate);
        setEndDate(endDate);
        setSelectedUsers(users);
        setSelectedModules(modules);

        resetCurrentPage();

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

            getAllUsers().add(USER_ALL); //"All" filter
            for (WorkflowUser workflowUser : mTimelineUiData.getWorkflowUsers()) {
                for (User user : mTimelineUiData.getUsers()) {
                    if (workflowUser.getId() == user.getId()) {
                        workflowUser.setUserId(user.getUserId());
                        break;
                    }
                }

                getAllUsers().add(String.valueOf(workflowUser.getUserId()));
            }

            mTimelineLiveData.setValue(mTimelineUiData);
        }
    }

    protected List<WorkflowUser> getAllWorkflowUsers() {
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
        return mStartDate;
    }

    private void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    protected String getEndDate() {
        Date date = Utils.getDateFromString(mEndDate, Utils.SERVER_DATE_FORMAT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        date = calendar.getTime();

        return Utils.getFormattedDate(date, Utils.SERVER_DATE_FORMAT);
    }

    private void setEndDate(String endDate) {
        this.mEndDate = endDate;
    }

    protected List<String> getSelectedUsers() {
        if (mSelectedUsers == null) mSelectedUsers = new ArrayList<>();

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

    /**
     * Resets the current page to its initial value. Called when the dashboard filters change.
     */
    protected void resetCurrentPage() {
        this.mCurrentPage = 1;
    }

    /**
     * Increments the current page by one. Called when the user requests more workflows.
     */
    protected void incrementCurrentPage() {
        mCurrentPage++;
    }

    protected void getTimeline() {
        mWebCount++;

        showLoading.setValue(true);

        Disposable disposable = mRepository
                .getTimeline(mToken, getStartDate(), getEndDate(), mCurrentPage,
                        TIMELINE_PAGE_LIMIT, getSelectedUsers(), getSelectedModules())
                .subscribe(this::onTimelineSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onTimelineSuccess(TimelineResponse timelineResponse) {
        if (timelineResponse.getList() == null) {
            mErrorLiveData.setValue(R.string.error);
            mHideMoreButtonLiveData.setValue(false);
            return;
        }

        boolean isEmpty = timelineResponse.getList().isEmpty();
        mHideTimelineListEmptyLiveData
                .setValue(mCurrentPage == 1 && timelineResponse.getList().isEmpty());
        mHideMoreButtonLiveData.setValue(isEmpty || timelineResponse.getPager().getIsLastPage());
        mTimelineUiData.setTimelineItems(timelineResponse.getList());

        for (TimelineItem item : mTimelineUiData.getTimelineItems()) {
            if (item.getAuthor() == mUserId) {
                item.setShowCommentInput(hasInteractionsOwnPermissions);
            } else {
                item.setShowCommentInput(hasInteractionsPermissions);
            }
        }

        getTimelineInteractions();

        updateCompleted();
    }
    //endregion

    //region Users
    private void getUsers() {
        mWebCount++;

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
        mWebCount++;

        Disposable disposable = mRepository.getWorkflowUsers(mToken) //todo proper clientId
                .subscribe(this::onWorkflowUsersSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowUsersSuccess(WorkflowUserResponse workflowUserResponse) {
        if (workflowUserResponse.getUsers() == null) {
            mErrorLiveData.setValue(R.string.error);
            return;
        }

        mTimelineUiData.setWorkflowUsers(workflowUserResponse.getUsers());

        updateCompleted();
    }
    //endregion

    //region Comments
    private List<Integer> getTimelineEntityList() {
        List<Integer> timelineEntityList = new ArrayList<>();

        for (TimelineItem item : mTimelineUiData.getTimelineItems()) {
            timelineEntityList.add(item.getEntityId());
        }

        return timelineEntityList;
    }

    protected void getTimelineInteractions() {
        mWebCount++;

        Disposable disposable = mRepository
                .getTimelineInteractions(mToken, getSelectedModules(), getTimelineEntityList())
                .subscribe(this::onTimelineInteractionsSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onTimelineInteractionsSuccess(InteractionResponse interactionResponse) {
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
    public void postComment(@Nullable Integer interactionId, int entity, String entityType, String description,
                            int author) {
        showLoading.setValue(true);

        Disposable disposable = mRepository
                .postComment(mToken, interactionId, entity, entityType, description, author)
                .subscribe(this::onPostCommentSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onPostCommentSuccess(PostInteractionResponse postInteractionResponse) {
        showLoading.setValue(false);
        mPostInteractionLiveData.setValue(postInteractionResponse.getInteraction());
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

    //region Post Like/Dislike
    protected void postLike(@Nullable Integer interactionId, TimelineItem timelineItem, int authorId) {
        postLikeDislike(interactionId, timelineItem, authorId, THUMB_ACTION_UP);
    }

    protected void postDislike(@Nullable Integer interactionId, TimelineItem timelineItem, int authorId) {
        postLikeDislike(interactionId, timelineItem, authorId, THUMB_ACTION_DOWN);
    }

    private void postLikeDislike(@Nullable Integer interactionId, TimelineItem timelineItem, int authorId,
                                 String thumbAction) {
        boolean isOwnItem = timelineItem.getAuthor() == mUserId;
        if (!hasInteractionsPermissions && (!hasInteractionsOwnPermissions || !isOwnItem)) {
            return;
        }

        showLoading.setValue(true);

        PostLikeDislike request = new PostLikeDislike();
        request.setInteractionId(interactionId);
        request.setEntity(timelineItem.getEntityId());
        request.setEntityType(timelineItem.getEntity());
        request.setAuthor(authorId);
        request.setThumb(thumbAction);

        Disposable disposable;
        if (interactionId == null) {
            disposable = mRepository
                    .postLikeDislike(mToken, request)
                    .subscribe(this::postLikeDislikeSuccess, this::onFailure);
        } else {
            disposable = mRepository
                    .postLikeDislike(mToken, interactionId, request)
                    .subscribe(this::postLikeDislikeSuccess, this::onFailure);
        }

        mDisposables.add(disposable);
    }

    private void postLikeDislikeSuccess(PostInteractionResponse interactionResponse) {
        showLoading.setValue(false);
        mPostInteractionLiveData.setValue(interactionResponse.getInteraction());
    }
    //endregion

    private void onFailure(Throwable throwable) {
        mWebCount = mWebCompleted = 0;

        showLoading.setValue(false);
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
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

    protected LiveData<Interaction> getObservablePostInteraction() {
        if (mPostInteractionLiveData == null) {
            mPostInteractionLiveData = new MutableLiveData<>();
        }
        return mPostInteractionLiveData;
    }

    public LiveData<Comment> getObservablePostSubComments() {
        if (mPostSubCommentsLiveData == null) {
            mPostSubCommentsLiveData = new MutableLiveData<>();
        }
        return mPostSubCommentsLiveData;
    }

    protected LiveData<Boolean> getObservableHideMoreButton() {
        if (mHideMoreButtonLiveData == null) {
            mHideMoreButtonLiveData = new MutableLiveData<>();
        }
        return mHideMoreButtonLiveData;
    }

    protected LiveData<Boolean> getObservableHideTimelineListEmpty() {
        if (mHideTimelineListEmptyLiveData == null) {
            mHideTimelineListEmptyLiveData = new MutableLiveData<>();
        }
        return mHideTimelineListEmptyLiveData;
    }

    protected LiveData<Boolean> getObservableHideTimelineListPermissions() {
        if (mHideTimelineListPermissionsLiveData == null) {
            mHideTimelineListPermissionsLiveData = new MutableLiveData<>();
        }
        return mHideTimelineListPermissionsLiveData;
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

    public void clearPostInteractions() {
        mPostInteractionLiveData = new MutableLiveData<>();
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