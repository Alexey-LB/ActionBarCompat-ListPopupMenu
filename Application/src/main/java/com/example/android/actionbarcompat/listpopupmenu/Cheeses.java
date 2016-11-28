/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.actionbarcompat.listpopupmenu;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Dummy data. bjhkbk
 */
//http://developer.alexanderklimov.ru/android/tips-android.php
    //Разрешённый объём памяти для приложения
    //--
    //http://remotexy.com/ru/examples/temperature/
//http://learn-android.ru/news/konverter_temperatury/2015-01-07-8.html
    //МЕРЦАЮЩИЙ ТЕКСТ и т.д. http://developer.alexanderklimov.ru/android/views/textview.php

//http://fedyaevdmitriy.livejournal.com/245808.html
//пожалуй самые ходовые:
//        0136 € символ евро
//        0149 • «жирная» точка по центру
//        0153 ™ символ trade mark
//        0167 § параграф
//        0169 © символ копирайта
//        0174 ®
//        0176 ° символ градуса
//        0177 ±
//
//        и ещё:
//        0123 (или 123) {
//        0124 (или 124) |
//        0125 (или 125) }
//        0126 (или 126) ~
//        0130 ‚ нижняя одинарная кавычка
//        0132 „ открывающая «лапка»
//        0133 … многоточие
//        0134 † крестик (dagger)
//        0135 ‡ двойной крестик (double dagger)
//        0137 ‰ символ промилле
//        0139 ‹ левый «уголок»
//        0143 Џ
//        0145 ‘ верхняя одинарная кавычка (перевернутый апостроф)
//        0146 ’ апостроф
//        0147 “ закрывающая «лапка»
//        0148 ” английская закрывающая «лапка»
//        0150 – короткое тире (минус)
//        0151 — тире
//        0155 › правый «уголок»
//        0159 џ
//        0166 ¦
//        0171 « открывающая «ёлочка»
//        0172 ¬
//        0181 µ
//        0182 ¶
//        0183 · точка по центру
//        0185 №
//        0187 » закрывающая «ёлочка»
public class Cheeses  {

    public  TreeMap<Integer,Object> value = new TreeMap();
//    {{
//        put("textValueLeft",null);
//        put("textValueCenter",null);
//        put("textValueRight",null);
//        put("timgTitle",null);
//        put("textSignal",null);
//        put("textBattery",null);
//        put("itemTitle",null);
//    }};

    private final String teg ="Cheeses";
    private Handler changeValue = new Handler();



    public static final String[] CHEESES = {
            "Монитор", "Термометр", "Барометр"//, "Ybond", "Xckawi"
    };
    private float getNewValue(float min, float max){
        return (float)( Math.random() * (max-min) + min);
    }
////
//    map.put("textValueLeft", view.findViewById(R.id.numbe_min));
//    map.put("textValueCenter", view.findViewById(R.id.numbe_cur));
//    map.put("textValueRight", view.findViewById(R.id.numbe_max));
//    map.put("imgTitle", view.findViewById(R.id.imgTitle));
//    map.put("signal", view.findViewById(R.id.signal));
//    map.put("battery", view.findViewById(R.id.battery));
//    map.put("itemTitle", view.findViewById(R.id.text1));

    public final int index;
    private  void loop(){

        changeValue.postDelayed(new Runnable() {
            @Override
            public void run() {
                Object obj;
                for(Map.Entry es : value.entrySet()){
                    obj= es.getValue();
                    if(obj != null){

                        if(obj instanceof TextView){
                            ((TextView) obj).setText(String.format("%2.1f C",getNewValue(20f,100f)));//\176
                        } else{
                            if(obj instanceof ImageView){
                                ((ImageView)obj).setBackgroundColor((int)getNewValue(-9999999999f,9999999999f));
                            }
                        }
                    }
                }
                loop();
            }
        }, 1000);
    }
    //
    Cheeses(int in){

        index = in;
        loop();
    }

    public String getObject(){
        return CHEESES[index];
    }


    private String name(){
        String str = "?";
       // if(CHEESES.length > index){
            str = CHEESES[index % 3];
      //  }
        return str + " " +index;
    }
    public String toString(){
        return name();
    }

}