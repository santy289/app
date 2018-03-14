package com.rootnetapp.rootnetintranet.di;

import com.rootnetapp.rootnetintranet.ui.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainModule;
import com.rootnetapp.rootnetintranet.ui.login.LoginActivity;
import com.rootnetapp.rootnetintranet.ui.login.LoginModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment.ResetPasswordModule;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenFragment;
import com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment.RequestTokenModule;

import javax.inject.Singleton;

import dagger.Component;

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
                RequestTokenModule.class
        }
)

public interface AppComponent {

    void inject(DomainActivity domainActivity);
    void inject(LoginActivity loginActivity);
    void inject(ResetPasswordFragment resetPasswordFragment);
    void inject(RequestTokenFragment requestTokenFragment);
    void inject(SyncActivity syncActivity);

}