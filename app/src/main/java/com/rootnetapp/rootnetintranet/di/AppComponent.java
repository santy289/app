package com.rootnetapp.rootnetintranet.di;

import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainModule;
import com.rootnetapp.rootnetintranet.ui.login.LoginActivity;
import com.rootnetapp.rootnetintranet.ui.login.LoginModule;

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
                LoginModule.class
        }
)

public interface AppComponent {

    void inject(DomainActivity domainActivity);
    void inject(LoginActivity loginActivity);

}