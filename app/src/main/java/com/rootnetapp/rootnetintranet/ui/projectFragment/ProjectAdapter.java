package com.rootnetapp.rootnetintranet.ui.projectFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.projectFragment.models.ProjectDataResult;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private final List<ProjectDataResult> projectDataResults;

    public ProjectAdapter(List<ProjectDataResult> projectDataResults) {
        this.projectDataResults = projectDataResults;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectDataResult projectDataResult = projectDataResults.get(position);
        holder.project_title.setText(projectDataResult.getTitle());
        holder.project_type.setText(projectDataResult.getProject_type().name);
        holder.project_state.setText(projectDataResult.getKey_code());
        holder.project_created.setText(projectDataResult.getCreated_at().toString());
        holder.project_updated.setText(projectDataResult.getUpdated_at().toString());

    }

    @Override
    public int getItemCount() {
        return projectDataResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView project_title;
        public TextView project_type;
        public TextView project_state;
        public TextView project_created;
        public TextView project_updated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            project_title = (TextView) itemView.findViewById(R.id.project_title);
            project_type = (TextView) itemView.findViewById(R.id.project_type);
            project_state = (TextView) itemView.findViewById(R.id.project_state);
            project_created = (TextView) itemView.findViewById(R.id.project_created);
            project_updated = (TextView) itemView.findViewById(R.id.project_updated);
        }
    }
}
