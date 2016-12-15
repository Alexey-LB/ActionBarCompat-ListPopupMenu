package com.portfolio.alexey.connector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.content.ServiceConnection;
import android.util.Log;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by lesa on 15.12.2016.
 */

public class DataHubSingleton {
    public final   String TAG = getClass().getSimpleName();
    private static DataHubSingleton instance;

    private DataHubSingleton(){

    }
    public static DataHubSingleton getInstance(){
        if (null == instance){
            instance = new DataHubSingleton();
        }return instance;
    }

    @Override
    public void finalize(){
        Log.e(TAG,"--------finalize()");
    }
}
