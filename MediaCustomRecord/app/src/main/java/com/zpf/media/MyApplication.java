package com.zpf.media;

import android.app.Application;
import android.content.Context;

/**
 * author:zpf
 * date:2019-08-16
 */
public class MyApplication extends Application {
    private static Context mContext;
    private static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);
        setApplicationContext(getApplicationContext());
    }
    private static void setInstance(MyApplication instance) {
        mInstance = instance;
    }

    private static void setApplicationContext(Context context) {
        mContext = context;
    }

    public static MyApplication getInstance() {
        if (mInstance == null) {
            synchronized (MyApplication.class) {
                if (mInstance == null) {
                    mInstance = new MyApplication();
                }
            }
        }
        return mInstance;
    }

    public static Context getAppContext() {
        if (mContext == null) {
            synchronized (MyApplication.class) {
                if (mContext == null) {
                    mContext = MyApplication.getInstance().getApplicationContext();
                }
            }
        }
        return mContext;
    }
}
