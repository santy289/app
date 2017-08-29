package com.rootnetapp.rootnetintranet.lifecycle;

import android.arch.lifecycle.LifecycleRegistry;
import android.support.v7.app.AppCompatActivity;
import android.arch.lifecycle.LifecycleRegistryOwner;

/**
 * TODO: Remove this class when the library is final and version 1
 * Temporary fix until Architecture Components from Google is final. Then we will need to
 * remove this class and use AppCompatActivity directly.
 */

public class LifecycleAppCompatActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return registry;
    }
}
