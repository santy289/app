package com.rootnetapp.rootnetintranet.ui.workflowlist.createworkflow;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;

/**
 * Created by root on 21/03/18.
 */

public class CreateWorkflowDialog extends DialogFragment{

    public static CreateWorkflowDialog newInstance() {
        CreateWorkflowDialog fragment = new CreateWorkflowDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_workflow, container, false);
        /*setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);
        prefs = PreferenceHandler.getInstance(getContext());
        startSet = prefs.getBoolean(getString(R.string.isStartSet), false);
        endSet = prefs.getBoolean(getString(R.string.isEndSet), false);
        Set<String> set = prefs.getStringSet(getString(R.string.days), null);
        dias = new ArrayList<>();
        if (set != null) {
            dias.addAll(set);
        }
        for (String item : dias) {
            switch (Integer.parseInt(item)) {
                case Calendar.MONDAY: {
                    check_lunes.setChecked(true);
                    break;
                }
                case Calendar.TUESDAY: {
                    check_martes.setChecked(true);
                    break;
                }
                case Calendar.WEDNESDAY: {
                    check_miercoles.setChecked(true);
                    break;
                }
                case Calendar.THURSDAY: {
                    check_jueves.setChecked(true);
                    break;
                }
                case Calendar.FRIDAY: {
                    check_viernes.setChecked(true);
                    break;
                }
                case Calendar.SATURDAY: {
                    check_sabado.setChecked(true);
                    break;
                }
                case Calendar.SUNDAY: {
                    check_domingo.setChecked(true);
                    break;
                }
            }
        }

        if(startSet){
            startHour = prefs.getInt(getString(R.string.startHour), 0);
            startMinute = prefs.getInt(getString(R.string.startMinute), 0);
            button_start_time.setText(startHour + ":" + startMinute);
        }
        if(endSet){
            endHour = prefs.getInt(getString(R.string.endHour), 0);
            endMinute = prefs.getInt(getString(R.string.endMinute), 0);
            button_end_time.setText(endHour + ":" + endMinute);
        }*/
        return view;
    }

}
