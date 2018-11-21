package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemBooleanBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemCurrencyBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemDateBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FieldData;
import com.rootnetapp.rootnetintranet.ui.createworkflow.enums.FormItemViewType;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FormItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<FieldData> mDataset;

    public FormItemsAdapter(Context context, List<FieldData> dataset) {
        this.mContext = context;
        this.mDataset = dataset;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {

            case FormItemViewType.TEXT_INPUT:
                return new TextInputViewHolder(FormItemTextInputBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.SINGLE_CHOICE:
                return new SingleChoiceViewHolder(FormItemSingleChoiceBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.BOOLEAN:
                return new BooleanViewHolder(FormItemBooleanBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.DATE:
                return new DateViewHolder(FormItemDateBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.CURRENCY:
                return new CurrencyViewHolder(FormItemCurrencyBinding
                        .inflate(layoutInflater, viewGroup, false));

            default:
                throw new IllegalStateException("Invalid ViewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemCount() < 1) {
            return;
        }

        FieldData fieldData = getItem(position);

        //todo populate the corresponding views

        switch (getItemViewType(position)) {
            case FormItemViewType.TEXT_INPUT:
                TextInputViewHolder textInputViewHolder = (TextInputViewHolder) holder;
                break;

            case FormItemViewType.SINGLE_CHOICE:
                SingleChoiceViewHolder singleChoiceViewHolder = (SingleChoiceViewHolder) holder;
                break;

            case FormItemViewType.BOOLEAN:
                BooleanViewHolder booleanViewHolder = (BooleanViewHolder) holder;
                break;

            case FormItemViewType.DATE:
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                break;

            case FormItemViewType.CURRENCY:
                CurrencyViewHolder currencyViewHolder = (CurrencyViewHolder) holder;
                break;

            default:
                throw new IllegalStateException("Invalid ViewType");
        }
    }

    @Override
    public @FormItemViewType
    int getItemViewType(int position) {
        return getItem(position).viewType;
    }

    protected FieldData getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
