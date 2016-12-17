package com.portfolio.alexey.connector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.actionbarcompat.listpopupmenu.R;

/**
 * Created by lesa on 17.12.2016.
 */

public class Util {
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
