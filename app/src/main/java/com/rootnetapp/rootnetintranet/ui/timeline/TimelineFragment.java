package com.rootnetapp.rootnetintranet.ui.timeline;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.FragmentTimelineBinding;
import com.rootnetapp.rootnetintranet.models.responses.timeline.ItemComments;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.timeline.adapters.TimelineAdapter;

import java.util.List;

import javax.inject.Inject;


public class TimelineFragment extends Fragment implements TimelineInterface/*,
        PopupMenu.OnMenuItemClickListener*/{

    @Inject
    TimelineViewModelFactory viewModelFactory;
    private TimelineViewModel viewModel;
    private FragmentTimelineBinding binding;
    private MainActivityInterface anInterface;
    private String token;
    private List<TimelineItem> timelineItems;
    private List<User> timelineUsers;
    private String start, end;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(MainActivityInterface anInterface) {
        TimelineFragment fragment = new TimelineFragment();
        fragment.anInterface = anInterface;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_timeline, container, false);
        View view = binding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(TimelineViewModel.class);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        binding.recTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        subscribe();
        binding.btnMonth.setOnClickListener(this::filterClicked);
        binding.btnWeek.setOnClickListener(this::filterClicked);
        binding.btnDay.setOnClickListener(this::filterClicked);
        binding.btnSelectdates.setOnClickListener(this::selectDates);
        start = Utils.getMonthDay(0,1);
        end = Utils.getMonthDay(0,30);
        binding.tvSelecteddates.setText("("+start+" - "+end+")");
        binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
        start = start+"T00:00:00-0000";
        end = end+"T00:00:00-0000";
        getTimeline();
        return view;
    }

    private void selectDates(View view) {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    private void filterClicked(View view) {
        switch (view.getId()){
            case R.id.btn_month:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.white));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                start = Utils.getMonthDay(0,1);
                end = Utils.getMonthDay(0,30);
                binding.tvSelecteddates.setText("("+start+" - "+end+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
                start = start+"T00:00:00-0000";
                end = end+"T00:00:00-0000";
                break;
            }
            case R.id.btn_week:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.white));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                start = Utils.getWeekStart();
                end = Utils.getWeekEnd();
                binding.tvSelecteddates.setText("("+start+" - "+end+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_week));
                start = start+"T00:00:00-0000";
                end = end+"T00:00:00-0000";
                break;
            }
            case R.id.btn_day:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.white));
                start = Utils.getCurrentDate();
                binding.tvSelecteddates.setText("("+start+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.today));
                start = start+"T00:00:00-0000";
                end = Utils.getCurrentDate()+"T23:59:59-0000";
                break;
            }
        }
        getTimeline();
    }

    private void getTimeline(){
        Utils.showLoading(getContext());
        viewModel.getTimeline(token, start, end);
    }

    private void subscribe() {

        final Observer<List<TimelineItem>> timelineObserver = ((List<TimelineItem> data) -> {
            if (null != data) {
                viewModel.getUsers(token);
                timelineItems = data;
            }else{
                Utils.hideLoading();
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<List<User>> usersObserver = ((List<User> data) -> {
            if (null != data) {
                viewModel.getComments(token);
                timelineUsers = data;
            }else{
                Utils.hideLoading();
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<List<ItemComments>> commentsObserver = ((List<ItemComments> data) -> {
            Utils.hideLoading();
            if (null != data) {
                if (timelineItems.size()!=0){
                    binding.lytNotimeline.setVisibility(View.GONE);
                    binding.recTimeline.setVisibility(View.VISIBLE);
                    binding.recTimeline.setAdapter(new TimelineAdapter(timelineItems, timelineUsers,
                            data, viewModel, token, this, this));
                }
                else{
                    binding.recTimeline.setVisibility(View.GONE);
                    binding.lytNotimeline.setVisibility(View.VISIBLE);
                }
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Utils.hideLoading();
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getObservableTimeline().observe(this, timelineObserver);
        viewModel.getObservableUsers().observe(this, usersObserver);
        viewModel.getObservableComments().observe(this, commentsObserver);
        viewModel.getObservableError().observe(this, errorObserver);

    }

    @Override
    public void setDate(String start, String end) {
        binding.tvSelecteddates.setText("("+start+" - "+end+")");
        binding.tvSelecteddatetitle.setText(getString(R.string.selected_period));
        this.start = start+"T00:00:00-0000";
        this.end = end+"T00:00:00-0000";
        getTimeline();
    }

    @Override
    public void reload() {
        getTimeline();
    }

    /*private void imgClick(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_timeline_filters, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.:

                return true;
            default:
                return false;
        }
    }*/

}
