package com.rootnetapp.rootnetintranet.di;

import com.rootnetapp.rootnetintranet.ui.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowDialog;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowModule;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomCountryPicker;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ListSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ProductoSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ServicioSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.UsuariosSpinner;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainModule;
import com.rootnetapp.rootnetintranet.ui.editprofile.EditProfileActivity;
import com.rootnetapp.rootnetintranet.ui.editprofile.EditProfileModule;
import com.rootnetapp.rootnetintranet.ui.login.LoginActivity;
import com.rootnetapp.rootnetintranet.ui.login.LoginModule;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenModule;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailModule;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;

/**
 * Created by Propietario on 09/03/2018.
 */

@Singleton
@Component(
        modules = {
                AppModule.class,
                NetModule.class,
                SharedPreferencesModule.class,
                DomainModule.class,
                LoginModule.class,
                ResetPasswordModule.class,
                RequestTokenModule.class,
                ProfileModule.class,
                EditProfileModule.class,
                WorkflowModule.class,
                CreateWorkflowModule.class,
                WorkflowDetailModule.class
        }
)

public interface AppComponent {

    void inject(DomainActivity domainActivity);

    void inject(LoginActivity loginActivity);

    void inject(ResetPasswordFragment resetPasswordFragment);

    void inject(RequestTokenFragment requestTokenFragment);

    void inject(SyncActivity syncActivity);

    void inject(ProfileFragment profileFragment);

    void inject(EditProfileActivity editProfileActivity);

    void inject(WorkflowFragment workflowFragment);

    void inject(CreateWorkflowDialog createWorkflowDialog);

    void inject(CustomSpinner customSpinner);

    void inject(ListSpinner listSpinner);

    void inject(ProductoSpinner productoSpinner);

    void inject(ServicioSpinner servicioSpinner);

    void inject(UsuariosSpinner usuariosSpinner);

    void inject(CustomCountryPicker countryPicker);

    void inject(WorkflowDetailFragment workflowDetailFragment);

}