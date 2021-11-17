package com.github.baby.owspace.di.components;

import com.github.baby.owspace.di.modules.DetailModule;
import com.github.baby.owspace.di.scopes.UserScope;
import com.github.baby.owspace.view.activity.AudioDetailActivity;
import com.github.baby.owspace.view.activity.DetailActivity;

import dagger.Component;

@UserScope
@Component(modules = DetailModule.class, dependencies = NetComponent.class)
public interface DetailComponent {
    void inject(DetailActivity activity);
    void inject(AudioDetailActivity activity);
}
