package com.liucj.ppjoke;

import android.app.Application;

import com.liucj.libnetwork.ApiService;

public class JokeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://192.168.1.109:8087/serverdemo", null);
    }
}
