package com.rootnetapp.rootnetintranet.ui.createworkflow.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;

public class ValidateFormDialog extends DialogFragment {

    public ValidateFormDialog() { }

    public static ValidateFormDialog newInstance(String title, String message, String[] listText) {
        ValidateFormDialog vaildateDialog = new ValidateFormDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putStringArray("list", listText);
        vaildateDialog.setArguments(args);
        return vaildateDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_validation, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.form_dialog_list);
        TextView messageView = view.findViewById(R.id.form_dialog_message);
        Button button = view.findViewById(R.id.form_dialog_button);
        button.setOnClickListener( v -> dismiss());
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String[] list = getArguments().getStringArray("list");
        if (list == null) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setAdapter(
                    new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            list
                    )
            );
        }

        getDialog().setTitle(title);
        messageView.setText(message);
    }
}
