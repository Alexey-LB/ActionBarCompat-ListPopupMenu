package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import com.portfolio.alexey.connector.DataHubSingleton;

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
        if(dataHub == null)dataHub = DataHubSingleton.getInstance(RunDataHub.this);
        myThread.start();
        Log.e("--------RunDataHub", "onCreate DataHub -------------------");
    }
    //---------------------------------------------------------------------------



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
