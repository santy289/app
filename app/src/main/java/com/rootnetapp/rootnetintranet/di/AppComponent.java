package com.rootnetapp.rootnetintranet.di;

import com.rootnetapp.rootnetintranet.services.manager.WorkflowManagerServiceModule;
import com.rootnetapp.rootnetintranet.services.manager.WorkflowManagerService;
import com.rootnetapp.rootnetintranet.ui.createworkflow.WorkFlowCreateFragment;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityModule;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerFragment;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerModule;
import com.rootnetapp.rootnetintranet.ui.splash.SplashModule;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
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
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenModule;
import com.rootnetapp.rootnetintranet.ui.splash.SplashActivity;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineFragment;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineModule;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailModule;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                MainActivityModule.class,
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
                WorkflowDetailModule.class,
                TimelineModule.class,
                WorkflowManagerServiceModule.class,
                WorkflowManagerModule.class,
                SplashModule.class
        }
)

public interface AppComponent {

    void inject(DomainActivity domainActivity);

    void inject(LoginActivity loginActivity);

    void inject(MainActivity mainActivity);

    void inject(SplashActivity splashActivity);

    void inject(ResetPasswordFragment resetPasswordFragment);

    void inject(RequestTokenFragment requestTokenFragment);

    void inject(SyncActivity syncActivity);

    void inject(ProfileFragment profileFragment);

    void inject(EditProfileActivity editProfileActivity);

    void inject(WorkflowFragment workflowFragment);

    void inject(CreateWorkflowDialog createWorkflowDialog);

    void inject(WorkFlowCreateFragment workFlowCreateFragment);

    void inject(CustomSpinner customSpinner);

    void inject(ListSpinner listSpinner);

    void inject(ProductoSpinner productoSpinner);

    void inject(ServicioSpinner servicioSpinner);

    void inject(UsuariosSpinner usuariosSpinner);

    void inject(CustomCountryPicker countryPicker);

    void inject(WorkflowDetailFragment workflowDetailFragment);

    void inject(TimelineFragment timelineFragment);

    void inject(WorkflowManagerService workflowManagerService);

    void inject(WorkflowManagerFragment workflowManagerFragment);

}