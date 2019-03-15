package com.rootnetapp.rootnetintranet.ui.workflowdetail.files.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DocumentsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragmentInterface;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 04/04/18.
 */

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsViewholder> {

    public List<Preset> totalDocuments;
    private List<DocumentsFile> files;
    private Context context;
    private FilesFragmentInterface mFilesFragmentInterface;
    private boolean showFileDownloadButton;
    private boolean showTemplateDownloadButton;

    public DocumentsAdapter(FilesFragmentInterface filesFragmentInterface,
                            List<Preset> totalDocuments, List<DocumentsFile> files) {
        this.mFilesFragmentInterface = filesFragmentInterface;
        this.totalDocuments = totalDocuments;
        this.files = files;
    }

    public void setShowFileDownloadButton(boolean show) {
        showFileDownloadButton = show;
    }

    public void setShowTemplateDownloadButton(boolean show) {
        showTemplateDownloadButton = show;
    }

    @NonNull
    @Override
    public DocumentsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        DocumentsItemBinding itemBinding =
                DocumentsItemBinding.inflate(layoutInflater, viewGroup, false);
        return new DocumentsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsViewholder holder, int i) {
        Preset item = totalDocuments.get(i);
        DocumentsFile file = null;
        for (DocumentsFile aux : files) {
            if (item.getId() == aux.getPresetId()) {
                file = aux;
                break;
            }
        }
        holder.binding.tvName.setText(item.getName());
        holder.binding.chbxItem.setOnCheckedChangeListener(
                (compoundButton, b) -> item.setSelected(b));
        item.setSelected(holder.binding.chbxItem.isChecked());
        if (file != null) {
            holder.binding.imgDownload.setVisibility(showFileDownloadButton ? View.VISIBLE : View.GONE);

            holder.binding.imgUploaded.setImageResource(R.drawable.ic_check_accent_24dp);
            holder.binding.imgUploaded
                    .setColorFilter(ContextCompat.getColor(context, R.color.green),
                            android.graphics.PorterDuff.Mode.SRC_IN);
            holder.binding.tvFileName.setText(file.getName());
            String date = file.getCreatedAt().split(" ")[0];
            String hour = file.getCreatedAt().split(" ")[1];
            holder.binding.tvDate.setText(date + " - " + hour);
        } else {
            if (item.getPresetFile() != null) {
                holder.binding.tvFileName.setText(item.getPresetFile().getFileName());
                holder.binding.imgDownload.setVisibility(showTemplateDownloadButton ? View.VISIBLE : View.GONE);
            } else {
                holder.binding.imgDownload.setVisibility(View.GONE);
            }
            holder.binding.imgUploaded.setImageResource(R.drawable.ic_close_black_24dp);
            holder.binding.imgUploaded.setColorFilter(ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (i % 2 == 0) {
            holder.binding.lytHeader
                    .setBackgroundColor(context.getResources().getColor(R.color.workflowListBg));
        }
        holder.binding.lytHeader.setOnClickListener(view -> {
            if (holder.binding.lytDetails.getVisibility() == View.GONE) {
                holder.binding.imgArrow
                        .setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                holder.binding.imgArrow
                        .setColorFilter(ContextCompat.getColor(context, R.color.arrow),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetails.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgArrow
                        .setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                holder.binding.imgArrow
                        .setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetails.setVisibility(View.GONE);
            }
        });

        DocumentsFile finalFile = file;
        holder.binding.imgDownload.setOnClickListener(v -> {
            if (finalFile != null) {
                // file exists, proceed to download it
                mFilesFragmentInterface.downloadDocumentFile(finalFile);
            } else {
                // file has not been uploaded, download the preset instead
                mFilesFragmentInterface.downloadPreset(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return totalDocuments.size();
    }
}
