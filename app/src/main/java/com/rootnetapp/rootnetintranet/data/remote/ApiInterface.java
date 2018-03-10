package com.rootnetapp.rootnetintranet.data.remote;


import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Propietario on 09/03/2018.
 */

public interface ApiInterface {

    @POST("/v1/check/client")
    @FormUrlEncoded
    Observable<ClientResponse> getDomain(@Field("domain") String domain);

    @Headers({"Domain-Name: api"})
    @POST("/v1/login_check")
    @FormUrlEncoded
    Observable<LoginResponse> login(@Field("username") String user, @Field("password") String password);

}
