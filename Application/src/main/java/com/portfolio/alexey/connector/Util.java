package com.portfolio.alexey.connector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.actionbarcompat.listpopupmenu.R;

/**
 * Created by lesa on 17.12.2016.
 */

public class Util {
    // https://geektimes.ru/post/232885/
// Что бы звук не был тихим:
// stackoverflow.com/questions/8278939/android-mediaplayer-volume-is-very-low-already-adjusted-volume
    //пример: playerRingtone(0.8f, null); рингтонг 0.8 от макимума звука и мелодия по умолчанию
    //пример: playerRingtone(0f, Uri); ГРОМКОСТ звука СИТЕМНОЙ настройки проигрывателя и мелодия по Uri
    static public void playerRingtone(Float setVolume, String uriRingtone , Activity activity,String  tag){
        Uri uri= null;
        if(uriRingtone != null){
            // TODO: 17.12.2016 обработать возможные ИСКЛЮЧЕНИЯ парсера
            uri = Uri.parse(uriRingtone);
        }
        playerRingtone(setVolume, uri ,activity,tag);
    }
    static boolean onRingtoneWork = false;
    static public void playerRingtone(Float setVolume, Uri uriRingtone , Activity activity,String  tag){
        if(onRingtoneWork) return;//если чет о играем, то пока НЕ закончим, новй играть НЕ будем
        onRingtoneWork = true;
        if(uriRingtone == null){// Сигнал по умолчанию
            uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        MediaPlayer mediaPlayer= new MediaPlayer();
        if(setVolume == 0){//ГРОМКОСТЬ системных настроек
            setVolume = 1f;
        }else{
            // (Завист только от setVolume)НА ПОЛНУЮ гмкость ВНЕ зависмости от УСТАНОВКИ в СИСТЕМЕ!!!
//http://stackoverflow.com/questions/8278939/android-mediaplayer-volume-is-very-low-already-adjusted-volume
            AudioManager amanager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            // int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            int maxVolume = amanager.getStreamVolume(AudioManager.STREAM_ALARM);
            amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM); // this is important.
        }
        //устанавливает ОТ максимума!!!
        mediaPlayer.setVolume(setVolume, setVolume);
        try {
            mediaPlayer.setDataSource(activity.getApplicationContext(), uriRingtone);
            //  mediaPlayer.setLooping(looping);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                //после проигрывания попадает сюда
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //останавливается ПЛЕЕР и выбрасывается из памяти
                    mp.release();//Это закончит, освободить, отпустить
                    onRingtoneWork = false;//разрешили другим вызывать музыку
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), "Error default media ", Toast.LENGTH_LONG).show();
            Log.e(tag, "  Ringtone ERR= " + e);
        }

    }


    //android.support.v7.app.ActionBar
    // getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
    static public boolean setSupportV7appActionBar(
            android.support.v7.app.ActionBar actionBar,String  tag,String  title) {
        if(actionBar == null){
            Log.e(tag, "actionBar == null--");
            return false;
        }
      //  android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//установить картинку
        actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp); //gtab.setIcon(R.drawable.ic_add);
        //разрешить копку доиой
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //
        //actionBar.setDisplayUseLogoEnabled(true);
        //-- срабатывают только если вместе, отменяют ИКОНКУ, если заменить- достаточно одного
        actionBar.setIcon(null);//actionBar.setIcon(R.drawable.ic_language_black_24dp);
        actionBar.setDisplayUseLogoEnabled(false);
        //
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        //
        Log.d(tag, "actionBar != null--");
        return true;
    }


    static public boolean setActionBar(ActionBar actionBar,String  tag,String  title) {
        if(actionBar == null){
            Log.e(tag, "actionBar == null--");
            return false;
        }
       // ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
        //вместо ЗНачка по умолчанию, назначаемого выше, подставляет свой
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_black_24dp);
        //------------------------------
        //  actionBar.
        //разрешить копку доиой
        actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);//устанавливает надпись и иконку как кнопку домой(не требуется
        // actionBar.setDisplayUseLogoEnabled(true);
        //    actionBar.setHomeButtonEnabled(true); метод - actionBar.setDisplayHomeAsUpEnabled(true);)
        //чето не показыввет ее
