package com.rootnetapp.rootnetintranet.ui.createworkflow.customviews;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 26/03/18.
 */

public abstract class CustomSpinner extends FrameLayout {

    protected List<String> spn_data;
    protected Spinner spinner;
    protected FragmentActivity activity;

    public abstract void subscribe();

    public CustomSpinner(FragmentActivity activity, Field field) {
        super(activity);
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.prototype_list, this, true);
        TextView title = findViewById(R.id.field_title);
        title.setText(field.getFieldName());
        spinner = findViewById(R.id.field_spinner);
        spn_data = new ArrayList<>();
    }

}
