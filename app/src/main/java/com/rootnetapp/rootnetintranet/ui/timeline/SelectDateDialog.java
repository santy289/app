package com.rootnetapp.rootnetintranet.ui.timeline;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DialogSelectDateBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

/**
 * Created by root on 11/04/18.
 */

public class SelectDateDialog extends DialogFragment{

    private DialogSelectDateBinding binding;
    private TimelineInterface timeInterface;
    private ManagerInterface manInterface;

    public static SelectDateDialog newInstance(TimelineInterface anInterface) {
        SelectDateDialog fragment = new SelectDateDialog();
        fragment.timeInterface = anInterface;
        fragment.manInterface = null;
        return fragment;
    }

    public static SelectDateDialog newInstance(ManagerInterface anInterface) {
        SelectDateDialog fragment = new SelectDateDialog();
        fragment.manInterface = anInterface;
        fragment.timeInterface = null;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_select_date, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding.btnClose.setOnClickListener(view1 -> dismiss());
        binding.btnAccept.setOnClickListener(view1 -> setDates());
        return view;
    }

    private void setDates() {
        String start = binding.pickerStartdate.getYear()+"-"
                +(binding.pickerStartdate.getMonth()+1)+"-"
                +binding.pickerStartdate.getDayOfMonth();
        String end = binding.pickerEnddate.getYear()+"-"
                +(binding.pickerStartdate.getMonth()+1)+"-"
                +binding.pickerEnddate.getDayOfMonth();

        if(manInterface!=null){
            manInterface.setDate(start, end);
        }else if(timeInterface!=null){
            timeInterface.setDate(start, end);
        }
        dismiss();
    }

}
