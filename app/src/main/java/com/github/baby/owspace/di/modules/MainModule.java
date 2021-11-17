package com.github.baby.owspace.di.modules;

import com.github.baby.owspace.presenter.MainContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private final MainContract.View view;

    public MainModule(MainContract.View view) {
        this.view = view;
    }

    @Provides
    public MainContract.View provideView() {
        return view;
    }
}
