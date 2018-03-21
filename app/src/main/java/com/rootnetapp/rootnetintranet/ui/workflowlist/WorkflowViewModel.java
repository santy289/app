package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 19/03/18.
 */

public class WorkflowViewModel extends ViewModel {

    private MutableLiveData<List<Workflow>> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowRepository workflowRepository;
    private List<Workflow> workflows, unordered;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXUyJ9.eyJleHAiOjE1MjE3MTQyMjgsInVzZXJuYW1lIjoiYWRtaW4iLCJkZXBhcnRtZW50IjpbeyJpZCI6MSwibmFtZSI6Ik1hbmFnZW1lbnQiLCJhY3RpdmUiOm51bGwsImNyZWF0ZWRfYXQiOnsiZGF0ZSI6IjIwMTYtMDYtMDIgMTE6Mjk6NTYuMDAwMDAwIiwidGltZXpvbmVfdHlwZSI6MywidGltZXpvbmUiOiJBbWVyaWNhL0hhbGlmYXgifSwidXBkYXRlZF9hdCI6eyJkYXRlIjoiMjAxNy0xMi0yOCAxNTo1NzoxOC4wMDAwMDAiLCJ0aW1lem9uZV90eXBlIjozLCJ0aW1lem9uZSI6IkFtZXJpY2EvSGFsaWZheCJ9LCJzbHVnIjoiTWFuYWdlbWVudCJ9LHsiaWQiOjIsIm5hbWUiOiJWZW50YXMiLCJhY3RpdmUiOm51bGwsImNyZWF0ZWRfYXQiOnsiZGF0ZSI6IjIwMTYtMDYtMDIgMTE6Mjk6NTYuMDAwMDAwIiwidGltZXpvbmVfdHlwZSI6MywidGltZXpvbmUiOiJBbWVyaWNhL0hhbGlmYXgifSwidXBkYXRlZF9hdCI6eyJkYXRlIjoiMjAxNy0xMi0yOCAxNTo1NzoxOC4wMDAwMDAiLCJ0aW1lem9uZV90eXBlIjozLCJ0aW1lem9uZSI6IkFtZXJpY2EvSGFsaWZheCJ9LCJzbHVnIjoiVmVudGFzIn1dLCJjbGllbnRfaGFzaCI6IjEyZGMxMWNhN2RmNjQxIiwicHJvZmlsZSI6eyJpZCI6MSwidXNlcl9pZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImVtYWlsIjoibmFzc2VyQHJvb3RzdGFjay5jb21hIiwiZW5hYmxlZCI6dHJ1ZSwiZnVsbF9uYW1lIjoiSm9uIERvZSIsInBob25lX251bWJlciI6IjM0NTc3OCIsInBpY3R1cmUiOiJ1cGxvYWRzL3Byb2ZpbGVzL2IxMDMwOGNjYTUyNTNhMTM2OTY2MWZkYmExOWMwYmNiZDdmNTkwMmQucG5nIiwibG9jYWxlIjoiZW4iLCJlbmFibGVkUHJvZHVjdHMiOlsiY3JtIiwiaW50cmFuZXQiLCJzZXJ2aWNlX2Rlc2siXSwiZGVmYXVsdExhbmRpbmciOm51bGwsImRlcGFydG1lbnQiOlt7ImlkIjoxLCJuYW1lIjoiTWFuYWdlbWVudCIsImFjdGl2ZSI6bnVsbCwiY3JlYXRlZF9hdCI6eyJkYXRlIjoiMjAxNi0wNi0wMiAxMToyOTo1Ni4wMDAwMDAiLCJ0aW1lem9uZV90eXBlIjozLCJ0aW1lem9uZSI6IkFtZXJpY2EvSGFsaWZheCJ9LCJ1cGRhdGVkX2F0Ijp7ImRhdGUiOiIyMDE3LTEyLTI4IDE1OjU3OjE4LjAwMDAwMCIsInRpbWV6b25lX3R5cGUiOjMsInRpbWV6b25lIjoiQW1lcmljYS9IYWxpZmF4In0sInNsdWciOiJNYW5hZ2VtZW50In0seyJpZCI6MiwibmFtZSI6IlZlbnRhcyIsImFjdGl2ZSI6bnVsbCwiY3JlYXRlZF9hdCI6eyJkYXRlIjoiMjAxNi0wNi0wMiAxMToyOTo1Ni4wMDAwMDAiLCJ0aW1lem9uZV90eXBlIjozLCJ0aW1lem9uZSI6IkFtZXJpY2EvSGFsaWZheCJ9LCJ1cGRhdGVkX2F0Ijp7ImRhdGUiOiIyMDE3LTEyLTI4IDE1OjU3OjE4LjAwMDAwMCIsInRpbWV6b25lX3R5cGUiOjMsInRpbWV6b25lIjoiQW1lcmljYS9IYWxpZmF4In0sInNsdWciOiJWZW50YXMifV0sImdyb3VwcyI6WzEsMl0sInJvbGVzIjp7IjAiOiJST0xFX1VTRVIiLCIxIjoiUk9MRV9ST09UTkVUX1VTRVIiLCIyIjoiUk9MRV9BRE1JTiIsIjMiOiJST0xFX0FDQ09VTlRfQ1JFQVRFIiwiNCI6IlJPTEVfQUNDT1VOVF9VUERBVEUiLCI1IjoiUk9MRV9BQ0NPVU5UX0RFTEVURSIsIjYiOiJST0xFX0FDQ09VTlRfREVMRVRFX0FMTCIsIjciOiJST0xFX0FDQ09VTlRfREVBQ1RJVkFURSIsIjgiOiJST0xFX0FDQ09VTlRfREVBQ1RJVkFURV9BTEwiLCI5IjoiUk9MRV9BQ0NPVU5UX01BTkFHRV9VU0VSUyIsIjEwIjoiUk9MRV9TVVBQT1JUX1RJQ0tFVCIsIjExIjoiUk9MRV9TVVBQT1JUX1RJQ0tFVF9DUkVBVEUiLCIxMiI6IlJPTEVfU1VQUE9SVF9USUNLRVRfVVBEQVRFIiwiMTMiOiJST0xFX1NVUFBPUlRfVElDS0VUX0RFTEVURSIsIjE0IjoiUk9MRV9TVVBQT1JUX1RJQ0tFVF9ERUxFVEVfQUxMIiwiMTUiOiJST0xFX1NVUFBPUlRfVElDS0VUX1NUQVRVUyIsIjE2IjoiUk9MRV9NQU5BR0VfQUNDT1VOVFNfR0VORVJBTFMiLCIxNyI6IlJPTEVfTUFOQUdFX0FDQ09VTlRTX0NPTlRBQ1RfVFlQRVMiLCIxOCI6IlJPTEVfTUFOQUdFX0FDQ09VTlRTX0NPTlRBQ1RfVFlQRV9GSUVMRFMiLCIxOSI6IlJPTEVfTUFOQUdFX0FDQ09VTlRTX0NPTlRBQ1RfVFlQRV9MSVNUUyIsIjIwIjoiUk9MRV9NQU5BR0VfQUNDT1VOVFNfQ09OVEFDVF9UUkFDS0lOR19GSUVMRFMiLCIyMSI6IlJPTEVfTUFOQUdFX0FDQ09VTlRTX1NVQl9DT05UQUNUX0ZJRUxEUyIsIjIyIjoiUk9MRV9NQU5BR0VfQUNDT1VOVFNfQ09OVEFDVF9UWVBFX1BST0RVQ1RTIiwiMjMiOiJST0xFX01BTkFHRV9BQ0NPVU5UU19MT0ciLCIyNCI6IlJPTEVfU1VCX0NPTlRBQ1RfQ1JFQVRFIiwiMjUiOiJST0xFX1NVQl9DT05UQUNUX1VQREFURSIsIjI2IjoiUk9MRV9TVUJfQ09OVEFDVF9ERUxFVEUiLCIyNyI6IlJPTEVfTUFOQUdFX1NVUFBPUlRfUFJJT1JJVElFUyIsIjI4IjoiUk9MRV9NQU5BR0VfU1VQUE9SVF9SQVRJTkdTIiwiMjkiOiJST0xFX01BTkFHRV9TVVBQT1JUX1NUQVRVUyIsIjMwIjoiUk9MRV9NQU5BR0VfU1VQUE9SVF9USUNLRVRfVFlQRSIsIjMxIjoiUk9MRV9NQU5BR0VfUk9MRVMiLCIzMiI6IlJPTEVfTUFOQUdFX1BFUk1JU1NJT05TIiwiMzMiOiJST0xFX1BST0pFQ1RfQ1JFQVRFIiwiMzQiOiJST0xFX1BST0pFQ1RfREVMRVRFIiwiMzUiOiJST0xFX1BST0pFQ1RfU1dJVENIX1NUQVRFIiwiMzYiOiJST0xFX1BST0pFQ1RfREVMRVRFX0FMTCIsIjM3IjoiUk9MRV9NQU5BR0VfUFJPSkVDVFMiLCIzOCI6IlJPTEVfTUFOQUdFX05PVElGSUNBVElPTlMiLCIzOSI6IlJPTEVfTUFOQUdFX1NBTEVTX0ZPUkNFIiwiNDAiOiJST0xFX1BST0RVQ1RfQ1JFQVRFIiwiNDEiOiJST0xFX1BST0RVQ1RfREVMRVRFIiwiNDIiOiJST0xFX1BST0RVQ1RfVVBEQVRFIiwiNDMiOiJST0xFX1BST0RVQ1RTIiwiNDQiOiJST0xFX1NFUlZJQ0VfQ1JFQVRFIiwiNDUiOiJST0xFX1NFUlZJQ0VfREVMRVRFIiwiNDYiOiJST0xFX1NFUlZJQ0VfVVBEQVRFIiwiNDciOiJST0xFX1NFUlZJQ0VTIiwiNDgiOiJST0xFX0NPTlRBQ1RfQ1JFQVRFIiwiNDkiOiJST0xFX0NPTlRBQ1RfVVBEQVRFIiwiNTAiOiJST0xFX0NPTlRBQ1RfREVMRVRFIiwiNTEiOiJST0xFX0NPTlRBQ1RfREVMRVRFX0FMTCIsIjUyIjoiUk9MRV9DT05UQUNUX0RFQUNUSVZBVEUiLCI1MyI6IlJPTEVfQ09OVEFDVF9ERUFDVElWQVRFX0FMTCIsIjU0IjoiUk9MRV9DT05UQUNUX1VQREFURV9UWVBFIiwiNTUiOiJST0xFX0NPTlRBQ1RfSU1QT1JUIiwiNTYiOiJST0xFX0NPTlRBQ1RfVFJBQ0tJTkdfQ1JFQVRFIiwiNTciOiJST0xFX0NPTlRBQ1RfSElTVE9SWSIsIjY2IjoiUk9MRV9QUk9EVUNUX1NFUlZJQ0VfQUNUSVZBVEUiLCI2OSI6IlJPTEVfTUFOQUdFX09SR0FOSVpBVElPTl9VU0VSUyIsIjcwIjoiUk9MRV9NQU5BR0VfT1JHQU5JWkFUSU9OX0dFTkVSQUxTIiwiNzEiOiJST0xFX01BTkFHRV9PUkdBTklaQVRJT05fREVQQVJUTUVOVFMiLCI3MiI6IlJPTEVfTUFOQUdFX09XTl9TUFJJTlQiLCI3MyI6IlJPTEVfTUFOQUdFX0FMTF9TUFJJTlQiLCI3NCI6IlJPTEVfTUFOQUdFX0FMTF9HT0FMIiwiNzUiOiJST0xFX01BTkFHRV9PV05fR09BTCIsIjc2IjoiUk9MRV9NQU5BR0VfU0FMRV9GTE9XIiwiNzciOiJST0xFX01BTkFHRV9TQUxFX0ZJRUxEIiwiNzgiOiJST0xFX0RFTEVURV9DTE9TRURfU1BSSU5UIiwiNzkiOiJST0xFX1NFRV9BTExfT1BQT1JUVU5JVElFUyIsIjgwIjoiUk9MRV9TRUVfT1dOX09QUE9SVFVOSVRJRVMiLCI4MSI6IlJPTEVfRURJVF9BTExfT1BQT1JUVU5JVElFUyIsIjgyIjoiUk9MRV9FRElUX09XTl9PUFBPUlRVTklUSUVTIiwiODMiOiJST0xFX0FERF9BTExfT1BQT1JUVU5JVElFUyIsIjg0IjoiUk9MRV9BRERfT1dOX09QUE9SVFVOSVRJRVMiLCI4NSI6IlJPTEVfREVMRVRFX0FMTF9PUFBPUlRVTklUSUVTIiwiODYiOiJST0xFX0RFTEVURV9PV05fT1BQT1JUVU5JVElFUyIsIjg3IjoiUk9MRV9TRUVfQUxMX1NUQVRVU19DSEFOR0VEIiwiODgiOiJST0xFX1NFRV9PV05fU1RBVFVTX0NIQU5HRUQiLCI4OSI6IlJPTEVfQ1JFQVRFX1JFVklUSU9OUyIsIjkwIjoiUk9MRV9TRUVfUkVWSVRJT05TIiwiOTEiOiJST0xFX01BTkFHRV9TQUxFU19QRVJGT1JNQU5DRSIsIjkyIjoiUk9MRV9NQU5BR0VfQUxMX1NBTEVTX1BFUkZPUk1BTkNFIiwiOTMiOiJST0xFX0JPQVJEUyIsIjk0IjoiUk9MRV9NQU5BR0VfSU5URUdSQVRJT05TIiwiOTUiOiJST0xFX1JFQ1VSUkVOQ0VTIiwiOTYiOiJST0xFX01BTkFHRV9NSVNDRUxMQU5FT1VTIiwiOTciOiJST0xFX1NQUklOVF9TVEFUVVNfREVMRVRFX09XTiIsIjk4IjoiUk9MRV9TUFJJTlRfU1RBVFVTX0RFTEVURV9BTEwiLCIxMDQiOiJST0xFX1BST0pFQ1RfRVhQT1JUIiwiMTA1IjoiUk9MRV9UUkFDS19ERUxFVEVfT1dOIiwiMTA2IjoiUk9MRV9UUkFDS19ERUxFVEVfQUxMIiwiMTA3IjoiUk9MRV9DT05UQUNUX0VYUE9SVCIsIjEwOCI6IlJPTEVfSU5UUkFORVRfSE9NRV9WSUVXIiwiMTA5IjoiUk9MRV9JTlRFUkFDVElPTl9NQU5BR0UiLCIxMTAiOiJST0xFX0lOVEVSQUNUSU9OX0NSRUFURSIsIjEzMiI6IlJPTEVfRVhQT1JUX0FDQ09VTlRfUFJPRklUQUJJTElUWSJ9fSwidXNlcl9pZCI6MSwiZmlyc3RfbG9naW4iOmZhbHNlLCJwcm9maWxlX2lkIjoxLCJmdWxsX25hbWUiOiJKb24gRG9lIiwidXNlcl90eXBlIjoicm9vdG5ldCIsImxvY2FsZSI6ImVuIiwiaWF0IjoiMTUyMTEwOTQyOSJ9.xGVxzPMTdq0myVrTyQnpv_zv_vdXXOucRjYe102V8WAdppffj5t1qaxQRrYK-qo_UF01wa4XTx6HG-XDNqUqWUg2PqMZ8lGfYAtaDua6hMXl5_ad_R_5cX4txbJUVtXeX8emlQXr-wjxKZmKG2hIMEXDO0eUMNDvZQroSe92lqJ2XCp5_5uM7IWHA0JwN2U_0s7YZvfSlyI23OThpwtEFyqs0xVzYEy5DYRJTBQ8fOBdSwHJX1dXPuv_Nzg7lRm0HUU1PGU9zt8PwPAdqxqaiBhw7db-WavUQZU44QUqRPey8nTXhdOw7X8YoUmUN3X38yyra7cPogEJSKwkeqTVXoRpCq40eiUSIsuD2JjNHRLJahlRrmvpXqYFeNpnnK3Be0NE1JgMFjySyNon1_549ViPg6dkXVRJrBZsPqU04EH_i73gei-gKL0vapoy8UleRPbBemW2DgptG3Pp4bhwMB9bX7pN0vpuro4f91QJBORFAcyx_TV_YwxXz7WzsUtXZ-jrJFf2PRzJrOibttxt5YGhDQ-XWTPY3ct73bMaofXe2naYWRr4v8dzgCXGgF4gfJxhU_7qtRhC7ahd8MosIm3wasduBVd11rMiZ5pP6hvWWHQmMQ3FHoWYxyJIb4aKnN1ivf_qIcEe9ppHtY44N5NGokYH-Kzk1ZWZCe19AyI";

