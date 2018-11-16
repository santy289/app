package com.rootnetapp.rootnetintranet.ui.createworkflow.customviews;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;

import java.util.ArrayList;
import java.util.List;

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
