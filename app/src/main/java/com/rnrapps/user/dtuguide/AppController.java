package com.rnrapps.user.dtuguide;

/**
 * Created by rohanpc on 4/10/2016.
 */
import android.app.Application;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.api.client.googleapis.util.Utils;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //crashlytics
        Fabric.with(this, new Crashlytics());
        setupCrashlyticsParameters();
        LeakCanary.install(this);

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    private void setupCrashlyticsParameters() {
        Crashlytics.setString("User Id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        Crashlytics.setString("Device Name", Build.MODEL);
        Crashlytics.setInt("Android Version", Build.VERSION.SDK_INT);
        Crashlytics.setString("App Version", BuildConfig.VERSION_NAME);
        Crashlytics.setInt("App Version Code", BuildConfig.VERSION_CODE);
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}