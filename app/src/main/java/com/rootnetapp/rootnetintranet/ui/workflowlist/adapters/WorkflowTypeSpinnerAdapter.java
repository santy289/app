package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.SpinnerWorkflowTypeMenu;

import java.util.List;

public class WorkflowTypeSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SpinnerWorkflowTypeMenu> menus;

    public static final int TYPE = 0;
    public static final int CATEGORY = 1;
    public static final int NO_CATEGORY = R.string.no_category;
    public static final String NO_CATEGORY_LABEL = "no_category";

    public WorkflowTypeSpinnerAdapter(LayoutInflater inflater, List<SpinnerWorkflowTypeMenu> menus) {
        this.inflater = inflater;
        this.menus = menus;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        SpinnerWorkflowTypeMenu menu = menus.get(position);
        int type =  menu.getRowType();
        return type == TYPE || type == NO_SELECTION;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpinnerWorkflowTypeMenu menu = menus.get(position);
        int itemRowType = menu.getRowType();

        TextView textView;
        switch (itemRowType) {
            case TYPE:
                convertView = inflater.inflate(R.layout.spinner_type_layout, null);
                textView = convertView.findViewById(R.id.spinner_row_type);
                textView.setText(menu.getLabel());
                break;
            case CATEGORY:
                convertView = inflater.inflate(R.layout.spinner_category_layout, null);
                textView = convertView.findViewById(R.id.spinner_row_category);
                String label;
                if (menu.getLabel().equals(NO_CATEGORY_LABEL)) {
                    label = convertView.getContext().getString(NO_CATEGORY);
                } else {
                    label = menu.getLabel();
                }
                textView.setText(label);
                convertView.setEnabled(false);
                break;
            case NO_SELECTION:
                convertView = inflater.inflate(R.layout.spinner_type_layout, null);
                textView = convertView.findViewById(R.id.spinner_row_type);
                textView.setText(menu.getLabel());
                break;
        }

        return convertView;
    }
}
