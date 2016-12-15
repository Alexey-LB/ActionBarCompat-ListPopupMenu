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

    private DataHubSingleton(Context context){
        //подключение сервиса//-------------ЗАПУСТИЛИ ервис ---------
        Intent gattServiceIntent = new Intent(context,BluetoothLeServiceNew.class);
        context.bindService(gattServiceIntent, mServiceConnectionM, BIND_AUTO_CREATE);
    }
    public static DataHubSingleton getInstance(Context context){
        if (null == instance){
            instance = new DataHubSingleton(context);
        }return instance;
    }
    public BluetoothLeServiceNew mBluetoothLeServiceM;

    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnectionM = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //     mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeServiceM = ((BluetoothLeServiceNew.LocalBinder) service).getService();
            if (!mBluetoothLeServiceM.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
       //         finish();
            }
 //           if(popupListFragment != null) popupListFragment.initList();
            // Automatically connects to the device upon successful start-up initialization.
            //         mBluetoothLeService.connect(mDeviceAddress,true);
            Log.w(TAG, "---initialize ---onServiceConnected-----");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeServiceM = null;
            Log.v(TAG, "onServiceDisconnected");
        }
    };
    @Override
    public void finalize(){
        Log.e(TAG,"--------finalize()");
    }
}
