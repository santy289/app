package com.rootnetapp.rootnetintranet.data.remote;


import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
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
import com.rootnetapp.rootnetintranet.models.responses.timeline.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

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


public interface ApiInterface {

    /* El unico con v1 porque el base url en gradle properties no tiene el v1.
       Los demas no tienen v1 porque el endpoint esta enviando la api_domain + /v1/ en la url
    */
    @POST("v1/check/client")
    @FormUrlEncoded
    Observable<ClientResponse> getDomain(@Field("domain") String domain);

    @Headers({"Domain-Name: api"})
    @POST("login_check")
    @FormUrlEncoded
    Observable<LoginResponse> login(@Field("username") String user,
                                    @Field("password") String password);

    @Headers({"Domain-Name: api"})
    @POST("check/send/email")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> requestToken(@Field("username") String username,
                                                   @Field("client_id") String client_id);

    @Headers({"Domain-Name: api"})
    @POST("check/token")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> validateToken(@Field("token") String token);

    @Headers({"Domain-Name: api"})
    @POST("check/reset/password")
    @FormUrlEncoded
    Observable<ResetPasswordResponse> resetPassword(@Field("token") String token,
                                                    @Field("username") String username,
                                                    @Field("password") String password,
                                                    @Field("repeat_new_password") String repeatNewPassword);

    @Headers({"Domain-Name: api"})
    @GET("profiles?enabled=all")
    Observable<UserResponse> getUsers(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @PATCH("profiles/{id}")
    @FormUrlEncoded
    Observable<EditUserResponse> editUser(@Header("Authorization") String authorization,
                                          @Path("id") int id,
                                          @Field("full_name") String fullName,
                                          @Field("email") String email,
                                          @Field("phone_number") String phoneNumber);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowsResponse> getWorkflows(@Header("Authorization") String authorization,
                                               @Query("limit") int limit,
                                               @Query("open") boolean open,
                                               @Query("page") int page,
                                               @Query("status") boolean status);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getWorkflowsDb(@Header("Authorization") String authorization,
                                                  @Query("limit") int limit,
                                                  @Query("open") boolean open,
                                                  @Query("page") int page,
                                                  @Query("workflow_type") boolean showTypeDetails);


    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types")
    Observable<WorkflowTypesResponse> getWorkflowTypes(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types")
    Observable<WorkflowTypeDbResponse> testGetWorkflowTypes(@Header("Authorization") String authorization);


    @Headers({"Domain-Name: api"})
    @GET("list/{id}/item")
    Observable<ListsResponse> getListItems(@Header("Authorization") String authorization,
                                           @Path("id") int id);

    @Headers({"Domain-Name: api"})
    @GET("contacts/products?all=true")
    Observable<ProductsResponse> getProducts(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("contacts/services?all=true")
    Observable<ServicesResponse> getServices(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("client/35/users")
    Observable<WorkflowUserResponse> getWorkflowUsers(@Header("Authorization") String authorization);

    @GET("check/countries")
    Observable<CountriesResponse> getCountries(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows")
    @FormUrlEncoded
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Field("workflow_type_id") int workflowTypeId,
                                                      @Field("title") String title,
                                                      @Field("workflow_metas") String workflowMetas,
                                                      @Field("start") String start,
                                                      @Field("description") String description);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types/{id}")
    Observable<WorkflowTypeResponse> getWorkflowType(@Header("Authorization") String authorization,
                                                     @Path("id") int typeId);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/{id}")
    Observable<WorkflowResponse> getWorkflow(@Header("Authorization") String authorization,
                                             @Path("id") int workflowId);

    @Headers({"Domain-Name: api"})
    @GET("intranet/templates/{id}")
    Observable<TemplatesResponse> getTemplate(@Header("Authorization") String authorization,
                                              @Path("id") int templateId);


    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/{id}/files")
    Observable<FilesResponse> getFiles(@Header("Authorization") String authorization,
                                       @Path("id") int workflowId);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflow/{id}/comments?")
    Observable<CommentsResponse> getComments(@Header("Authorization") String authorization,
                                             @Path("id") int workflowId,
                                             @Query("limit") int limit,
                                             @Query("page") int page);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflow/{id}/comment")
    @FormUrlEncoded
    Observable<CommentResponse> postComment(@Header("Authorization") String authorization,
                                            @Path("id") int workflowId,
                                            @Field("description") String description,
                                            @Field("files") List<CommentFile> files);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows/records/file")
    @FormUrlEncoded
    Observable<AttachResponse> attachFile(@Header("Authorization") String authorization,
                                          @Field("workflows") List<WorkflowPresetsRequest> request,
                                          @Field("file") CommentFile fileRequest);

    @Headers({"Domain-Name: api"})
    @GET("timeline?")
    Observable<TimelineResponse> getTimeline(@Header("Authorization") String authorization,
                                             @Query("start") String start,
                                             @Query("end") String end,
                                             @Query("userId[]") List<String> users,
                                             @Query("entity[]") List<String> modules);

    @Headers({"Domain-Name: api"})
    @GET("interaction?all=true&comment=500")
    Observable<InteractionResponse> getTimelineComments(@Header("Authorization") String authorization);


    @Headers({"Domain-Name: api"})
    @GET("interaction/comment?limit=500")
    Observable<SubCommentsResponse> getSubComment(@Header("Authorization")String auth,
                                                  @Query("associate")int associate,
                                                  @Query("level")int level);

    @Headers({"Domain-Name: api"})
    @POST("interaction")
    @FormUrlEncoded
    Observable<PostCommentResponse> postComment(@Header("Authorization")String auth,
                                                @Field("interactionId") int interactionId,
                                                @Field("entity") int entity,
                                                @Field("entityType") String entityType,
                                                @Field("description") String description,
                                                @Field("author") int author);

    @Headers({"Domain-Name: api"})
    @POST("interaction/comment")
    @FormUrlEncoded
    Observable<PostSubCommentResponse> postSubComment(@Header("Authorization")String auth,
                                                      @Field("interaction") int interaction,
                                                      @Field("associate") int associate,
                                                      @Field("description") String description,
                                                      @Field("author") int author);

}