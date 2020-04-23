package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.project.ProjectResponse;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingsResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.RoleResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResourcingPlannerRepository {

    private final ApiInterface service;

    private static final String TAG = "ResourcingPlannerRepo";

    public ResourcingPlannerRepository(ApiInterface service) {
        this.service = service;
    }

    public Observable<BookingsResponse> getBookings(String auth, String startDate, String endDate) {
        return service.getBookings(auth, startDate, endDate).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ProjectResponse> getProjects(String auth) {
        return service.getProjects(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RoleResponse> getRoles(String auth) {
        return service.getRoles(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}