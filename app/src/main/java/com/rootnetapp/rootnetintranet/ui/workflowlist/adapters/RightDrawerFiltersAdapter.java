package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;

import java.util.List;

public class RightDrawerFiltersAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<WorkflowTypeMenu> menus;

    public static final int TYPE = 0;
    public static final int CATEGORY = 1;
    public static final int NO_CATEGORY = R.string.no_category;
    public static final String NO_CATEGORY_LABEL = "no_category";

    public RightDrawerFiltersAdapter(LayoutInflater inflater, List<WorkflowTypeMenu> menus) {
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

//    @Override
//    public boolean isEnabled(int position) {
//        WorkflowTypeMenu menu = menus.get(position);
//        int type =  menu.getRowType();
//        return type == TYPE || type == NO_SELECTION;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkflowTypeMenu menu = menus.get(position);
        int itemRowType = menu.getRowType();

        TextView textView;
        switch (itemRowType) {
            case TYPE:
                convertView = inflater.inflate(R.layout.right_drawer_filter_item, null);
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                textView.setText(menu.getLabel());
                textView = convertView.findViewById(R.id.right_drawer_item_subtitle);
                textView.setText(menu.getSubTitle());
                break;
            case CATEGORY:
//                convertView = inflater.inflate(R.layout.spinner_category_layout, null);
//                textView = convertView.findViewById(R.id.spinner_row_category);
//                String label;
//                if (menu.getLabel().equals(NO_CATEGORY_LABEL)) {
//                    label = convertView.getContext().getString(NO_CATEGORY);
//                } else {
//                    label = menu.getLabel();
//                }
//                textView.setText(label);
//                convertView.setEnabled(false);
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                textView.setText("Something went wrong");
                break;
            case NO_SELECTION:
//                convertView = inflater.inflate(R.layout.spinner_type_layout, null);
//                textView = convertView.findViewById(R.id.spinner_row_type);
//                textView.setText(menu.getLabel());
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                textView.setText("Something went wrong");
                break;
            default:
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                textView.setText("Something went wrong");
                break;
        }

        return convertView;
    }
}