//-- срабатывают только если вместе, отменяют ИКОНКУ, если заменить- достаточно одного
        actionBar.setIcon(null);//actionBar.setIcon(R.drawable.ic_language_black_24dp);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
//------------------------------------------------------
        //actionBar.setCustomView(null);
        //actionBar.setLogo(null);
        Log.d(tag, "actionBar != null--");
        return true;
    }
    //заносит данные во вювер с на МИНУС работаем с уровнями и,
    // А ТАКЖЕ- если там тоже самое- не записывает в Вювер ,эеономия времени ОТРИСОВКИ
    static public boolean setLevelToImageView(int level, View view) {
        if (isNoNull(level, view) && (view instanceof ImageView)) {
            //Отрицательное число уровеня- ломает вывод ИЗОБРАЖЕНИЯ по уровням
            if (level < 0) level = ~level + 1;
            ImageView iv = ((ImageView) view);
            //--
            if((iv.getTag() == null) || ((Integer)iv.getTag() != level)){
                iv.setImageLevel(level);
                iv.setTag(level);
            }
            return true;
        }
        return false;
    }
    //ПОИСК глобадьный по АКТИВНОСТИ- берет ПЕРВЫЙ попашийся элемент!
    static public boolean setLevelToImageView(int level, int viewID, Activity activity) {
        return setLevelToImageView(level,  activity.findViewById(viewID));
    }
    ////ПОИСК ЛОКАЛЬНЫЙ внутри  viewRoot- берет ПЕРВЫЙ попашийся элемент!
    static public boolean setLevelToImageView(int level, int viewID, View viewRoot) {
        return setLevelToImageView(level,  viewRoot.findViewById(viewID));
    }
    //заносит данные во вювер с контролем на НУЛЛ и длинну строки,
    // А ТАКЖЕ- если там тоже самое- не записывает в Вювер текст,эеономия времени ОТРИСОВКИ
    static public boolean setTextToTextView(String str, View view, String def){
        if((view != null) && (view instanceof TextView) && ((str != null) || (def != null))){
            if(str == null) str = def;
            else {
                if(str.length() > 64) str = str.substring(0,63);
            }
            // А ТАКЖЕ- если там тоже самое- не записывает в Вювер текст,эеономия времени ОТРИСОВКИ
            if(((TextView)view).getText().toString().compareTo(str) != 0){
                ((TextView)view).setText(str);
            }
            return true;
        }
        return false;
    }
//ПОИСК глобадьный по АКТИВНОСТИ- берет ПЕРВЫЙ попашийся элемент!
    static public boolean setTextToTextView(String str, int viewID, Activity activity) {
        return setTextToTextView(str,activity.findViewById(viewID),null);
    }

    static public boolean setTextToTextView(String str, int viewID, Activity activity, String def) {
        return setTextToTextView(str, activity.findViewById(viewID),def);
    }

////ПОИСК ЛОКАЛЬНЫЙ внутри  viewRoot- берет ПЕРВЫЙ попашийся элемент!
    static public boolean setTextToTextView(String str, int viewID, View viewRoot) {
        return setTextToTextView(str,viewRoot.findViewById(viewID),null);
    }

    static public boolean setTextToTextView(String str, int viewID,  View viewRoot, String def) {
        return setTextToTextView(str, viewRoot.findViewById(viewID),def);
    }
    ///-------------------------------------------
    static public boolean isNoNull(Object ob1) {
        return ((ob1 != null) );
    }
    static public boolean isNoNull(Object ob1, Object ob2) {
        return ((ob1 != null) && (ob2 != null));
    }
    static public boolean isNoNull(Object ob1, Object ob2, Object ob3) {
        return ((ob1 != null) && (ob2 != null) && (ob3 != null));
    }
}
