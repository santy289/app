package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;

import java.util.List;

public class RightDrawerOptionsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<WorkflowTypeMenu> menus;

    public static final int TYPE = 0;
    public static final int CATEGORY = 1;
    public static final int NO_CATEGORY = R.string.no_category;
    public static final String NO_CATEGORY_LABEL = "no_category";

    public RightDrawerOptionsAdapter(LayoutInflater inflater, List<WorkflowTypeMenu> menus) {
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
        WorkflowTypeMenu menu = menus.get(position);
        int type = menu.getRowType();
        return type == TYPE || type == NO_SELECTION;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkflowTypeMenu menu = menus.get(position);
        int itemRowType = menu.getRowType();
        Resources resources = parent.getResources();
        TextView textView;
        ImageView checkImage;

        switch (itemRowType) {
            case TYPE:
                convertView = inflater
                        .inflate(R.layout.right_drawer_filter_workflow_type_item, null);
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                checkImage = convertView.findViewById(R.id.right_drawer_image_checkmark);
                if (menu.isSelected()) {
                    checkImage.setVisibility(View.VISIBLE);
                    textView.setTextColor(resources.getColor(R.color.colorAccent));
                } else {
                    checkImage.setVisibility(View.INVISIBLE); //INVISIBLE - for constraints
                    textView.setTextColor(resources.getColor(R.color.black));
                }
                if (TextUtils.isEmpty(menu.getLabel())) {
                    textView.setText(resources.getString(menu.getResLabel()));
                } else {
                    textView.setText(menu.getLabel());
                }
                TextView workflowCount = convertView.findViewById(R.id.right_drawer_workflow_count);
                if (menu.getWorkflowCount() != null) {
                    workflowCount.setVisibility(View.VISIBLE);
                    workflowCount.setText(String.valueOf(menu.getWorkflowCount()));
                } else {
                    workflowCount.setVisibility(View.GONE);
                }
                break;
            case CATEGORY:
                convertView = inflater.inflate(R.layout.right_drawer_filter_category_item, null);
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                String label;
                if (menu.getLabel().equals(NO_CATEGORY_LABEL)) {
                    label = convertView.getContext().getString(NO_CATEGORY);
                } else {
                    label = menu.getLabel();
                }
                textView.setText(label);
//                convertView.setEnabled(false);
                break;
            case NO_SELECTION:
                convertView = inflater.inflate(R.layout.right_drawer_filter_item, null);
                textView = convertView.findViewById(R.id.right_drawer_item_title);
                textView.setText(menu.getLabel());
                break;
        }

        return convertView;
    }
}
