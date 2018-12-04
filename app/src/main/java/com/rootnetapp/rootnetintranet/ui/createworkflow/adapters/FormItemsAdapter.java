package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
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
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class FormItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private FragmentManager mFragmentManager;
    private List<BaseFormItem> mDataset;
    private boolean hasToEvaluateValid;

    public FormItemsAdapter(Context context, FragmentManager fragmentManager,
                            List<BaseFormItem> dataset) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
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

    public void setHasToEvaluateValid(boolean hasToEvaluateValid) {
        this.hasToEvaluateValid = hasToEvaluateValid;
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

    private BaseFormItem getItem(int position) {
        return mDataset.get(position);
    }

    public int getItemPosition(BaseFormItem item) {
        return mDataset.indexOf(item);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void populateTextInputView(TextInputViewHolder holder, int position) {
        TextInputFormItem item = (TextInputFormItem) getItem(position);

        holder.getBinding().tvTitle.setText(item.getTitle());

        setTextInputParams(holder.getBinding().etInput, item.getInputType());

        holder.getBinding().etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setValue(s.toString());
            }
        });

        if (hasToEvaluateValid && !item.isValid()) {
            item.setErrorMessage(item.getErrorMessage());
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            item.setErrorMessage(null);
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    private void setTextInputParams(AppCompatEditText etInput, String type) {
        switch (type) {

            case TextInputFormItem.InputType.TEXT_AREA:
                etInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                etInput.setMaxLines(5);
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

        List<Option> options = new ArrayList<>(item.getOptions());
        String hint = mContext.getString(R.string.no_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        holder.getBinding().spInput.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        options));

        if (holder.getBinding().spInput.getOnItemSelectedListener() == null) {
            holder.getBinding().spInput
                    .setSelection(0, false); //workaround so the listener won't be called on init
            holder.getBinding().spInput.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {

                            // the user has selected the No Selection option
                            if (position == 0) {
                                //fixme the spinner stops working
                                item.setValue(null);
                                item.getOnSelectedListener().onSelected(item);
                                return;
                            }

                            // this prevents the listener to be triggered by setSelection
                            Object tag = holder.getBinding().spInput.getTag();
                            if (tag == null || (int) tag != position) {
                                int index = position--; // because of the No Selection option
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
            index++; // because of the No Selection option
            holder.getBinding().spInput.setTag(index);
            holder.getBinding().spInput.setSelection(index);
        }

        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    private void populateBooleanView(BooleanViewHolder holder, int position) {
        BooleanFormItem item = (BooleanFormItem) getItem(position);

        holder.getBinding().switchInput.setText(item.getTitle());
        holder.getBinding().switchInput.setOnCheckedChangeListener(
                (buttonView, isChecked) -> item.setValue(isChecked));
    }

    private void populateDateView(DateViewHolder holder, int position) {
        DateFormItem item = (DateFormItem) getItem(position);

        holder.getBinding().tvSelectedDate.setOnClickListener(v -> {
            // the style mdtp_ActionButton.Text must be overridden in styles.xml for MaterialComponents
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    (view, year, monthOfYear, dayOfMonth) -> {
                        Date date = Utils.getDateFromIntegers(year, monthOfYear, dayOfMonth);

                        holder.getBinding().tvSelectedDate
                                .setText(Utils.getFormattedDate(date, item.getDateFormat()));
                        item.setValue(date);
                    }
            );

            //region Data
            dpd.setTitle(item.getTitle());

            Calendar calendar = Calendar.getInstance();
            if (item.getMinDate() != null) {
                calendar.setTime(item.getMinDate());
                dpd.setMinDate(calendar);
            }
            if (item.getMaxDate() != null) {
                calendar.setTime(item.getMaxDate());
                dpd.setMinDate(calendar);
            }
            //endregion

            //region UI
            dpd.setOkText(R.string.accept);
            dpd.setCancelText(R.string.cancel);
            //endregion

            dpd.show(mFragmentManager, String.valueOf(item.getTag()));
        });

        holder.getBinding().tvTitle.setText(item.getTitle());

        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().tvSelectedDate.setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().tvSelectedDate.setBackgroundResource(R.drawable.spinner_bg);
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
