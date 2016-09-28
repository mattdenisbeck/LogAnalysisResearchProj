package com.mattdenisbeck.csc699ecommerceclient;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class NetworkSingleton {
    private static NetworkSingleton networkSingletonInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private NetworkSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (networkSingletonInstance == null) {
            networkSingletonInstance = new NetworkSingleton(context);
        }
        return networkSingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
