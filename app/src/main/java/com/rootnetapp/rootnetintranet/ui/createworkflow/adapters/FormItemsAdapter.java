package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
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
import com.rootnetapp.rootnetintranet.databinding.FormItemFileBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemMultipleChoiceBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemPhoneBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;
import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BooleanFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.CurrencyFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DateFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FileFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FormItemViewType;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.MultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.PhoneFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragmentInterface;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FormItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private FragmentManager mFragmentManager;
    private List<BaseFormItem> mDataset;
    private boolean hasToEvaluateValid;
    private CreateWorkflowFragmentInterface mFragmentInterface;

    public FormItemsAdapter(Context context, FragmentManager fragmentManager,
                            List<BaseFormItem> dataset,
                            CreateWorkflowFragmentInterface fragmentInterface) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.mDataset = dataset;
        this.mFragmentInterface = fragmentInterface;
    }

    public void addItem(BaseFormItem item) {
        mDataset.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setData(List<BaseFormItem> list) {
        mDataset = list;
        notifyDataSetChanged();
        getItemCount();
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

            case FormItemViewType.MULTIPLE_CHOICE:
                return new MultipleChoiceViewHolder(FormItemMultipleChoiceBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.PHONE:
                return new PhoneViewHolder(FormItemPhoneBinding
                        .inflate(layoutInflater, viewGroup, false));

            case FormItemViewType.FILE:
                return new FileViewHolder(FormItemFileBinding
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

            case FormItemViewType.MULTIPLE_CHOICE:
                populateMultipleChoiceView((MultipleChoiceViewHolder) holder, position);
                break;

            case FormItemViewType.PHONE:
                populatePhoneView((PhoneViewHolder) holder, position);
                break;

            case FormItemViewType.FILE:
                populateFileView((FileViewHolder) holder, position);
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

    //region Populate Views

    /**
     * Handles the view for the {@link TextInputFormItem}. Displays the UI according to the
     * visibility, enabled and validation params.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateTextInputView(TextInputViewHolder holder, int position) {
        TextInputFormItem item = (TextInputFormItem) getItem(position);

        //set title
        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        //set value
        holder.getBinding().etInput.setText(item.getValue());

        setTextInputParams(holder.getBinding().etInput, item.getInputType());

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().etInput.setEnabled(false);
            return;
        } else {
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().etInput.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            item.setErrorMessage(item.getErrorMessage());
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            item.setErrorMessage(null);
            holder.getBinding().etInput.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    /**
     * Maps the appropriate input type for the EditText according to our server type.
     *
     * @param etInput the EditText to be set
     * @param type    server input type.
     */
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

            case TextInputFormItem.InputType.LINK:
                etInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                break;

            case TextInputFormItem.InputType.TEXT:
            default:
                etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    /**
     * Handles the view for the {@link SingleChoiceFormItem}. Displays the UI according to the
     * visibility, enabled and validation params.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateSingleChoiceView(SingleChoiceViewHolder holder, int position) {
        SingleChoiceFormItem item = (SingleChoiceFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        List<Option> options = new ArrayList<>(item.getOptions());

        //add hint
        String hint = mContext.getString(R.string.no_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        //check for selection
        int selection = 0;
        if (item.getValue() != null) {
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                if (item.getValue() == option) selection = i;
            }
        }

        //create the adapter
        holder.getBinding().spInput.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        options));

        //only creates the listener once.
        if (holder.getBinding().spInput.getOnItemSelectedListener() == null) {
            holder.getBinding().spInput
                    .setSelection(selection,
                            false); //workaround so the listener won't be called on init
            holder.getBinding().spInput.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {

                            // this prevents the listener to be triggered by setSelection
                            Object tag = holder.getBinding().spInput.getTag();
                            if (tag == null || (int) tag != position) {

                                // the user has selected the No Selection option
                                if (position == 0) {
                                    item.setValue(null);
                                    if (item.getOnSelectedListener() != null) {
                                        item.getOnSelectedListener().onSelected(item);
                                    }
                                    return;
                                }

                                // the user has selected a valid option
                                int index = position - 1; // because of the No Selection option
                                item.setValue(item.getOptions().get(index));
                                if (item.getOnSelectedListener() != null) {
                                    item.getOnSelectedListener().onSelected(item);
                                }
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

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().spInput.setEnabled(false);
            return;
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().spInput.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    /**
     * Handles the view for the {@link BooleanFormItem}. Displays the UI according to the visibility
     * params.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateBooleanView(BooleanViewHolder holder, int position) {
        BooleanFormItem item = (BooleanFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().switchInput.setText(title);

        holder.getBinding().switchInput.setChecked(item.getValue());
        holder.getBinding().switchInput.setOnCheckedChangeListener(
                (buttonView, isChecked) -> item.setValue(isChecked));

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        holder.getBinding().switchInput.setEnabled(item.isEnabled());
    }

    /**
     * Handles the view for the {@link DateFormItem}. Displays the UI according to the visibility,
     * enabled and validation params. Uses a 3rd party library to handle the dialog that will open
     * upon user interaction.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateDateView(DateViewHolder holder, int position) {
        DateFormItem item = (DateFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        final String finalTitle = title;
        holder.getBinding().tvTitle.setText(finalTitle);

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
            dpd.setTitle(finalTitle);

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

        if (item.getValue() == null) {
            holder.getBinding().tvSelectedDate.setText(null);
        } else {
            holder.getBinding().tvSelectedDate
                    .setText(Utils.getFormattedDate(item.getValue(), item.getDateFormat()));
        }

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().tvSelectedDate
                    .setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().tvSelectedDate.setEnabled(false);
            return;
        } else {
            holder.getBinding().tvSelectedDate.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().tvSelectedDate.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().tvSelectedDate.setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().tvSelectedDate.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    private void populateCurrencyView(CurrencyViewHolder holder, int position) {
        CurrencyFormItem item = (CurrencyFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        //fill value
        if (item.getValue() != null) {
            holder.getBinding().etCurrency
                    .setText(String.format(Locale.US, "%.2f", item.getValue()));
        }

        //currency value
        holder.getBinding().etCurrency
                .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //currency options
        List<Option> options = new ArrayList<>(item.getOptions());
        String hint = mContext.getString(R.string.no_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        //check for selection
        int selection = 0;
        if (item.getValue() != null) {
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                if (item.getSelectedOption() == option) selection = i;
            }
        }

        //currency adapter
        holder.getBinding().spCurrency.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        options));

        //only creates the listener once.
        if (holder.getBinding().spCurrency.getOnItemSelectedListener() == null) {
            holder.getBinding().spCurrency
                    .setSelection(selection,
                            false); //workaround so the listener won't be called on init
            holder.getBinding().spCurrency.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {

                            // this prevents the listener to be triggered by setSelection
                            Object tag = holder.getBinding().spCurrency.getTag();
                            if (tag == null || (int) tag != position) {

                                // the user has selected the No Selection option
                                if (position == 0) {
                                    item.setSelectedOption(null);
                                    return;
                                }

                                // the user has selected a valid option
                                int index = position - 1; // because of the No Selection option
                                item.setSelectedOption(item.getOptions().get(index));
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
        } else {
            // this prevents the listener to be triggered by setSelection
            int index = item.getOptions().indexOf(item.getSelectedOption());
            index++; // because of the No Selection option
            holder.getBinding().spCurrency.setTag(index);
            holder.getBinding().spCurrency.setSelection(index);
        }

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().spCurrency.setEnabled(false);
            holder.getBinding().etCurrency.setEnabled(false);
            return;
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().spCurrency.setEnabled(true);
            holder.getBinding().etCurrency.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_error);
            holder.getBinding().etCurrency
                    .setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().etCurrency
                    .setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    /**
     * Handles the view for the {@link MultipleChoiceFormItem}. Displays the UI according to the
     * visibility, enabled and validation params.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateMultipleChoiceView(MultipleChoiceViewHolder holder, int position) {
        MultipleChoiceFormItem item = (MultipleChoiceFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        //creates the selected items adapter
        MultipleChoiceSelectionsAdapter selectionsAdapter = new MultipleChoiceSelectionsAdapter(
                item.getValues());
        holder.getBinding().rvSelectedItems.setLayoutManager(
                new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        holder.getBinding().rvSelectedItems.setAdapter(selectionsAdapter);

        List<Option> options = new ArrayList<>(item.getOptions());

        //adds a hint to the spinner
        String hint = mContext.getString(R.string.multiple_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        //creates the options adapter
        holder.getBinding().spInput.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        options));

        //only creates the listener once.
        if (holder.getBinding().spInput.getOnItemSelectedListener() == null) {
            holder.getBinding().spInput
                    .setSelection(0, false); //workaround so the listener won't be called on init
            holder.getBinding().spInput.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {
                            // the user has selected the hint option
                            if (position == 0) {
                                return;
                            }

                            // the user has selected a valid option
                            int index = position - 1; // because of the hint option
                            selectionsAdapter.addItem(item.getOptions().get(index));
                            holder.getBinding().spInput
                                    .setSelection(0, false); //clear spinner selection
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
        }

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().spInput.setEnabled(false);
            return;
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().spInput.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    private void populatePhoneView(PhoneViewHolder holder, int position) {
        PhoneFormItem item = (PhoneFormItem) getItem(position);

        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        //fill value
        if (item.getValue() != null) {
            holder.getBinding().etPhone.setText(item.getValue());
        }

        //phone value
        holder.getBinding().etPhone
                .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);

        //phone options
        List<Option> options = new ArrayList<>(item.getOptions());
        String hint = mContext.getString(R.string.no_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        //check for selection
        int selection = 0;
        if (item.getValue() != null) {
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                if (item.getSelectedOption() == option) selection = i;
            }
        }

        //currency adapter
        holder.getBinding().spCountry.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,
                        options));

        //only creates the listener once.
        if (holder.getBinding().spCountry.getOnItemSelectedListener() == null) {
            holder.getBinding().spCountry
                    .setSelection(selection,
                            false); //workaround so the listener won't be called on init
            holder.getBinding().spCountry.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {

                            // this prevents the listener to be triggered by setSelection
                            Object tag = holder.getBinding().spCountry.getTag();
                            if (tag == null || (int) tag != position) {

                                // the user has selected the No Selection option
                                if (position == 0) {
                                    item.setSelectedOption(null);
                                    return;
                                }

                                // the user has selected a valid option
                                int index = position - 1; // because of the No Selection option
                                item.setSelectedOption(item.getOptions().get(index));
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
        } else {
            // this prevents the listener to be triggered by setSelection
            int index = item.getOptions().indexOf(item.getSelectedOption());
            index++; // because of the No Selection option
            holder.getBinding().spCountry.setTag(index);
            holder.getBinding().spCountry.setSelection(index);
        }

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        // verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // verify enabled param
        if (!item.isEnabled()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_disabled);
            holder.getBinding().spCountry.setEnabled(false);
            holder.getBinding().etPhone.setEnabled(false);
            return;
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().spCountry.setEnabled(true);
            holder.getBinding().etPhone.setEnabled(true);
        }

        // verify validation
        if (hasToEvaluateValid && !item.isValid()) {
            holder.getBinding().viewSpinnerBackground
                    .setBackgroundResource(R.drawable.spinner_bg_error);
            holder.getBinding().etPhone
                    .setBackgroundResource(R.drawable.spinner_bg_error);
        } else {
            holder.getBinding().viewSpinnerBackground.setBackgroundResource(R.drawable.spinner_bg);
            holder.getBinding().etPhone
                    .setBackgroundResource(R.drawable.spinner_bg);
        }
    }

    /**
     * Handles the view for the {@link FileFormItem}. Displays the UI according to the visibility
     * params.
     *
     * @param holder   view holder
     * @param position item position in adapter.
     */
    private void populateFileView(FileViewHolder holder, int position) {
        FileFormItem item = (FileFormItem) getItem(position);

        //set title
        String title = item.getTitle();
        if (title == null || title.isEmpty()) title = mContext.getString(item.getTitleRes());
        holder.getBinding().tvTitle.setText(title);

        //set button click listener
        holder.getBinding().btnAddFile.setOnClickListener(
                v -> item.getOnButtonClickedListener().onButtonClicked());

        //set value
        if (item.getFileName() != null) {
            holder.getBinding().chipFile.setText(item.getFileName());
            holder.getBinding().chipFile.setVisibility(View.VISIBLE);
            holder.getBinding().btnAddFile.setVisibility(View.GONE);
        } else {
            holder.getBinding().chipFile.setVisibility(View.GONE);
            holder.getBinding().btnAddFile.setVisibility(View.VISIBLE);
        }

        //handle chip close icon
        holder.getBinding().chipFile.setOnCloseIconClickListener(v -> {
            item.clearFileValues();
            notifyItemChanged(getItemPosition(item));
        });

        //handle chip on click
        holder.getBinding().chipFile.setOnClickListener(v -> {
            int fileId = item.getFileId();
            if (fileId == 0) return; //file was not uploaded yet.
            mFragmentInterface.downloadFile(fileId);
        });

        // verify required indicator
        holder.getBinding().tvRequired.setVisibility(item.isRequired() ? View.VISIBLE : View.GONE);

        //verify visibility
        if (!item.isVisible()) {
            holder.hide();
            return;
        } else {
            holder.show();
        }

        // todo verify validation
        if (hasToEvaluateValid && !item.isValid()) {

        } else {

        }

        //verify enabled param
        holder.getBinding().btnAddFile.setEnabled(item.isEnabled());
        holder.getBinding().chipFile.setEnabled(item.isEnabled());
    }
    //endregion

    //region Retrieve Values

    /**
     * Goes through every item in the adapter, except for {@link SingleChoiceFormItem} and {@link
     * DateFormItem} (because they are already set via listeners), and retrieves the selected value
     * from the View to save it into the class object.
     *
     * @param recylerView the RecyclerView that this adapter is attached to.
     */
    public void retrieveValuesFromViews(RecyclerView recylerView) {
        for (int i = 0; i < mDataset.size(); i++) {
            BaseFormItem item = mDataset.get(i);

            RecyclerView.ViewHolder holder = recylerView.findViewHolderForAdapterPosition(i);

            if (holder == null) continue;

            switch (getItemViewType(i)) {

                case FormItemViewType.TEXT_INPUT:
                    retrieveValueForTextInputView(((TextInputViewHolder) holder),
                            (TextInputFormItem) item);
                    continue;

                case FormItemViewType.SINGLE_CHOICE:
                case FormItemViewType.MULTIPLE_CHOICE:
                    //the value(s) is/are saved when the user selects the spinner item(s)
                    continue;

                case FormItemViewType.BOOLEAN:
                    retrieveValueForBooleanView(((BooleanViewHolder) holder),
                            (BooleanFormItem) item);
                    continue;

                case FormItemViewType.DATE:
                    //the value is set when the user selects the date from the dialog
                    continue;

                case FormItemViewType.CURRENCY:
                    retrieveValueForCurrencyView(((CurrencyViewHolder) holder),
                            (CurrencyFormItem) item);
                    continue;

                case FormItemViewType.PHONE:
                    retrieveValueForPhoneView(((PhoneViewHolder) holder),
                            (PhoneFormItem) item);
                    continue;

                case FormItemViewType.FILE:
                    //the value is saved when the user selects the file from the FileChooser intent
                    continue;

                default:
                    throw new IllegalStateException("Invalid ViewType");
            }
        }
    }

    /**
     * Saves the current selected value from the {@link TextInputFormItem} view into the class
     * object.
     *
     * @param holder the view holder.
     * @param item   desired item.
     */
    private void retrieveValueForTextInputView(TextInputViewHolder holder,
                                               TextInputFormItem item) {
        Editable text = holder.getBinding().etInput.getText();
        String value = text == null ? null : text.toString();
        item.setValue(value);
    }

    /**
     * Saves the current selected value from the {@link BooleanFormItem} view into the class
     * object.
     *
     * @param holder the view holder.
     * @param item   desired item.
     */
    private void retrieveValueForBooleanView(BooleanViewHolder holder,
                                             BooleanFormItem item) {
        item.setValue(holder.getBinding().switchInput.isChecked());
    }

    /**
     * Saves the current selected value from the {@link CurrencyFormItem} view into the class
     * object. This method only needs to save the currency number value, because the spinner value
     * is automatically saved upon selection.
     *
     * @param holder the view holder.
     * @param item   desired item.
     */
    private void retrieveValueForCurrencyView(CurrencyViewHolder holder,
                                              CurrencyFormItem item) {
        Editable text = holder.getBinding().etCurrency.getText();
        Double value = text == null || text.length() == 0 ? null : Double.valueOf(text.toString());
        item.setValue(value);
    }

    /**
     * Saves the current selected value from the {@link PhoneFormItem} view into the class object.
     * This method only needs to save the phone number value, because the spinner value is
     * automatically saved upon selection.
     *
     * @param holder the view holder.
     * @param item   desired item.
     */
    private void retrieveValueForPhoneView(PhoneViewHolder holder,
                                           PhoneFormItem item) {
        Editable text = holder.getBinding().etPhone.getText();
        String value = text == null ? null : text.toString();
        item.setValue(value);
    }
    //endregion
}
