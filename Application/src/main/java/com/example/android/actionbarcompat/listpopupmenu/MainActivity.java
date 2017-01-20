/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.actionbarcompat.listpopupmenu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
//import android.app.FragmentManager;
//import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.portfolio.alexey.connector.BluetoothLeServiceNew;
import com.portfolio.alexey.connector.Util;

import static java.lang.Thread.sleep;

/**
 * This sample shows you how to use {@link android.support.v7.widget.PopupMenu PopupMenu} from
 * ActionBarCompat to create a list, with each item having a dropdown menu.
 * <p>
 * The interesting part of this sample is in {@link PopupListFragment}.
 *
 * This Activity extends from {@link ActionBarActivity}, which provides all of the function
 * necessary to display a compatible Action Bar on devices running Android v2.1+.
 */
//
// LIB !!=> http://developer.alexanderklimov.ru/android/theory/appcompat.php
//fragments-> https://github.com/codepath/android_guides/wiki/creating-and-using-fragments
public class MainActivity extends AppCompatActivity {// ActionBarActivity {
    public  final static String TAG = "MAIN";
//    public  final static String EXTRAS_DEVICE_NAME = "EXTRAS_DEVICE_NAME";
//    public  final static String EXTRAS_DEVICE_NAME_FILTR = "EXTRAS_DEVICE_NAME_FILTR";
//    public  final static String EXTRAS_DEVICE_ADDRESS = "EXTRAS_DEVICE_ADDRESS";
//    public  final static String EXTRAS_DEVICE_ITEM = "EXTRAS_DEVICE_ITEM";
//
// //ОБЯЗАТЕЛЬНО !! ввести в практику передачу тила для порожденного окна!!
//    public  final static String EXTRA_BAR_TITLE = "EXTRA_BAR_TITLE";
//
    public  final static int MAIN_ACTIVITY = 0xFFFF1234;

    //----------
//    Анимация Floating Action Button в Android
//    https://geektimes.ru/company/nixsolutions/blog/276128/
//    Design
//    Downloads!
//    https://developer.android.com/design/downloads/index.html
//    Design
//    Action Bar
//    https://developer.android.com/design/patterns/actionbar.html
//    Настройка ActionBar — панели действий
//    http://www.fandroid.info/nastrojka-paneli-dejstvij-actionbar/
    //
//    Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
//    http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/
//
//    Настройка ActionBar вкладки на Android 4
//    http://ru.androids.help/q11418
//    https://www.youtube.com/watch?v=NYVcfa6Bke4
//
//    Меню
//    https://developer.android.com/guide/topics/ui/menus.html#context-menu
//
//    Menu Resource
//    https://developer.android.com/guide/topics/resources/menu-resource.html
    //Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
    // http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/

    //  программное создоние и подключения слоя
    //  https://github.com/codepath/android_guides/wiki/creating-and-using-fragments
    //http://developer.alexanderklimov.ru/android/theory/layout.php
    //Экземпляр фрагмента связан с активностью. Активность может вызывать методы фрагмента
    // через ссылку на объект фрагмента. Доступ к фрагменту можно получить через
    // методы findFragmentById() или findFragmentByTag().
    // Фрагмент в свою очередь может получить доступ к своей активности через
    // метод Fragment.getActivity().
    //--
    //взаимодействие АКТИВНОсТИ и фрагмента, вызов явно метода из фрагмента, по ссылке на него!
    //там же взаимодействи обратное, работа с АкшионБар и КНОПКА НАЗАД!
    // http://developer.alexanderklimov.ru/android/theory/fragments.php
    private View mLayoutMain;
    private View mImageButtonTransition;
    TransitionDrawable mTransitionDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.sample_main);
        setContentView(R.layout.activity_main);
        //-------------------------------------------------------
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if(app == null)finish();
         //------------------------------
        // показываем заставку релсиба --------------
        //первый запуск показываем заставку,убираем системный бар
        mLayoutMain = findViewById(R.id.LayoutMain);
        mLayoutMain.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
//        //переходы и АНИМАЦИИ примеры ---------
//        //http://startandroid.ru/ru/uroki/vse-uroki-spiskom/392-urok-164-grafika-drawable-level-list-transition-inset-clip-scale.html
//        //http://androidfanclub.ru/programming/%D0%BA%D0%BB%D0%B0%D1%81%D1%81-transitiondrawable
//        //http://developer.alexanderklimov.ru/android/theory/drawable.php#layer-list
//       // mImageButtonTransition = findViewById(R.id.imeg_button_transition);
//        mImageButtonTransition = findViewById(R.id.button_transition);//frame_button
//        //TransitionDrawable td = (TransitionDrawable)getResources().getDrawable(R.drawable.marker_transition);
//        //mImageButtonTransition.setBackground(td);
//        //УСТАНОВЛЕН ДОЛЖЕН БЫТЬ ИМЕННО TransitionDrawable, ИНЧЕ ОБЛОМ В ЭТОМ МЕСТЕ
//        mTransitionDrawable = (TransitionDrawable)mImageButtonTransition.getBackground();
//        //
//        mImageButtonTransition.setOnClickListener(new View.OnClickListener() {
//            private Handler mHandler;
//            private Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    //переход в следующее окно
//                    go();
//                }
//            };
//            @Override
//            public void onClick(View v) {
//                if(mHandler == null){
//                    mHandler = new Handler();
//                } else {//удаляем старые запуски, чтоб небло повторов вызовов
//                    mHandler.removeCallbacks(runnable);
//                }
//                //переход в следующее окно
//                mHandler.postDelayed(runnable,250);
//                //красиво переключемем (ПЕРЕХОД)между рисунком off -> on button
//                mTransitionDrawable.startTransition(300);
//            }
//        });
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Button bt = (Button)v;bt.setPressed(b);
                Log.i(TAG, "----buttonMain-----" );
                //переход в следующее окно
                go();
            }
        });
