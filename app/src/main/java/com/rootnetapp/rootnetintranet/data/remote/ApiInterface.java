package com.rootnetapp.rootnetintranet.data.remote;

import com.rootnetapp.rootnetintranet.models.createworkflow.CreateRequest;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePost;
import com.rootnetapp.rootnetintranet.models.requests.approval.ApprovalRequest;
import com.rootnetapp.rootnetintranet.models.requests.comment.EditCommentRequest;
import com.rootnetapp.rootnetintranet.models.requests.comment.PostCommentRequest;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.EditRequest;
import com.rootnetapp.rootnetintranet.models.requests.files.AttachFilesRequest;
import com.rootnetapp.rootnetintranet.models.requests.resetpassword.ResetPasswordRequest;
import com.rootnetapp.rootnetintranet.models.requests.signature.SignatureInitiateRequest;
import com.rootnetapp.rootnetintranet.models.responses.activation.WorkflowActivationResponse;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.business.BusinessOpportunitiesResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentDeleteResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.contact.ContactsResponse;
import com.rootnetapp.rootnetintranet.models.responses.contact.SubContactsResponse;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.country.CountryDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.FileUploadResponse;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.downloadfile.DownloadFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;
import com.rootnetapp.rootnetintranet.models.responses.exportpdf.ExportPdfResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.PlaceDetailsResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.AutocompleteResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.NearbySearchResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.TemporaryTokenResponse;
import com.rootnetapp.rootnetintranet.models.responses.people.PeopleDirectoryResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.project.ProjectResponse;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingResponse;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingsResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.RoleResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentListResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.InitiateSigningResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureWorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.InteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostInteractionResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostLikeDislike;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.PostSubCommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.SubCommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.ProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.websocket.OptionsSettingsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.DeleteWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.WorkflowApproveRejectResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowoverview.WorkflowOverviewResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.PostDeleteWorkflows;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.CategoryListResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

public interface ApiInterface {

    /**
     * IMPORTANTE: El unico con v1 porque el base url en gradle properties no tiene el v1.
       Los demas no tienen v1 porque el endpoint esta enviando la api_domain + /v1/ en la url
    */
    @POST("v1/check/client")
    @FormUrlEncoded
    Observable<ClientResponse> getDomain(@Field("domain") String domain);

