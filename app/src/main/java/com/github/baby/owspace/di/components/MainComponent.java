package com.github.baby.owspace.di.components;

import com.github.baby.owspace.di.modules.MainModule;
import com.github.baby.owspace.di.scopes.UserScope;
import com.github.baby.owspace.view.activity.MainActivity;

import dagger.Component;

@UserScope
@Component(modules = MainModule.class, dependencies = NetComponent.class)
public interface MainComponent {

    void inject(MainActivity activity);

}