//ПРИНУДИТЕЛЬНО задал ОРИНТАЦИЮ экрана ВНЕ зависимости от положения устройства
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.e(TAG, "----onCreate END-----");
//      //  ImageView iv = (ImageView)findViewById(R.id.imageView2);
//        TransitionDrawable td = (TransitionDrawable)getResources().getDrawable(R.drawable.marker_transition);
//        findViewById(R.id.frameHeadband).setBackground(td);
//        td.startTransition(5000);
   //     ((TransitionDrawable)(mImageButtonTransition.getBackground())).startTransition(3000);
//        AnimationDrawable ad = (AnimationDrawable)getResources().getDrawable(R.drawable.marker_animation);
//        findViewById(R.id.frame2).setBackground(ad);
//        ad.start();
    }
//    Drawable.Callback cb = new Drawable.Callback(){
//        @Override
//        public void invalidateDrawable(Drawable who) {
//            ((TransitionDrawable)who).startTransition(5000);
//            Log.e(TAG,"-------------invalidateDrawable");
//        }
//        @Override
//        public void scheduleDrawable(Drawable who, Runnable what, long when) {
//            Log.e(TAG,"-------------scheduleDrawable");
//        }
//        @Override
//        public void unscheduleDrawable(Drawable who, Runnable what) {
//            Log.e(TAG,"-------------unscheduleDrawable");
//        }
//    };

//переход в следующее окно
    private void go() {
        //контроль ВСЕХ настроек и запуска СЕРВИСА,
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if((app == null) || (app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)){
            Log.e(TAG,"ERROR app || service || getFile from flash");
            finish();
        }
        //переход в следующее окно
        Intent intent = new Intent(this, MainActivityWork.class);
      //  intent.putExtra(Util.EXTRAS_BAR_TITLE, "v2.6.16 Температура");//--
        intent.putExtra(Util.EXTRAS_BAR_TITLE, "v2.6.24");//--
        // все изменения будет писать сразу в сенсор
        // по умолчанию устанавливаем минимум, все остальное делется НАПРЯМУЮ с данными
        //   startActivityForResult(intent,MAIN_ACTIVITY);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //при возвращениие из других окон, может быть системный бар, по этому еще раз его отменяем
        mLayoutMain.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //а первый запуск показываем заставку,убираем системный бар
        getSupportActionBar().hide();
        //быстро возвращяем назад кнопку в  OFF, рисунк ON -> off button
//        mTransitionDrawable.resetTransition();
       // mTransitionDrawable.reverseTransition(500);//resetTransition();

        Log.e(TAG, "----onResume() ----------");
    }
    @Override
    public  void onPause() {
        super.onPause();
    }
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "----onActivityResult ----------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            Log.e(TAG,"onDestroy() ==============isFinishing() =========== isFinishing() ======");
        } else {
            Log.e(TAG,"onDestroy() -----------WORK ------------- WORK -------------");
        }
    }
}
//    private void go(){
//        RunDataHub app;int i=0;
//        //максимум ВСЕХ настроек и запуска СЕРВИСА, чтение флеши телефона,ждем 6 секунд и финиш
//        while (true){
//            app = ((RunDataHub) getApplicationContext());
//            if((app != null) && (app.mBluetoothLeServiceM != null)
//                    && (app.mBluetoothLeServiceM.arraySensors != null))break;
//            //ожидание готовности
//            try {
//                sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            // больше 6 секуд- выходим из приложения- проблеммы у него
//            if(i++> 20){
//                Log.e(TAG,"ERROR app || service || getFile from flash");
//                finish();
//            }
//            Log.w(TAG,"wait tame(ms)= " +i*300);
//        }
//        Intent intent = new Intent(this, MainActivityWork.class);
//        intent.putExtra(Util.EXTRAS_BAR_TITLE, "     B1/B3 v2.5.9");
//        // все изменения будет писать сразу в сенсор
//        // по умолчанию устанавливаем минимум, все остальное делется НАПРЯМУЮ с данными
//        //   startActivityForResult(intent,MAIN_ACTIVITY);
//        startActivity(intent);
//    }