    @Headers({"Domain-Name: api"})
    @POST("login_check")
    @FormUrlEncoded
    Observable<LoginResponse> login(@Field("username") String user,
                                    @Field("password") String password,
                                    @Field("firebase_id") String firebaseToken);

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
    Observable<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @Headers({"Domain-Name: api"})
    @POST("user/temporary_token")
    Observable<TemporaryTokenResponse> requestTemporaryToken(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("profiles?enabled=all")
    Observable<UserResponse> getUsers(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("profiles?enabled=all")
    Observable<ProfileResponse> getProfiles(@Header("Authorization") String authorization);


    @Headers({"Domain-Name: api"})
    @GET("profiles?")
    Observable<ProfileResponse> getProfiles(@Header("Authorization") String authorization,
                                            @Query("enabled") boolean enabled);

    @Headers({"Domain-Name: api"})
    @GET("profile")
    Observable<LoggedProfileResponse> getLoggedProfile(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @PATCH("profiles/{id}")
    @FormUrlEncoded
    Observable<EditUserResponse> editUser(@Header("Authorization") String authorization,
                                          @Path("id") int id,
                                          @Field("full_name") String fullName,
                                          @Field("email") String email,
                                          @Field("phone_number") String phoneNumber);

    @Headers({"Domain-Name: api"})
    @PATCH("profiles/{id}")
    @FormUrlEncoded
    Observable<EditUserResponse> changeUserPassword(@Header("Authorization") String authorization,
                                          @Path("id") int id,
                                          @Field("password") String password,
                                          @Field("repeated_password") String repeatedPassword);

    @Headers({"Domain-Name: api"})
    @GET("signing_vendor/workflow_type/{id}/templates")
    Observable<com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse> getSignatureTemplatesBy(@Header("Authorization") String authorization,
                                                                                                                     @Path("id") int workflowTypeId,
                                                                                                                    @Query("workflowId") int workflowId);

    @Headers({"Domain-Name: api"})
    @GET("signing_vendor/document_list/{workflowId}")
    Observable<DocumentListResponse> getSignatureDocumentList(@Header("Authorization") String authorization,
                                                              @Path("workflowId") int workflowId);

    @Headers({"Domain-Name: api"})
    @DELETE("signing_vendor/document")
    Observable<Object> deleteDocument(@Header("Authorization") String authorization,
                                      @Query("workflowId") int workflowId,
                                      @Query("templateId") int templateId);

    @Headers({"Domain-Name: api"})
    @POST("signing_vendor/initiate_signing")
    Observable<InitiateSigningResponse> initiateSigning(@Header("Authorization") String authorization,
                                                        @Body SignatureInitiateRequest request);

    @Headers({"Domain-Name: api"})
    @POST("signing_vendor/initiate_signing")
    Observable<Object> initiateSigningWithFields(@Header("Authorization") String authorization,
                                                        @Body SignatureInitiateRequest request);

    @Headers({"Domain-Name: api"})
    @GET("signing_vendor/workflow_types")
    Observable<SignatureWorkflowTypesResponse> getTemplatesInWorkflowType(@Header("Authorization") String authorization,
                                                                          @Query("workflowTypeId") int workflowTypeId);


    @Headers({"Domain-Name: api"})
    @GET("signing_vendor/documents/{provider_document_id}")
    Observable<SignatureFileResponse> getPdfFomProvider(@Header("Authorization") String authorization,
                                                        @Path("provider_document_id") String providerDocumentId,
                                                        @Query("save") boolean save);

    @Headers({"Domain-Name: api"})
    @GET("signing_vendor/documents/{provider_document_id}")
    Observable<SignatureFileResponse> getPdfFomProviderUsing(@Header("Authorization") String authorization,
                                                    @Path("provider_document_id") String providerDocumentId,
                                                    @Query("params") String jsonStringParams,
                                                    @Query("save") boolean save);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true&order=%7B%22desc%22:true,%22column%22:%22updated%22%7D")
    Observable<WorkflowResponseDb> getWorkflowsDb(@Header("Authorization") String authorization,
                                                  @Query("limit") int limit,
                                                  @Query("open") boolean open,
                                                  @Query("page") int page,
                                                  @Query("workflow_type") boolean showTypeDetails);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true&order=%7B%22desc%22:true,%22column%22:%22updated%22%7D")
    Observable<WorkflowResponseDb> getWorkflowsDbWithSearch(@Header("Authorization") String authorization,
                                                  @Query("limit") int limit,
                                                  @Query("open") boolean open,
                                                  @Query("page") int page,
                                                  @Query("workflow_type") boolean showTypeDetails,
                                                  @Query("query") String searchText);
    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getWorkflowsDbWithQueryOptions(@Header("Authorization") String authorization,
                                                                  @Query("limit") int limit,
                                                                  @Query("open") boolean open,
                                                                  @Query("page") int page,
                                                                  @Query("workflow_type") boolean showTypeDetails,
                                                                  @QueryMap Map<String, Object> options);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> searchWorkflowsDb(@Header("Authorization") String authorization,
                                                  @Query("limit") int limit,
                                                  @Query("open") boolean open,
                                                  @Query("page") int page,
                                                  @Query("workflow_type") boolean showTypeDetails,
                                                  @Query("query") String searchText);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true&order=%7B%22desc%22:true,%22column%22:%22updated%22%7D")
    Observable<WorkflowResponseDb> getWorkflowsBySearchQuery(@Header("Authorization") String authorization,
                                                             @Query("limit") int limit,
                                                             @Query("page") int page,
                                                             @Query("open") boolean open,
                                                             @Query("query") String query,
                                                             @Query("workflow_type") boolean showTypeDetails);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true")
    Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(@Header("Authorization") String authorization,
                                                           @Query("limit") int limit,
                                                           @Query("open") boolean open,
                                                           @Query("page") int page,
                                                           @Query("workflow_type") boolean showTypeDetails,
                                                           @QueryMap Map<String, Object> options);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true")
    Observable<WorkflowResponseDb> getWorkflowsDbFilteredByDynamicFields(@Header("Authorization") String authorization,
                                                                         @Query("limit") int limit,
                                                                         @Query("open") boolean open,
                                                                         @Query("page") int page,
                                                                         @Query("workflow_type") boolean showTypeDetails,
                                                                         @Query("workflow_type_id") int workflowTypeId,
                                                                         @Query("workflow_metadata") String metaData);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getMyPendingWorkflowsDb(@Header("Authorization") String authorization,
                                                  @Query("limit") int limit,
                                                  @Query("open") boolean open,
                                                  @Query("page") int page,
                                                  @Query("workflow_type") boolean showTypeDetails,
                                                  @Query("responsible_id") int profileId);
    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?status=true")
    Observable<WorkflowResponseDb> getMyPendingWorkflowsDb(@Header("Authorization") String authorization,
                                                           @Query("limit") int limit,
                                                           @Query("open") boolean open,
                                                           @Query("page") int page,
                                                           @Query("workflow_type") boolean showTypeDetails,
                                                           @Query("responsible_id") int profileId,
                                                           @QueryMap Map<String, Object> options);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getMyPendingWorkflowsDbByWorkflowType(@Header("Authorization") String authorization,
                                                           @Query("limit") int limit,
                                                           @Query("open") boolean open,
                                                           @Query("page") int page,
                                                           @Query("workflow_type") boolean showTypeDetails,
                                                           @Query("workflow_metadata") String metaData,
                                                           @Query("workflow_type_id") int workflowTypeId,
                                                           @Query("query") String searchText);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(@Header("Authorization") String authorization,
                                                             @Query("limit") int limit,
                                                             @Query("page") int page,
                                                             @Query("workflow_type") boolean showTypeDetails,
                                                             @QueryMap Map<String, Object> options);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflow/overview?")
    Observable<WorkflowOverviewResponse> getOverviewWorkflowsCount(@Header("Authorization") String authorization,
                                                                   @Query("open") boolean open,
                                                                   @Query("status") boolean status,
                                                                   @QueryMap Map<String, Object> options);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows?")
    Observable<WorkflowResponseDb> getWorkflowsByType(@Header("Authorization") String authorization,
                                                           @Query("limit") int limit,
                                                           @Query("open") boolean open,
                                                           @Query("page") int page,
                                                           @Query("workflow_type") boolean showTypeDetails,
                                                           @Query("workflow_type_id") int typeId);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types")
    Observable<WorkflowTypesResponse> getWorkflowTypes(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types?filter_counter_status=true")
    Observable<WorkflowTypeDbResponse> getWorkflowTypesDb(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types?allowed=true")
    Observable<WorkflowTypeDbResponse> getWorkflowTypesDbAllowedOnly(@Header("Authorization") String authorization);


    @Headers({"Domain-Name: api"})
    @GET("list/{id}/item")
    Observable<ListsResponse> getListItems(@Header("Authorization") String authorization,
                                           @Path("id") int id);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflow/category")
    Observable<CategoryListResponse> getCategoryListId(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("contacts/products?all=true")
    Observable<ProductsResponse> getProducts(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("contacts/services?all=true")
    Observable<ServicesResponse> getServices(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("roles?all=true")
    Observable<RoleResponse> getRoles(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("intranet/drafts?all=true")
    Observable<ProjectResponse> getProjects(@Header("Authorization") String authorization);


    @Headers({"Domain-Name: api"})
    @GET("client/35/users")
    Observable<WorkflowUserResponse> getWorkflowUsers(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("client/{clientId}/users")
    Observable<WorkflowUserResponse> getWorkflowUsers(@Header("Authorization") String authorization,
                                                      @Path("clientId") int clientId);

    @GET("check/countries")
    Observable<CountriesResponse> getCountries(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("check/countries")
    Observable<CountryDbResponse> getCountriesDb(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows")
    @FormUrlEncoded
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Field("workflow_type_id") int workflowTypeId,
                                                      @Field("title") String title,
                                                      @Field("workflow_metas") String workflowMetas,
                                                      @Field("start") String start,
                                                      @Field("description") String description);

    @Headers({"Domain-Name: api", "Content-Type: application/json;charset=UTF-8"})
    @POST("intranet/workflows")
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Body CreateRequest body);

    @Headers({"Domain-Name: api", "Content-Type: application/json;charset=UTF-8"})
    @POST("intranet/workflows")
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Body Map<String, Object> body);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows")
    Observable<CreateWorkflowResponse> createWorkflow(@Header("Authorization") String authorization,
                                                      @Body String body);

    //todo test
    @Headers({"Domain-Name: api", "Content-Type: application/json;charset=UTF-8"})
    @PATCH("intranet/workflows/{id}")
    Observable<CreateWorkflowResponse> editWorkflow(@Header("Authorization") String authorization,
                                                    @Path("id") int workflowId,
                                                    @Body EditRequest body);

    @Headers({"Domain-Name: api", "Content-Type: application/json;charset=UTF-8"})
    @PATCH("intranet/workflows/{id}")
    Observable<CreateWorkflowResponse> editWorkflow(@Header("Authorization") String authorization,
                                                    @Path("id") int workflowId,
                                                    @Body Map<String, Object> body);

    @Headers({"Domain-Name: api"})
    @POST("upload/file")
    Observable<FileUploadResponse> uploadFile(@Header("Authorization") String authorization,
                                              @Body FilePost body);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/types/{id}")
    Observable<WorkflowTypeResponse> getWorkflowType(@Header("Authorization") String authorization,
                                                     @Path("id") int typeId);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/{id}")
    Observable<WorkflowResponse> getWorkflow(@Header("Authorization") String authorization,
                                             @Path("id") int workflowId);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows/{id}?confirmation=true")
    Observable<DeleteWorkflowResponse> deleteWorkflow(@Header("Authorization") String authorization,
                                                      @Path("id") int workflowId);

    @Headers({"Domain-Name: api", "Content-Type: application/json;charset=UTF-8"})
    @POST("intranet/workflows/{id}?confirmation=true")
    Observable<DeleteWorkflowResponse> deleteWorkflows(@Header("Authorization") String authorization,
                                                       @Path("id") int workflowId,
                                                       @Body PostDeleteWorkflows postDeleteWorkflows);

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
    Observable<CommentResponse> postComment(@Header("Authorization") String authorization,
                                            @Path("id") int workflowId,
                                            @Body PostCommentRequest request);

    @Headers({"Domain-Name: api"})
    @PATCH("intranet/workflow/comment/{id}")
    Observable<CommentResponse> editComment(@Header("Authorization") String authorization,
                                                    @Path("id") int commentId,
                                                    @Body EditCommentRequest request);

    @Headers({"Domain-Name: api"})
    @DELETE("intranet/workflow/comment/{id}")
    Observable<CommentDeleteResponse> deleteComment(@Header("Authorization") String authorization,
                                                    @Path("id") int commentId,
                                                    @Query("confirmation") boolean confirmation);
    @Headers({"Domain-Name: api"})
    @POST("intranet/workflow/{id}/approval")
    Observable<WorkflowApproveRejectResponse> postApproveReject(@Header("Authorization") String authorization,
                                                                @Path("id") int workflowId,
                                                                @Body ApprovalRequest request);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflow/mass_approval")
    Observable<WorkflowResponse> postMassApproval(@Header("Authorization") String authorization,
                                                  @Body Map<String, Object> body);

    @Headers({"Domain-Name: api"})
    @POST("intranet/workflows/records/file")
    Observable<AttachResponse> attachFile(@Header("Authorization") String authorization,
                                          @Body AttachFilesRequest request);

    @Headers({"Domain-Name: api"})
    @GET("timeline?")
    Observable<TimelineResponse> getTimeline(@Header("Authorization") String authorization,
                                             @Query("start") String start,
                                             @Query("end") String end,
                                             @Query("page") int page,
                                             @Query("limit") int limit,
                                             @Query("userId[]") List<String> users,
                                             @Query("entity[]") List<String> modules);

    @Headers({"Domain-Name: api"})
    @GET("interaction?all=true&comment=100&thumb=100")
    Observable<InteractionResponse> getTimelineInteractions(@Header("Authorization") String authorization,
                                                            @Query("entity_type[]") List<String> modules,
                                                            @Query("entity[]") List<Integer> entities);


    @Headers({"Domain-Name: api"})
    @GET("interaction/comment?limit=500")
    Observable<SubCommentsResponse> getSubComment(@Header("Authorization")String auth,
                                                  @Query("associate")int associate,
                                                  @Query("level")int level);

    @Headers({"Domain-Name: api"})
    @POST("interaction")
    @FormUrlEncoded
    Observable<PostInteractionResponse> postComment(@Header("Authorization")String auth,
                                                    @Field("interactionId") Integer interactionId,
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

    @Headers({"Domain-Name: api"})
    @PATCH("interaction/{id}")
    Observable<PostInteractionResponse> postLikeDislike(@Header("Authorization") String authorization,
                                                        @Path("id") int interactionId,
                                                        @Body PostLikeDislike request);

    @Headers({"Domain-Name: api"})
    @POST("interaction")
    Observable<PostInteractionResponse> postLikeDislike(@Header("Authorization") String authorization,
                                                        @Body PostLikeDislike request);

    @Headers({"Domain-Name: api"})
    @PATCH("intranet/workflow/activation")
    @FormUrlEncoded
    Observable<WorkflowActivationResponse> postWorkflowActivationOpenClose(@Header("Authorization") String authorization,
                                                                           @Field("workflows[]") List<Integer> workflowIds,
                                                                           @Field("open") boolean isOpen);

    @Headers({"Domain-Name: api"})
    @PATCH("intranet/workflow/activation")
    @FormUrlEncoded
    Observable<WorkflowActivationResponse> postWorkflowActivationEnableDisable(@Header("Authorization") String authorization,
                                                                           @Field("workflows[]") List<Integer> workflowIds,
                                                                           @Field("activate") boolean isEnabled);

    @Headers({"Domain-Name: api"})
    @GET("intranet/workflows/{id}/pdf?all=true")
    Observable<ExportPdfResponse> getWorkflowPdfFile(@Header("Authorization") String authorization,
                                                     @Path("id") int workflowId);

    @Headers({"Domain-Name: api"})
    @Streaming
    @GET("file/download/{entity}/{id}")
    Observable<DownloadFileResponse> downloadFile(@Header("Authorization") String authorization,
                                                  @Path("entity") String entity,
                                                  @Path("id") int fileId);

    @Headers({"Domain-Name: api"})
    @Streaming
    @GET("options?key=socket_main")
    Observable<OptionsSettingsResponse> getWsPort(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @Streaming
    @GET("options?key=socket_protocol")
    Observable<OptionsSettingsResponse> getWsProtocol(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @Streaming
    @GET("options?key=google_maps_apikey")
    Observable<OptionsSettingsResponse> getGoogleMapsApiKey(@Header("Authorization") String authorization);

    @Headers({"Domain-Name: api"})
    @GET("contacts/records?")
    Observable<ContactsResponse> getContacts(@Header("Authorization") String authorization,
                                             @Query("query") String query,
                                             @Query("limit") int limit);

    @Headers({"Domain-Name: api"})
    @GET("business/opportunities?")
    Observable<BusinessOpportunitiesResponse> getBusinessOpportunities(@Header("Authorization") String authorization,
                                                                       @Query("query") String query,
                                                                       @Query("limit") int limit);

    @Headers({"Domain-Name: api"})
    @POST("sub_contacts/records?")
    @FormUrlEncoded
    Observable<SubContactsResponse> postSearchSubContacts(@Header("Authorization") String authorization,
                                                          @Field("query") String query,
                                                          @Query("limit") int limit);

    @Headers({"Domain-Name: api"})
    @GET("intranet/bookings")
    Observable<BookingsResponse> getBookings(@Header("Authorization") String authorization,
                                                          @Query("initial_date") String startDate,
                                                          @Query("end_date") String endDate);

    @Headers({"Domain-Name: api"})
    @GET("intranet/booking/{id}")
    Observable<BookingResponse> getBooking(@Header("Authorization") String authorization,
                                           @Path("id") int bookingId);

    @Headers({"Domain-Name: api"})
    @GET("/intranet/people/directory?all=true")
    Observable<PeopleDirectoryResponse> getPeopleDirectory(@Header("Authorization") String authorization);

    //region Google Maps API
    @GET("https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankby=distance")
    Observable<NearbySearchResponse> getNearbyPlaces(@Query("location") String locationString,
                                                     @Query("key") String apiKey);

    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json?")
    Observable<AutocompleteResponse> getAutocompletePlaces(@Query("input") String input,
                                                           @Query("key") String apiKey);

    @GET("https://maps.googleapis.com/maps/api/place/details/json?fields=name,geometry,formatted_address")
    Observable<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String placeId,
                                                     @Query("key") String apiKey);
    //endregion
}