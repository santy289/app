package com.rootnetapp.rootnetintranet.ui.main.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;

/**
 * Created by root on 24/04/18.
 */

public class SearchAdapter extends CursorAdapter {

    private MainActivityInterface anInterface;

    public SearchAdapter(Context context, Cursor c, MainActivityInterface anInterface) {
        super(context, c, false);
        this.anInterface = anInterface;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.suggestion_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String key = cursor.getString(cursor.getColumnIndexOrThrow("workflow_type_key"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("type_name"));
        String state = cursor.getString(cursor.getColumnIndexOrThrow("state_name"));
        TextView workflowId = view.findViewById(R.id.tv_id);
        TextView workflowType = view.findViewById(R.id.tv_type);
        TextView workflowState = view.findViewById(R.id.tv_state);
        workflowId.setText(key);
        workflowType.setText(type);
        workflowState.setText(state);
        view.findViewById(R.id.lyt_info).setOnClickListener(view1 ->
                anInterface.showWorkflow(Integer.parseInt(id)));
    }

}