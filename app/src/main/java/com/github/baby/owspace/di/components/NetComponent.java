package com.github.baby.owspace.di.components;

import com.github.baby.owspace.di.modules.NetModule;
import com.github.baby.owspace.model.api.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Component(modules = NetModule.class)
@Singleton
public interface NetComponent {
    ApiService getApiService();
    OkHttpClient getOkHttp();
    Retrofit getRetrofit();
}