    public WorkflowViewModel(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    protected void getWorkflows(String auth) {
        this.auth = auth;
        try {
            if (Utils.isConnected()) {
                getWorkflowsFromService(auth2, 0);
            } else {
                getWorkflowsFromLocal(null);
            }
        } catch (InterruptedException | IOException e) {
            getWorkflowsFromLocal(null);
            //e.printStackTrace();
        }
    }

    private void getWorkflowsFromService(String auth, int page) {
        workflowRepository.getWorkflowsFromService(auth, page).subscribe(this::onServiceSuccess,
                this::getWorkflowsFromLocal);
    }

    private void getWorkflowsFromLocal(Throwable throwable) {
        workflowRepository.getWorkflowsFromInternal().subscribe(this::onWorkflowSuccess,
                this::onWorkflowFailure);
    }

    private void onServiceSuccess(WorkflowResponse workflowResponse) {
        workflows.addAll(workflowResponse.getList());
        if (!workflowResponse.getPager().isIsLastPage()) {
            //todo CAMBIAR AUTH
            getWorkflowsFromService(auth2, workflowResponse.getPager().getNextPage());
        } else {
            workflowRepository.setWorkflowsOnInternal(workflows).subscribe(this::onWorkflowSuccess,
                    this::onWorkflowFailure);
        }
    }

    private void onWorkflowSuccess(List<Workflow> workflowList) {
        unordered = new ArrayList<>();
        unordered.addAll(workflowList);
        mUserLiveData.setValue(workflowList);
    }

    private void onWorkflowFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<List<Workflow>> getObservableWorkflows() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public void applyFilters(WorkflowFragment.Sort sorting) {
        List<Workflow> workflows = mUserLiveData.getValue();

        switch (sorting.getSortingType()) {
            case NONE: {
                workflows = new ArrayList<>();
                workflows.addAll(unordered);
                break;
            }
            case BYNUMBER: {
                Collections.sort(workflows, (s1, s2) -> {
                    if (sorting.getNumberSortOrder().equals(WorkflowFragment.sortOrder.ASC)) {
                        /*For ascending order*/
                        return s1.getId() - s2.getId();
                    } else {
                        /*For descending order*/
                        return s2.getId() - s1.getId();
                    }
                });
                break;
            }
            case BYCREATE: {

                /*
                Collections.sort(workflows, (s1, s2) -> {
                    if (sorting.getNumberSortOrder().equals(WorkflowFragment.sortOrder.ASC)) {


                        return s1.getId() - s2.getId();
                    } else {

                        return s2.getId() - s1.getId();
                    }
                });*/
                break;
            }
            case BYUPDATE: {
                //todo falta este dato del servicio.
                break;
            }
        }

        mUserLiveData.setValue(workflows);
    }
}
