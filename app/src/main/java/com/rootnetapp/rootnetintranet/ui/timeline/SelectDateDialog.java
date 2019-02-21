package com.rootnetapp.rootnetintranet.ui.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.DialogSelectDateBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

import java.util.Date;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

/**
 * Created by root on 11/04/18.
 */

public class SelectDateDialog extends DialogFragment {

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
        String start = Utils.getFormattedDateFromIntegers(
                binding.pickerStartdate.getYear(),
                binding.pickerStartdate.getMonth(),
                binding.pickerStartdate.getDayOfMonth(),
                0,
                0,
                0
        );

        String end = Utils.getFormattedDateFromIntegers(
                binding.pickerEnddate.getYear(),
                binding.pickerEnddate.getMonth(),
                binding.pickerEnddate.getDayOfMonth(),
                23,
                59,
                59
        );

        Date dateStart = Utils.getDateFromString(start, Utils.SERVER_DATE_FORMAT_NO_TIMEZONE);
        Date dateEnd = Utils.getDateFromString(end, Utils.SERVER_DATE_FORMAT_NO_TIMEZONE);

        if (dateStart != null && dateStart.after(dateEnd)) {
            if (manInterface != null) {
                manInterface.showToastMessage(
                        R.string.select_date_dialog_end_date_before_start_date_error);
            } else if (timeInterface != null) {
                timeInterface.showToastMessage(
                        R.string.select_date_dialog_end_date_before_start_date_error);
            }

            return;
        }

        if (manInterface != null) {
            manInterface.setDate(start, end);
        } else if (timeInterface != null) {
            timeInterface.setDate(start, end);
        }

        dismiss();
    }

}
