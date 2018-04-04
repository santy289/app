package com.rootnetapp.rootnetintranet.data.remote;


import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Observable<LoginResponse> login(@Field("username") String user,
                                    @Field("password") String password);

    @Headers({"Domain-Name: api"})
    @POST("v1/check/send/email")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> requestToken(@Field("username") String username,
                                                   @Field("client_id") String client_id);

    @Headers({"Domain-Name: api"})
    @POST("v1/check/token")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> validateToken(@Field("token") String token);

    @Headers({"Domain-Name: api"})
    @POST("v1/check/reset/password")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> resetPassword(@Field("token") String token,
                                                    @Field("username") String username,
                                                    @Field("password") String password,
                                                    @Field("repeat_new_password") String repeatNewPassword);

    @Headers({"Domain-Name: api"})
    @GET("v1/profiles?enabled=all")
    Observable<UserResponse> getUsers(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @PATCH("v1/profiles/{id}")
    @FormUrlEncoded
    Observable<EditUserResponse> editUser(@Header("Authorization") String authorization,
                                          @Path("id") int id,
                                          @Field("full_name") String fullName,
                                          @Field("email") String email,
                                          @Field("phone_number") String phoneNumber);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflows?")
    Observable<WorkflowsResponse> getWorkflows(@Header("Authorization") String authorization,
                                               @Query("limit") int limit,
                                               @Query("open") boolean open,
                                               @Query("page") int page,
                                               @Query("status") boolean status);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflows/types")
    Observable<WorkflowTypesResponse> getWorkflowTypes(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/list/{id}/item")
    Observable<ListsResponse> getListItems(@Header("Authorization") String authorization,
                                           @Path("id") int id);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/contacts/products?all=true")
    Observable<ProductsResponse> getProducts(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/contacts/services?all=true")
    Observable<ServicesResponse> getServices(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/client/35/users")
    Observable<WorkflowUserResponse> getWorkflowUsers(@Header("Authorization") String authorization);

    @GET("v1/check/countries")
    Observable<CountriesResponse> getCountries(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: localhost"})
    @POST("v1/intranet/workflows")
    @FormUrlEncoded
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Field("workflow_type_id") int workflowTypeId,
                                                      @Field("title") String title,
                                                      @Field("workflow_metas") String workflowMetas,
                                                      @Field("start") String start,
                                                      @Field("description") String description);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflows/types/{id}")
    Observable<WorkflowTypeResponse> getWorkflowType(@Header("Authorization") String authorization,
                                                     @Path("id")int typeId);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflows/{id}")
    Observable<WorkflowResponse> getWorkflow(@Header("Authorization") String authorization,
                                             @Path("id")int workflowId);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/templates/{id}")
    Observable<TemplatesResponse> getTemplate(@Header("Authorization") String authorization,
                                              @Path("id") int templateId);


    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflows/{id}/files")
    Observable<FilesResponse> getFiles(@Header("Authorization") String authorization,
                                       @Path("id") int workflowId);

    @Headers({"Domain-Name: localhost"})
    @GET("v1/intranet/workflow/{id}/comments?")
    Observable<CommentsResponse> getComments(@Header("Authorization") String authorization,
                                             @Path("id") int workflowId,
                                             @Query("limit") int limit,
                                             @Query("page") int page);

}