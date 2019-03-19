package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostInteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostLikeDislike;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 10/04/18.
 */

public class TimelineRepository {

    private ApiInterface service;

    public TimelineRepository(ApiInterface service) {
        this.service = service;
    }

    public Observable<TimelineResponse> getTimeline(String auth, String start, String end, int page,
                                                    int limit, List<String> users,
                                                    List<String> modules) {
        return service.getTimeline(auth, start, end, page, limit, users, modules)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserResponse> getUsers(String auth) {
        return service.getUsers(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowUserResponse> getWorkflowUsers(String auth) {
        return service.getWorkflowUsers(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<InteractionResponse> getTimelineInteractions(String auth, List<String> modules,
                                                                   List<Integer> entities) {
        return service.getTimelineInteractions(auth, modules, entities)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SubCommentsResponse> getSubComment(String auth, int associate, int level) {
        return service.getSubComment(auth, associate, level).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostInteractionResponse> postComment(String auth, @Nullable Integer interactionId, int entity,
                                                           String entityType, String description,
                                                           int author) {
        return service.postComment(auth, interactionId, entity, entityType, description, author)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostSubCommentResponse> postSubComment(String auth, int interaction,
                                                             int associate, String description,
                                                             int author) {
        return service.postSubComment(auth, interaction, associate, description, author)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostInteractionResponse> postLikeDislike(String auth, int interaction,
                                                   PostLikeDislike request) {
        return service.postLikeDislike(auth, interaction, request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostInteractionResponse> postLikeDislike(String auth, PostLikeDislike request) {
        return service.postLikeDislike(auth, request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
