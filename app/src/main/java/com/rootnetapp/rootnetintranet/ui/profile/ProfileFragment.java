package com.rootnetapp.rootnetintranet.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.FragmentProfileBinding;
import com.rootnetapp.rootnetintranet.models.responses.user.Department;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedUser;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ProfileFragment extends Fragment {

    @Inject
    ProfileViewModelFactory profileViewModelFactory;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding mBinding;
    private MainActivityInterface mainActivityInterface;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(MainActivityInterface mainActivityInterface) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_profile, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        profileViewModel = ViewModelProviders
                .of(this, profileViewModelFactory)
                .get(ProfileViewModel.class);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        subscribe();

        profileViewModel.init(token);
        return view;
    }

    private void subscribe() {
        profileViewModel.getObservableUser().observe(this, this::updateProfileUi);
        profileViewModel.getObservableError().observe(this, this::showToastMessage);
        profileViewModel.getObservableShowLoading().observe(this, this::showLoading);
    }

    @UiThread
    private void updateProfileUi(LoggedUser user) {
        if (user == null) return;

        String path = Utils.imgDomain + user.getPicture().trim();
        Picasso.get().load(path).into(mBinding.ivUser);

        mBinding.tvName.setText(user.getFullName());

        StringBuilder departmentStringBuilder = new StringBuilder();
        List<Department> departments = user.getDepartment();
        for (int i = 0; i < departments.size(); i++) {
            Department department = departments.get(i);
            departmentStringBuilder.append(department.getName());

            if (i < departments.size() - 1) {
                departmentStringBuilder.append(", "); //separator
            }
        }

        mBinding.tvDepartments.setText(departmentStringBuilder.toString());
        mBinding.tvPhone.setText(user.getPhoneNumber());
        mBinding.tvEmail.setText(user.getEmail());
        mBinding.layoutProfile.setVisibility(View.VISIBLE);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }
}
