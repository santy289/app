package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.workflowlist.FilterSettings;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkflowTypeMenu menu = menus.get(position);
        int itemRowType = menu.getRowType();
        Resources resources = parent.getResources();
        TextView textView;
        switch (itemRowType) {
            case TYPE:
                convertView = inflater.inflate(R.layout.right_drawer_filter_item, null);
                textView = convertView.findViewById(R.id.right_drawer_item_title);

                if (menu.getId() == FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID) {
                    String label = resources.getString(R.string.workflow_type);
                    textView.setText(label);
                    menu.setLabel(label);
                    textView = convertView.findViewById(R.id.right_drawer_item_subtitle);

                    String subtitle = menu.getSubTitle();
                    if (TextUtils.isEmpty(subtitle)) {
                        textView.setText(resources.getString(R.string.no_selection));
                        textView.setTextColor(resources.getColor(R.color.dark_gray));
                    } else {
                        textView.setText(subtitle);
                        textView.setTextColor(resources.getColor(R.color.colorAccent));
                    }
                    break;
                }

                textView.setText(menu.getLabel());
                textView = convertView.findViewById(R.id.right_drawer_item_subtitle);
                String subtitle = menu.getSubTitle();
                if (TextUtils.isEmpty(subtitle)) {
                    textView.setText(resources.getString(R.string.no_selection));
                    textView.setTextColor(resources.getColor(R.color.dark_gray));
                } else {
                    textView.setText(menu.getSubTitle());
                    textView.setTextColor(resources.getColor(R.color.colorAccent));
                }

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
