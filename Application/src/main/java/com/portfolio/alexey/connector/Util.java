package com.portfolio.alexey.connector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lesa on 17.12.2016.
 */

public class Util {
//    //заносит данные во вювер с контролем на НУЛЛ и длинну строки,
//    // А ТАКЖЕ- если там тоже самое- не записывает в Вювер текст,эеономия времени ОТРИСОВКИ
//    static public boolean setTextToTextView(String str, View view){
//        if((view != null) && (str != null)
//                && (view instanceof TextView) && (str.length() < 64)){
//            // А ТАКЖЕ- если там тоже самое- не записывает в Вювер текст,эеономия времени ОТРИСОВКИ
//            if(((TextView)view).getText().toString().compareTo(str) != 0){
//                ((TextView)view).setText(str);
//            }
//            return true;
//        }
//        return false;
//    }
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

    static public boolean setTextToTextView(String str, int viewID, Activity activity) {
        return setTextToTextView(str, activity.findViewById(viewID),null);
    }
    static public boolean setTextToTextView(String str, int viewID, Activity activity, String def) {
        return setTextToTextView(str, activity.findViewById(viewID),def);
    }
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
