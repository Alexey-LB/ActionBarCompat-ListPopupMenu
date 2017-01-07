package com.portfolio.alexey.connector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by lesa on 07.01.2017.
 */

public class NotificationLevel {
    private  final boolean debug = true;
    private  final String TAG = "NOTIF_LEVEL";
    private  Activity activity;
    private  Context context;
    public boolean switchNotification = false;//ПЕРЕКЛЮЧатель разрешение на оповешение звуком или вибрацией
    public boolean switchVibration = false; //ПЕРЕКЛЮЧатель разрешение на оповешение  вибрацией
    //
    public boolean resetNotification = false;/// сброс работающего ОПОВЕЩЕНИЕ(сбрасывает флаг ПОРОГА срабатывания)
    public boolean onNotification = false;//ПОРОГ оповещения сработал (если разрешен переключателем switchNotification)
    //
    public String melody;//УРЛ мелодии ОПОВЕЩЕНИЯ звуком
    //
    private long timerMelody = 0;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
    public int timeLongMelody = 300;// длительность работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
    //
    private long timerVibration = 0;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
    public int timeLongVibration = 5;// длительность работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
    //
    private  final int typeLevel;//какой тип (логический, флоат)порог контролируем, минимум максимум указанного значения
    public final int BOOLEAN_FALSE = 0;// срабатывание при логическом значяениии лож
    public final int BOOLEAN_TRUE = 1;// срабатывание при логическом значяениии лож
    public final int FLOAT_MIN = 2;// срабатывание достижении и при опускании Флоат значения до указанного уровня и ниже!
    public final int FLOAT_MAX = 3;// срабатывание достижении и превышении Флоат значения до указанного уровня и ниже!
    //-----------
    private  final float  threshol = 0.15f;//порог срабатывания- отпускания оповещения
    //
    private  float  thresholdReset = Float.NaN;//порог срабатывания- отпускания оповещения
    //
    private float  valueLevel = Float.NaN;//порог срабатывания
    //
    private boolean  onLevel = false;//порог срабатывания
    //
    public NotificationLevel(int typelevel,float level,Activity activity){
        typeLevel = typelevel;
        valueLevel = level;
        //определяем порог шмитта, при котором происходит восстановления системы
        // сигнализации для нового срабатывания
        if(typelevel == FLOAT_MIN) thresholdReset = valueLevel + threshol;
        else thresholdReset = valueLevel - threshol;
    }
    public NotificationLevel(int typelevel,boolean level,Activity activity){
        typeLevel = typelevel;
        onLevel = level;
    }
    //расчет значений и сигнализация звуком, вибрацией
    public void calck(float value){
        calck(value, false);
    }
    //расчет значений и сигнализация звуком, вибрацией
    public void calck(boolean on){
        calck(Float.NaN, on);
    }
    //// для сброса к текущей температуре ОТДЕЛЬНАЯ кнопка
    public void resetNotification(){
        //сбрасываем таймеры работы
        timerMelody = 0;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
        timerVibration = 0;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
        resetNotification = true;/// сброс работающего ОПОВЕЩЕНИЕ(сбрасывает флаг ПОРОГА срабатывания)
        Util.playerRingtoneStop();
    }
    private void setNotification() {
        onNotification = true;
        if(timerMelody == 0)timerMelody = System.currentTimeMillis() + timeLongMelody * 1000;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
        if(timerVibration == 0)timerVibration = System.currentTimeMillis() + timeLongVibration * 1000;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
    }
    private void notification() {
        if(timerVibration > System.currentTimeMillis()) Util.playerVibrator(300, activity);
        if((melody != null) && (timerMelody > System.currentTimeMillis())) {
            Util.playerRingtone(0f, melody, activity,TAG);
        }
    }
    private void initNotification(){
        //сбрасываем таймеры ЗВУКА и вибрации, чтоб ПРИ последуюших превышениях СРАБОТАЛ
        // он у нас ограничен по времени
        timerMelody = 0;// разрешаем сново сработать сигнализации БЕЗ сброса ()
        timerVibration = 0;//  разрешаем сново сработать сигнализации БЕЗ сброса
        //приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
        if(resetNotification) {
            onNotification = false;
            resetNotification = false;
        }
    }
    private void calck(float value, boolean  on){
        switch(typeLevel){
            case BOOLEAN_FALSE:
                if(on == false) {
                    setNotification();
                    log("BOOLEAN_FALSE Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    initNotification();
                }
                break;
            case BOOLEAN_TRUE:
                if(on == true){
                    setNotification();
                    log("BOOLEAN_TRUE Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    initNotification();
                }
                break;
            case FLOAT_MIN:
                if(value <= valueLevel)  {
                    setNotification();
                    log("FLOAT_MIN Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    if(value > thresholdReset) initNotification();
                }
                break;
            case FLOAT_MAX:
                if(value >= valueLevel)  {
                    setNotification();
                    log("FLOAT_MAX Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    if(value < thresholdReset) initNotification();
                }
                break;
        }
        notification();
    }

    private void log(String mess){
        if(debug)Log.e(TAG,mess);
    }
}
