package com.feliperrm.animationsproject.utils;

import android.app.Application;

import com.feliperrm.animationsproject.models.Movie;
import com.feliperrm.animationsproject.network.ApiCalls;

import java.util.LinkedHashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by felip on 02/10/2016.
 */

public class MyApp extends Application {


    ApiCalls apiCalls;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRetrofit();
    }

    private void setUpRetrofit(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiCalls.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiCalls = retrofit.create(ApiCalls.class);
    }

    public ApiCalls getApiCalls() {
        return apiCalls;
    }
}
