package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import com.portfolio.alexey.connector.*;
import com.portfolio.alexey.connector.BluetoothLeServiceNew;

/**
 * Created by lesa on 15.12.2016.
 */
//этот класс ВЫЗАВАЕТСЯ ВСЕГДА первым, в нем мы готовим ОБЩИЕ данные,
// которые готовим в классе ЕДИНСТВЕННОМ! dataHub -  в нем все ссылки и все наши данные!!
public class RunDataHub extends Application {
    public final   String TAG = getClass().getSimpleName();
    private static DataHubSingleton dataHub;
    @Override
    public void onCreate() {
        super.onCreate();
        if(dataHub == null)dataHub = DataHubSingleton.getInstance();
        //
        //подключение сервиса//-------------ЗАПУСТИЛИ ервис ---------
        Intent gattServiceIntent = new Intent(this, com.portfolio.alexey.connector.BluetoothLeServiceNew.class);
        this.bindService(gattServiceIntent, mServiceConnectionM, BIND_AUTO_CREATE);
        //
        myThread.start();
        Log.e("--------RunDataHub", "onCreate DataHub -------------------");
    }
    //---------------------------------------------------------------------------
    public BluetoothLeServiceNew mBluetoothLeServiceM;
    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnectionM = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeServiceM = ((BluetoothLeServiceNew.LocalBinder) service).getService();
            Log.w(TAG, "---mBluetoothLeServiceM = getService() OK -----");
//            if (!mBluetoothLeServiceM.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
//                //         finish();
//            }
//            //           if(popupListFragment != null) popupListFragment.initList();
//            // Automatically connects to the device upon successful start-up initialization.
//            //         mBluetoothLeService.connect(mDeviceAddress,true);
//            Log.w(TAG, "---initialize ---onServiceConnected-----");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeServiceM = null;
            Log.v(TAG, "onServiceDisconnected");
        }
    };
    //========================================================================
    //dataHub -  в нем все ссылки и все наши данные!!
    public DataHubSingleton getDataHub() {return dataHub;}
    //--------ПЕРЕОДИЧЕСКИЙ вызов метода для проверки и УПРАВЛЕНИЯ состояния данных-------
    private void controlState(){
        Log.v(TAG,"controlState\\");
    }
    private boolean work = true;
    // создаём новый поток // описываем объект Runnable в конструкторе
    // TODO: 15.12.2016   // вообще поток этотт ВОЗМОЖНО будет лучше защишен от удаления
    // если его разместить в DataHubSingleton - проверить
    Thread myThread = new Thread(
            new Runnable() {
                public void run() {
                    try {
                        while(work){
                            controlState();
                            myThread.sleep(2000);// засыпаем на 2 секунды (в миллисекундах)
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
    @Override
    public void finalize(){
       // stopWork();
        work = false;
        unbindService(mServiceConnectionM);
        mBluetoothLeServiceM = null;
        if(dataHub != null){//таким бразом мы как бы обнулим его, но в тоже время заставим выполнять зачистку
            DataHubSingleton dh = dataHub;
            dataHub = null;
            dh.finalize();
        }
        Log.e(TAG,"--------finalize()");
    }
    //- Вызывается при изменении конфигурации устройства
    @Override
    public void onConfigurationChanged(Configuration newConfig){  }
    //- Вызывается когда система работает в условиях нехватки памяти, и просит работающие
    // процессы попытаться сэкономить ресурсы.
    @Override
    public void onLowMemory(){}
    //- Вызывается, когда операционная система решает, что сейчас хорошее время для обрезания
    // ненужной памяти из процесса.
    @Override
    public void onTrimMemory(int level){  }
}
