package com.rootnetapp.rootnetintranet.ui.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.FragmentProfileBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.editprofile.EditProfileActivity;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;

import javax.inject.Inject;

public class ProfileFragment extends Fragment {

    @Inject
    ProfileViewModelFactory profileViewModelFactory;
    ProfileViewModel profileViewModel;
    private FragmentProfileBinding fragmentProfileBinding;
    private MainActivityInterface mainActivityInterface;
    int id;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentProfileBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_profile, container, false);
        View view = fragmentProfileBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        profileViewModel = ViewModelProviders
                .of(this, profileViewModelFactory)
                .get(ProfileViewModel.class);
        mainActivityInterface.changeTitle(getString(R.string.profile));
        setHasOptionsMenu(true);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = prefs.getString("token","");
        JWT jwt = new JWT(token);
        id = Integer.parseInt(jwt.getClaim("profile_id").asString());
        subscribe();
        Utils.showLoading(getContext());
        profileViewModel.getUser(id);
        return view;
    }

    @Override
    public void onResume() {
        profileViewModel.getUser(id);
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile: {
                mainActivityInterface.showActivity(EditProfileActivity.class);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void subscribe() {
        final Observer<User> userObserver = ((User data) -> {
            Utils.hideLoading();
            if (null != data) {
                fragmentProfileBinding.layoutProfile.setVisibility(View.VISIBLE);
                fragmentProfileBinding.tvName.setText(data.getFullName());
                //todo modificar DAO para soportar departamentos
                //todo implementar DAO de departamentos
                fragmentProfileBinding.tvDepartment.setText("asdf");
                fragmentProfileBinding.tvPhone.setText(data.getPhoneNumber());
                fragmentProfileBinding.tvEmail.setText(data.getEmail());
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        profileViewModel.getObservableUser().observe(this, userObserver);
        profileViewModel.getObservableError().observe(this, errorObserver);
    }

}
