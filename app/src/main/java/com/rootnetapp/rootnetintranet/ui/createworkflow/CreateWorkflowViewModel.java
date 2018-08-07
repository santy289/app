package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowViewModel extends ViewModel {

    private MutableLiveData<WorkflowTypesResponse> mWorkflowsLiveData;
    private MutableLiveData<ListsResponse> mListLiveData;
    private MutableLiveData<ProductsResponse> mProductLiveData;
    private MutableLiveData<ServicesResponse> mServiceLiveData;
    private MutableLiveData<WorkflowUserResponse> mUserLiveData;
    private MutableLiveData<CountriesResponse> mCountriesLiveData;
    private MutableLiveData<CreateWorkflowResponse> mCreateLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCreateErrorLiveData;
    private CreateWorkflowRepository createWorkflowRepository;
    //todo REMOVE, solo testing
    //private String auth2 = "Bearer " + Utils.testToken;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
    }

    public void getWorkflowTypes(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        Log.d("test", "getWorkflowTypes: ");
        createWorkflowRepository.getWorkflowTypes(auth).subscribe(this::onTypesSuccess, this::onFailure);
    }

    public void getList(String auth, int id) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getList(auth, id).subscribe(this::onListSuccess, this::onFailure);
    }

    public void getProducts(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getProducts(auth).subscribe(this::onProductsSuccess, this::onFailure);
    }

    public void getServices(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getServices(auth).subscribe(this::onServicesSuccess, this::onFailure);
    }

    public void getUsers(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getUsers(auth).subscribe(this::onUsersSuccess, this::onFailure);
    }

    public void getCountries(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getCountries(auth).subscribe(this::onCountriesSuccess, this::onFailure);
    }

    public void createWorkflow(String auth, int workflowTypeId, String title, String workflowMetas,
                               String start, String description) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.createWorkflow(auth, workflowTypeId, title, workflowMetas,
                start, description).subscribe(this::onCreateSuccess, this::onCreateFailure);
    }

    private void onTypesSuccess(WorkflowTypesResponse workflowTypesResponse) {
        for (WorkflowType type : workflowTypesResponse.getList()) {
            Log.d("test", "onTypesSuccess: "+type.getName());
        }
        mWorkflowsLiveData.setValue(workflowTypesResponse);
    }

    private void onListSuccess(ListsResponse listsResponse) {
        mListLiveData.setValue(listsResponse);
    }

    private void onProductsSuccess(ProductsResponse productsResponse) {
        mProductLiveData.setValue(productsResponse);
    }

    private void onServicesSuccess(ServicesResponse servicesResponse) {
        mServiceLiveData.setValue(servicesResponse);
    }

    private void onUsersSuccess(WorkflowUserResponse workflowUserResponse) {
        mUserLiveData.setValue(workflowUserResponse);
    }

    private void onCountriesSuccess(CountriesResponse countriesResponse) {
        mCountriesLiveData.setValue(countriesResponse);
    }

    private void onCreateSuccess(CreateWorkflowResponse createWorkflowResponse) {
        mCreateLiveData.setValue(createWorkflowResponse);
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }
    private void onCreateFailure(Throwable throwable) {
        mCreateErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<WorkflowTypesResponse> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    public LiveData<ListsResponse> getObservableList() {
        if (mListLiveData == null) {
            mListLiveData = new MutableLiveData<>();
        }
        return mListLiveData;
    }

    public LiveData<ProductsResponse> getObservableProduct() {
        if (mProductLiveData == null) {
            mProductLiveData = new MutableLiveData<>();
        }
        return mProductLiveData;
    }

    public LiveData<ServicesResponse> getObservableService() {
        if (mServiceLiveData == null) {
            mServiceLiveData = new MutableLiveData<>();
        }
        return mServiceLiveData;
    }

    public LiveData<WorkflowUserResponse> getObservableWorkflowUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    public LiveData<CountriesResponse> getObservableCountries() {
        if (mCountriesLiveData == null) {
            mCountriesLiveData = new MutableLiveData<>();
        }
        return mCountriesLiveData;
    }

    public LiveData<CreateWorkflowResponse> getObservableCreate() {
        if (mCreateLiveData == null) {
            mCreateLiveData = new MutableLiveData<>();
        }
        return mCreateLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Integer> getObservableCreateError() {
        if (mCreateErrorLiveData == null) {
            mCreateErrorLiveData = new MutableLiveData<>();
        }
        return mCreateErrorLiveData;
    }

}
