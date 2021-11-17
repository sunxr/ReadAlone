package com.github.baby.owspace.di.components;

import com.github.baby.owspace.di.modules.SplashModule;
import com.github.baby.owspace.di.scopes.UserScope;
import com.github.baby.owspace.view.activity.SplashActivity;

import dagger.Component;

@UserScope
@Component(modules = SplashModule.class,dependencies = NetComponent.class)
public interface SplashComponent {
    void inject(SplashActivity splashActivity);
}
