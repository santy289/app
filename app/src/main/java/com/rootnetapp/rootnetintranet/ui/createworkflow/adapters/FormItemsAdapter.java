package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.rootnetapp.rootnetintranet.databinding.FormItemBooleanBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemCurrencyBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemDateBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BooleanFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.CurrencyFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DateFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

public class FormItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BaseFormItem> mDataset;

    public FormItemsAdapter(Context context, List<BaseFormItem> dataset) {
        this.mContext = context;
        this.mDataset = dataset;
    }

    public void addItem(BaseFormItem item) {
        mDataset.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setData(List<BaseFormItem> list) {
        mDataset = list;
        notifyDataSetChanged();
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

    private void setTextInputParams(AppCompatEditText etInput, String type) {
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

            case TextInputFormItem.InputType.NUMBER:
                etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case TextInputFormItem.InputType.DECIMAL:
                etInput.setInputType(
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;

            case TextInputFormItem.InputType.TEXT:
            default:
                etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private void populateSingleChoiceView(SingleChoiceViewHolder holder, int position) {
        SingleChoiceFormItem item = (SingleChoiceFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);
        holder.getBinding().spSteps.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        item.getOptions()));
        if (holder.getBinding().spSteps.getOnItemSelectedListener() == null) {
            holder.getBinding().spSteps
                    .setSelection(0, false); //workaround so the listener won't be called on init
            holder.getBinding().spSteps.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {
                            // this prevents the listener to be triggered by setSelection
                            Object tag = holder.getBinding().spSteps.getTag();
                            if (tag == null || (int) tag != position) {
                                item.setValue(item.getOptions().get(position));
                                item.getOnSelectedListener().onSelected(item);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
        } else {
            // this prevents the listener to be triggered by setSelection
            int index = item.getOptions().indexOf(item.getValue());
            holder.getBinding().spSteps.setTag(index);
            holder.getBinding().spSteps.setSelection(index);
        }
    }

    private void populateBooleanView(BooleanViewHolder holder, int position) {
        BooleanFormItem item = (BooleanFormItem) getItem(position);

        holder.getBinding().tvTitle.setText(item.getTitle());
    }

    private void populateDateView(DateViewHolder holder, int position) {
        DateFormItem item = (DateFormItem) getItem(position);

        holder.getBinding().tvTitle.setText(item.getTitle());
        if (item.getMinDate() != null) {
            holder.getBinding().dpInput.setMinDate(item.getMinDate().getTime());
        }
        if (item.getMaxDate() != null) {
            holder.getBinding().dpInput.setMaxDate(item.getMaxDate().getTime());
        }
    }

    private void populateCurrencyView(CurrencyViewHolder holder, int position) {
        CurrencyFormItem item = (CurrencyFormItem) getItem(position);

        holder.getBinding().tvTitle.setText(item.getTitle());
        holder.getBinding().spCurrency.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        item.getOptions()));
    }
}
