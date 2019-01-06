package com.application.club.guestlist;

import android.app.Application;
import android.content.Context;

import com.application.club.guestlist.payumoney.AppEnvironment;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by HoangAnhTuan on 1/21/2018.
 */

public class App extends Application {
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
    }

    AppEnvironment appEnvironment;

    @Override
    public void onCreate() {
        super.onCreate();
        appEnvironment = AppEnvironment.SANDBOX;
        AppEventsLogger.activateApp(this);
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}
