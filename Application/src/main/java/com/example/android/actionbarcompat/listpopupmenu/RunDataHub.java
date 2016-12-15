package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by lesa on 15.12.2016.
 */
//этот класс ВЫЗАВАЕТСЯ ВСЕГДА первым, в нем мы готовим ОБЩИЕ данные,
// которые готовим в классе ЕДИНСТВЕННОМ! dataHub -  в нем все ссылки и все наши данные!!
public class RunDataHub extends Application {
    private static DataHubSingleton dataHub;
    @Override
    public void onCreate() {
        super.onCreate();
        if(dataHub == null)dataHub = DataHubSingleton.getInstance();
        Log.e("--------RunDataHub", "onCreate DataHub -------------------");
        // MySingleton.initInstance();
    }
    //dataHub -  в нем все ссылки и все наши данные!!
    public DataHubSingleton getDataHub() {return dataHub;}
    //- Вызывается при изменении конфигурации устройства
    @Override
    public void onConfigurationChanged(Configuration newConfig){

    }
    //- Вызывается когда система работает в условиях нехватки памяти, и просит работающие
    // процессы попытаться сэкономить ресурсы.
    @Override
    public void onLowMemory(){

    }
    //- Вызывается, когда операционная система решает, что сейчас хорошее время для обрезания
    // ненужной памяти из процесса.
    @Override
    public void onTrimMemory(int level){

    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }
//    private DataHub(){}
//
}
