package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.timeline.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;

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


    public Observable<TimelineResponse> getTimeline(String auth, String start, String end) {
        return service.getTimeline(auth, start, end).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserResponse> getUsers(String auth) {
        return service.getUsers(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<InteractionResponse> getComments(String auth) {
        return service.getTimelineComments(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SubCommentsResponse> getSubComment(String auth, int associate, int level) {
        return service.getSubComment(auth, associate, level).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostCommentResponse> postComment(String auth, int interactionId, int entity,
                                                       String entityType, String description,
                                                       int author) {
        return service.postComment(auth, interactionId, entity, entityType, description, author).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PostSubCommentResponse> postSubComment(String auth, int interaction,
                                                             int associate, String description,
                                                             int author) {
        return service.postSubComment(auth, interaction, associate, description, author).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
