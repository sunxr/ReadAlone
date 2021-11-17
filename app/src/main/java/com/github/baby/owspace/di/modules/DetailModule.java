package com.github.baby.owspace.di.modules;

import com.github.baby.owspace.presenter.DetailContract;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailModule {
    private DetailContract.View view;

    public DetailModule(DetailContract.View view) {
        this.view = view;
    }

    @Provides
    public DetailContract.View provideView() {
        return view;
    }
}
