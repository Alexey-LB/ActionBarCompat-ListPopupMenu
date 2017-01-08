package com.portfolio.alexey.connector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by lesa on 07.01.2017.
 */

public class NotificationLevel {
    private  static  final boolean debug = true;
    private  static  final String TAG = "NOTIF_LEVEL";
    public boolean switchNotification = false;//ПЕРЕКЛЮЧатель разрешение на оповешение звуком или вибрацией
    public boolean switchVibration = false; //ПЕРЕКЛЮЧатель разрешение на оповешение  вибрацией
    //
    public boolean resetNotification = false;/// сброс работающего ОПОВЕЩЕНИЕ(сбрасывает флаг ПОРОГА срабатывания)
    public boolean onNotification = false;//ПОРОГ оповещения сработал (если разрешен переключателем switchNotification)
    //
    public String melody;//УРЛ мелодии ОПОВЕЩЕНИЯ звуком
    //
    private long timerMelody = 0;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
    public int timeLongMelody = 60;// длительность работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
    //
    private long timerVibration = 0;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
    public int timeLongVibration = 10;// длительность работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
    //
    private  final int typeLevel;//какой тип (логический, флоат)порог контролируем, минимум максимум указанного значения
    public static final int BOOLEAN_FALSE = 0;// срабатывание при логическом значяениии лож
    public static  final int BOOLEAN_TRUE = 1;// срабатывание при логическом значяениии лож
    public static  final int FLOAT_MIN = 2;// срабатывание достижении и при опускании Флоат значения до указанного уровня и ниже!
    public static  final int FLOAT_MAX = 3;// срабатывание достижении и превышении Флоат значения до указанного уровня и ниже!
    //-----------
    private  final float  threshol = 0.15f;//порог срабатывания- отпускания оповещения
    //
    public float  valueLevel = Float.NaN;//порог срабатывания
    //показывает текущее состояние
    public boolean  onLevel = false;// входное значение равно срабатыванию ПОРОГА
    //
    public NotificationLevel(int typelevel,Activity activity){
        typeLevel = typelevel;
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
        timerMelody = 1;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
        timerVibration = 1;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
        resetNotification = true;/// сброс работающего ОПОВЕЩЕНИЕ(сбрасывает флаг ПОРОГА срабатывания)
    }
    private void setNotification() {
        onNotification = true;
        onLevel = true;//показывает текущее состояние
        long time = System.currentTimeMillis();
        if(!switchNotification) return;//БЕЗ оповещения!
        if((timerMelody == 0)){
            Util.playerRingtoneStop();// на всякий случай сбрасываем предыдущие мелодии
            timerMelody = time + timeLongMelody * 1000;// время окончания работы ОПОВЕЩЕНИЯ звуком (ограниячиваем 300 секунд)
        }
        if((timerVibration == 0) && (switchVibration)){
            timerVibration = time + timeLongVibration * 1000;// время окончания работы ОПОВЕЩЕНИЯ вибрацией (ограниячиваем 5 секунд)
            log(" setNotification= " + onNotification +"  timeVibr" + (timerVibration/1000) % 1000 + "  time= "+(System.currentTimeMillis()/1000 ) % 1000+ "  melody= " + (timerMelody / 1000) % 1000);
        }

    }
    private void notification() {
        if((timerVibration > System.currentTimeMillis()) && (switchVibration)) {
            log("---timerVibration  ");
            Util.playerVibrator(300);
        }
        if((melody != null) && (timerMelody > System.currentTimeMillis())) {
            log("---melody, onNotf= " + onNotification +"  resetNotf= " + resetNotification +"  timeVibr" + (timerVibration/1000) % 1000 + "  time= "+(System.currentTimeMillis()/1000 ) % 1000+ "  melody= " + (timerMelody / 1000) % 1000);
            Util.playerRingtone(0f, melody,TAG);
        }
    }
    private void initNotification(){
        //сбрасываем таймеры ЗВУКА и вибрации, чтоб ПРИ последуюших превышениях СРАБОТАЛ
        // он у нас ограничен по времени
        long time = System.currentTimeMillis();
        // сбрасываем только после того как все отыграет
        if(time > timerMelody ) timerMelody = 0;// разрешаем сново сработать сигнализации БЕЗ сброса ()
        if(time > timerVibration )timerVibration = 0;//  разрешаем сново сработать сигнализации БЕЗ сброса
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
                    onLevel = false;//показывает текущее состояние
                }
                break;
            case BOOLEAN_TRUE:
                if(on == true){
                    setNotification();
                    log("BOOLEAN_TRUE Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    initNotification();
                    onLevel = false;//показывает текущее состояние
                }
                break;
            case FLOAT_MIN:
                if(Float.isNaN(value)) return;
                if(value <= valueLevel)  {
                    setNotification();
                    log("FLOAT_MIN Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    onLevel = false;//показывает текущее состояние
                    if(value > (valueLevel + threshol)) initNotification();
                }
                break;
            case FLOAT_MAX:
                if(Float.isNaN(value)) return;
                if(value >= valueLevel)  {
                    setNotification();
                    log("FLOAT_MAX Notification");
                }else{//приводим сигнализацию к новому срабатывания, если предыдущее срабатываение было сброшено!
                    onLevel = false;//показывает текущее состояние
                    if(value < (valueLevel - threshol)) initNotification();
                }
                break;
        }
        notification();
    //    log("-- calck--");
    }

    private void log(String mess){
        if(debug){
            String str = "";
            switch(typeLevel){
                case BOOLEAN_FALSE: str = ColorString.getBlueFonBlack(" FALSE");
                    break;
                case BOOLEAN_TRUE: str = ColorString.getRedFonBlack(" TRUE");
                    break;
                case FLOAT_MIN: str = ColorString.getBlueFonBlack(" MIN");
                    break;
                case FLOAT_MAX: str = ColorString.getRedFonBlack(" MAX");
            }
            Log.w(TAG + str, mess);
        }
    }
}
