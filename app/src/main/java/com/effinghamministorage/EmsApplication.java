package com.effinghamministorage;

import android.app.Application;

import co.igloo.access.sdk.IglooPlugin;

public class EmsApplication extends Application {

    private static IglooPlugin iglooPlugin;

    @Override
    public void onCreate() {
        super.onCreate();
        iglooPlugin = new IglooPlugin(getApplicationContext());
    }

    public static IglooPlugin getIglooPlugin() {
        return iglooPlugin;
    }
}
