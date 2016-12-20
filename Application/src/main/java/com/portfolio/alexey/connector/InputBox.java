package com.portfolio.alexey.connector;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by lesa on 20.12.2016.
 */

public class InputBox {
    //http://stackoverflow.com/questions/7671637/how-to-set-ringtone-with-ringtonemanager-action-ringtone-picker
// http://www.ceveni.com/2009/07/ringtone-picker-in-android-with-intent.html
    static public void pickRingtone(int requestCode, String title, String urlMelodi
            ,String tag, Activity activity) {

        Intent intent = new      Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, title);
        // for existing ringtone
        Uri urie;
//        urie =     RingtoneManager.getActualDefaultRingtoneUri(
//                getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        if((urlMelodi != null) && (urlMelodi.length() > 0)){
            try {
                urie = Uri.parse(urlMelodi);
            }catch (Exception e){
                Log.e(tag, " Uri.parse, get default uri. ERR= " + e);
                urie = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Log.v(tag,"goto change melody carent= "+ urie.toString());
        }else{
            urie = null;//тоесть БЕЗ звука!!
            Log.v(tag,"Звука нет urie == null");
        }
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, urie);
        activity.startActivityForResult(intent, requestCode);
    }
}
