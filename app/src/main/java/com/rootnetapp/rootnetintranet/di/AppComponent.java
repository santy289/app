package com.rootnetapp.rootnetintranet.di;

import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainModule;

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
                DomainModule.class
        }
)

public interface AppComponent {

    void inject(DomainActivity domainActivity);

}