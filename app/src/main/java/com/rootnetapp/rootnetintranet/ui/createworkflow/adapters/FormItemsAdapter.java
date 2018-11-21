package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemBooleanBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemCurrencyBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemDateBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

public class FormItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<BaseFormItem> mDataset;

    public FormItemsAdapter(Context context, List<BaseFormItem> dataset) {
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

        switch (getItemViewType(position)) {

            case FormItemViewType.TEXT_INPUT:
                populateTextInputView((TextInputViewHolder) holder, position);
                break;

            case FormItemViewType.SINGLE_CHOICE:
                populateSingleChoiceView((SingleChoiceViewHolder) holder, position);
                break;

            case FormItemViewType.BOOLEAN:
                populateBooleanView((BooleanViewHolder) holder, position);
                break;

            case FormItemViewType.DATE:
                populateDateView((DateViewHolder) holder, position);
                break;

            case FormItemViewType.CURRENCY:
                populateCurrencyView((CurrencyViewHolder) holder, position);
                break;

            default:
                throw new IllegalStateException("Invalid ViewType");
        }
    }

    @Override
    public @FormItemViewType
    int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    protected BaseFormItem getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void populateTextInputView(TextInputViewHolder holder, int position) {
        TextInputFormItem item = (TextInputFormItem) getItem(position);

        holder.getBinding().tvTitle.setText(item.getTitle());

        setTextInputParams(holder.getBinding().etInput, item.getInputType());
    }

    private void setTextInputParams(AppCompatEditText etInput, int type) {
        switch (type) {

            case TextInputFormItem.InputType.TEXT_AREA:
                etInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;

            case TextInputFormItem.InputType.PHONE:
                etInput.setInputType(InputType.TYPE_CLASS_PHONE);
                break;

            case TextInputFormItem.InputType.EMAIL:
                etInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;

            case TextInputFormItem.InputType.TEXT:
            default:
                etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private void populateSingleChoiceView(SingleChoiceViewHolder holder, int position) {
//        FieldData fieldData = getItem(position);
//
//        holder.getBinding().tvTitle.setText(fieldData.label);
//        holder.getBinding().spSteps.setAdapter(
//                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
//                        fieldData.options));
    }

    private void populateBooleanView(BooleanViewHolder holder, int position) {
//        FieldData fieldData = getItem(position);
//
//        holder.getBinding().tvTitle.setText(fieldData.label);
    }

    private void populateDateView(DateViewHolder holder, int position) {
//        FieldData fieldData = getItem(position);
//
//        holder.getBinding().tvTitle.setText(fieldData.label);
    }

    private void populateCurrencyView(CurrencyViewHolder holder, int position) {
//        FieldData fieldData = getItem(position);
//
//        holder.getBinding().tvTitle.setText(fieldData.label);
//        holder.getBinding().spCurrency.setAdapter(
//                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
//                        fieldData.options));
    }
}
