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

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.usage.UsageEvents;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.ActionMenuView;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeMap;

import static android.content.Context.BIND_AUTO_CREATE;
//Анимация Floating Action Button в Android
//        https://geektimes.ru/company/nixsolutions/blog/276128/
//
//
//        Design
//        Downloads!
//        https://developer.android.com/design/downloads/index.html
//
//        Design
//        Action Bar
//        https://developer.android.com/design/patterns/actionbar.html
//
//        Настройка ActionBar — панели действий
//        http://www.fandroid.info/nastrojka-paneli-dejstvij-actionbar/
//
//        Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
//        http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/
//
//        Настройка ActionBar вкладки на Android 4
//        http://ru.androids.help/q11418
//        https://www.youtube.com/watch?v=NYVcfa6Bke4
//
//        Меню ..
//        https://developer.android.com/guide/topics/ui/menus.html#context-menu
//
//        Menu Resource
//        https://developer.android.com/guide/topics/resources/menu-resource.html

//Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
// http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/

//Develop API Guides User Interface Меню
// https://developer.android.com/guide/topics/ui/menus.html#context-menu

// КАК ВЫГЛЯДИТ на различных Верисях кнопки БАРЫ: МЕНЮ(http://androiddrawables.com/Menu.html) и Т Д
// http://androiddrawables.com/  - здесь ссылка на ВСЕ!!
// Бесплатные ресурса, например ИСХОДНИКИ ИКОНОК
// http://apptractor.ru/develop/dizayn-i-prototipirovanie

//---
//Грыфика и фомирование полей с закругленными краями градиентом и т д
//http://developer.alexanderklimov.ru/android/design/tilemode.php#3dpanel
//http://developer.alexanderklimov.ru/android/theory/drawable.php
//--- АНИМАЦИЯ
// http://ru-code-android.livejournal.com/5392.html
//-- ФЛОАТ кнопка
//http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/


/**
 * This ListFragment displays a list of cheeses, with a clickable viewScroll on each item whichs displays
 * a {@link android.support.v7.widget.PopupMenu PopupMenu} when clicked, allowing the user to
 * remove the item from the list.
 */
//public class PopupListFragment extends ListFragmentA implements OnClickListener  {//если встраивать обработку нажатия
public class PopupListFragment extends ListFragmentA  {
    private static final String TAG = "PopListFA";
    private Menu menuFragment;
    private boolean dellItem= false;
    public  MainActivity parentActivity;

    private static int index_object = 0;
    private final int ofset_y = 60;

    //private FloatingActionButton fbButton;
    private View fbButton = null;
    private View fbButton2 = null;
    private ArrayAdapter adapter;//хранитель обектоа и их манипуляции с ними
    private final int dividerHeight = 3;//расстояние в dp  между ЭЛЕМЕНТАИ списка в попЛист
    private final int OverFloatButton = 3;// наезд Плавающей кнопки на ЭЛЕМЕНТ списка попЛиста в dp
    private final int hightPopListItem = 64;//высота Элемента попЛиста в Dp
    private final int hightButton = 40;//высота (диаметр) ПЛАВАЮЩЕЙ кнопки в Dp
    public final int maxChidren = 5;//максмальное количество датчиков для регистрации, пока такое ограгичение
    private final int CLICK_SHORT = 5;
    private final int CLICK_LONG = 10;
    private final int CLICK_DOWN = 15;
    private final int CLICK_MOVE = 20;
    private final int CLICK_UP = 25;
    private final int CLICK_ADD = 30;

    private ObjectDataToView objectDataToView = new ObjectDataToView();
 //   public byte shift = 5;//первичное смещение - показ КРАСНОГО СООБЩЕНИЯ ОБ УДАЛЕНИИ- индикация режима выбора
    //---------ПЕРЕСТРАИВАЕТСЯ всегда ПРИ ПОВОРОТЕ ТОЖЕ!! по этому определяем пльтность ЗДЕСЬ пикселей
    //определяем ПЛОТНОСТЬ пикселей на экран, ана разная для различных устройств, может отличатся по Х и У
    // за еденицу берется 160 пикселей на 1 дюйм(25.4мм)
    //        getResources().getDisplayMetrics().widthPixels;//абсалютное количество пикселей по ширине
//        getResources().getDisplayMetrics().scaledDensity;//фактор масштабирования для шрифтов, отображаемых на дисплее
//        getResources().getDisplayMetrics().xdpi;//точное количество пикселей по ширине экрана на один дюймна 1 дюйм (25.4 мм)
//        getResources().getDisplayMetrics().ydpi;//точное количество пикселей по высоте экрана на один дюймна 1 дюйм (25.4 мм)
//используетс для формирования КНОПОК МЕНЮ и ТД
    //        getResources().getDisplayMetrics().density
    //        getResources().getDisplayMetrics().densityDpi;//примерная обобщенная оценка кол. пикселей на один дюймна 1 дюйм (25.4 мм)
//    - low (ldpi) = 0,75
//     medium (mdpi) = 1
//     tv (tvdpi) = 1,33
//     - high (hdpi) = 1,5
//     - extra high (xhdpi) = 2
    private  float densityX;//это для ГРАФИКИ - точного представления соотношений РИСУНКА!!но НЕ для меню!!
    private float densityY;//это для ГРАФИКИ - точного представления соотношений РИСУНКА!!но НЕ для меню!!
    //------------------------
    private float density;//// ДЛЯ размера меню И ТД, используется density !!!
    private  int  DispleyWidthPixels;
    private int  DispleyHeightPixels;
    private  int  DispleyWidthDp;
    private int  DispleyHeightDp;
    // ДЛЯ размера меню И ТД, используется density !!!
    public int getDp(int x){return (int)((float)x /density);}
    public int getPixels(int xDp){return (int)(density *(float)xDp);}
    //это для ГРАФИКИ - точного представления соотношений РИСУНКА!!но НЕ для меню!!
    public int getGraphDpX(int x){return (int)((float)x /densityX);}
    public int getGraphDpY(int y){return (int)((float)y /densityY);}
    public int getGraphPixelsX(int xDp){return (int)(densityX *(float)xDp);}
    public int getGraphPixelsY(int yDp){return (int)(densityY *(float)yDp);}

    @SuppressWarnings("ResourceType")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG,"Fragment --- onActivityCreated-----STaRT--");
        super.onActivityCreated(savedInstanceState);

        ListView lw = getListView();
        View root;
        if(lw == null ){
            System.out.println("onActivityCreated ERROR: ListView=null");
            return;
        }
        root =  lw.getRootView();
        if(lw == null ){
            System.out.println("onActivityCreated ERROR: View root=null");
            return;
        }

        //убрать системный бар----------------
        //if(root.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_FULLSCREEN)
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//========================


      //  android.app.
                ActionBar ab = getActivity().getActionBar();
        System.out.println("-----ActionBar=" + ab);
//        if(ab != null){
//            ab.setIcon(R.drawable.alexey_photor_fo_visa);
//        }
        //---------
        //это для ГРАФИКИ - точного представления соотношений РИСУНКА!!но НЕ для меню!!
        densityX = getResources().getDisplayMetrics().xdpi/160f;
        densityY = getResources().getDisplayMetrics().ydpi/160f;
        //
        // ДЛЯ размера меню И ТД, используется density !!!
        density = getResources().getDisplayMetrics().density;
        DispleyWidthPixels= getResources().getDisplayMetrics().widthPixels;
        DispleyHeightPixels = getResources().getDisplayMetrics().heightPixels;
        DispleyWidthDp = (int)((float)DispleyWidthPixels /density);
        DispleyHeightDp = (int)((float)DispleyHeightPixels /density);
        //-------------------
        ////расстояние в dp  между ЭЛЕМЕНТАИ списка в попЛист, переводим в пикселы
        lw.setDividerHeight(getPixels(dividerHeight));//РАССТОЯНИЕ МЕЖДУ элементами списка!!
        //
    // при создании заталкиваем перечень названий в лист? с котоого потом и разворачиваем рор лист
        // We want to allow modifications to the list so copy the dummy data array into an ArrayList

//        for (int i = 0, z = Cheeses.CHEESES.length ; i < z ; i++) {
//            items.add(Cheeses.CHEESES[i]);
//        }
        //переходим на вариант объекта
//        for (int i = 0, z = Cheeses.CHEESES.length ; i < z ; i++) {
//            items.add(new Cheeses(i));
//        }
      //  items.add("еуые++");
        // Set the ListAdapter
        // в адаптере навести все ссылки на отображение данных Объекта-

//        ArrayList<Sensor> item =  mBluetoothLeService.mbleDot;
//        ArrayList<Object> it = (ArrayList)(Object)item;
//                PopupAdapter pop = new PopupAdapter(it);
 //------------------
        // ArrayList<Object> items = new ArrayList<Object>();
//        PopupAdapter pop = new PopupAdapter(items);
//        setListAdapter(pop);//создали адаптер для работы
//        //final ArrayAdapter
//        adapter = (ArrayAdapter) getListAdapter();//int count = adapter.getCount();
 //-----------
        //View v = (View)adapter.get(count -1);
       // ViewGroup vg = ((ViewGroup) v.getParent().getParent());
        //getActionBar();
        //setMenuVisibility(false);
        Log.d(TAG,"  getActivity().getActionBar()=" + getActivity().getActionBar());
        //
        //-------------Установка плавающей копки --------------------------------
        fbButton = View.inflate(getContext(),R.layout.poplist_item_3,null);//породил ИЗ ХМЛ, просто рисунок!!
      //  fbButton2 = getFloatButton();

        //Log.e(teg,"  fbButton.getHeight()=" + fbButton.getHeight());
        //бросаем на экран
  ((ViewGroup)lw.getParent()).addView(fbButton);
   //----------ListFragmentA строка 98 там создается скролинг !----------------
        View mProgressContainer = root.findViewById(ListFragmentA.INTERNAL_PROGRESS_CONTAINER_ID);//
        View mListContainer = root.findViewById(ListFragmentA.INTERNAL_LIST_CONTAINER_ID);
        View rawListView = root.findViewById(android.R.id.list);
        Log.i(TAG,"mProgressContainer=" + mProgressContainer
                +"\nmListContainer=" + mListContainer
                +"\nrawListView=" + rawListView
        );
 // ((ViewGroup)rawListView ).addView(fbButton);
        //======================================
        //Log.e(teg,"  fbButton.getHeight()=" + fbButton.getHeight());

     //   ((ViewGroup)lw.getParent()).addView(fbButton2);
       // ((ViewGroup)root).addView(fbButton);
  objectDataToView.moveButton();//позиционируем
        //слушательна кнопку: Если она в КОНТЕЙНЕРЕ, то ише ее там по ИД, иначе вешаем на все
        View vie = fbButton.findViewById(R.id.floatingActionButton);
        if(vie == null) vie = fbButton;//просто кнопка БЕЗ контейнера
        vie.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                objectDataToView.controlObject(CLICK_ADD,null,0);
             }
        });
        //http://developer.alexanderklimov.ru/android/theory/fragments.php
        //ДОБАВЛЕНИЯ СВОЕГО МЕНЮ ИЗ ФРАГМЕНТА!!
        setHasOptionsMenu(true);
        //этот метод должен вроде сохранять отображение фрагмента при повороте ТЕЛЕфона НО!!
        // http://developer.alexanderklimov.ru/android/theory/fragments.php
        setRetainInstance(true);
// ЗАПУСТИЛИ ервис       //-------------ЗАПУСТИЛИ ервис ---------
//        Intent gattServiceIntent = new Intent(this.getActivity(), BluetoothLeServiceNew.class);
//        this.getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//        //-----------------------------
  //      ((MainActivity)getActivity()).
        Log.e(TAG,"Fragment --- onActivityCreated---END----");
    }
    // ЗАПУСТИЛИ ервис    public BluetoothLeServiceNew mBluetoothLeService = null;
    public void initList(){
        Log.e(TAG,"Activity to frag initList()---");
        //            setListAdapter(pop);//создали адаптер для работы
        //--------ЭТО делать надо  1 раз только иначе падает!!
  //      ArrayList<Sensor> item =  mBluetoothLeService.mbleDot;
     //     ArrayList<Sensor> item =  parentActivity.mBluetoothLeServiceM.mbleDot;
        ArrayList<Sensor> item = ((MainActivity)getActivity()).mBluetoothLeServiceM.mbleDot;
        ArrayList<Object> it = (ArrayList)(Object)item;
        PopupAdapter pop = new PopupAdapter(it);

        setListAdapter(pop);//создали адаптер для работы
        //final ArrayAdapter
        adapter = (ArrayAdapter) getListAdapter();//int count = adapter.getCount();

    }
// ЗАПУСТИЛИ ервис    public void upd(){
//       this.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.w(TAG,"PopupAdapter= ");
//
//                ArrayList<Sensor> item =  mBluetoothLeService.mbleDot;
//                ArrayList<Object> it = (ArrayList)(Object)item;
//                PopupAdapter pop = new PopupAdapter(it);
//
//                setListAdapter(pop);//создали адаптер для работы
//                //mLeDeviceListAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//// ЗАПУСТИЛИ ервис    // Code to manage Service lifecycle.
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            //     mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//            mBluetoothLeService = ((BluetoothLeServiceNew.LocalBinder) service).getService();
//            if (!mBluetoothLeService.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
//             }
//            if(mBluetoothLeService == null)Log.w(TAG,"mBluetoothLeService= null");
//            if(mBluetoothLeService.mbleDot == null)Log.w(TAG,"mBluetoothLeService.mbleDot= null");
//     //       parentActivity
//   //         mBluetoothLeService.mbleDot.add(new Sensor());
//   //         mBluetoothLeService.mbleDot.add(new Sensor());
//    //        mBluetoothLeService.connect("74:DA:EA:9F:54:C9",true);
//Log.w(TAG,"mBluetoothLeService.mbleDot= " + mBluetoothLeService.mbleDot +
//"   size= " +  mBluetoothLeService.mbleDot.size());
//
//       //--------------------
////            ArrayList<Object> items = new ArrayList<Object>();
////            PopupAdapter pop = new PopupAdapter(items);
//////            setListAdapter(pop);//создали адаптер для работы
////            //--------ЭТО делать надо  1 раз только иначе падает!!
////            ArrayList<Sensor> item =  mBluetoothLeService.mbleDot;
////          //  ArrayList<Sensor> item =  parentActivity.mBluetoothLeServiceM.mbleDot;
////            ArrayList<Object> it = (ArrayList)(Object)item;
////            PopupAdapter pop = new PopupAdapter(it);
////
////            setListAdapter(pop);//создали адаптер для работы
////            //final ArrayAdapter
////            adapter = (ArrayAdapter) getListAdapter();//int count = adapter.getCount();
//            //addObject(new Sensor());
//       //-----------
//            // upd();
//
//            // Automatically connects to the device upon successful start-up initialization.
//            //         mBluetoothLeService.connect(mDeviceAddress,true);
//            Log.w(TAG, "---initialize ---onServiceConnected-----");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBluetoothLeService = null;
//            Log.v(TAG, "onServiceDisconnected");
//        }
//    };
    ///--------------------------------------------------------------------------------------
    final int iconActionEdit = 12345678;
    final int iconActionAdd = 23456789;

    //http://developer.alexanderklimov.ru/android/theory/fragments.php
    //ДОБАВЛЕНИЯ СВОЕГО МЕНЮ ИЗ ФРАГМЕНТА!!
    //вызывается при построениии и после вызова метода invalidateOptionsMenu();
    //ДЛЯ НАШЕГО случая-- this.getActivity().invalidateOptionsMenu();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuFragment = menu;//запомил, чтоб потом  изменить меню или удалить ПРИ ВЫХОДЕ из фрейма
       // inflater.inflate(R.menu.myfragment_options, menu);
        menu.add(Menu.NONE,iconActionEdit,Menu.NONE,"Edit")
                .setIcon(R.drawable.ic_clear_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE,iconActionAdd,Menu.NONE,"Add")
                .setIcon(R.drawable.ic_add_black_32dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
       // menu.clear();
    }
    @Override
public void onPrepareOptionsMenu(Menu menu){

}
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(menuFragment != null)menuFragment.clear();

// ЗАПУСТИЛИ ервис       this.getActivity().unbindService(mServiceConnection);
// ЗАПУСТИЛИ ервис      mBluetoothLeService = null;
        //Fragment
       // this.getActivity().invalidateOptionsMenu();
    }
    //Видимые и не видимые символы РЕДАКТИРОВАНИЯ
    private void setDellItemView(boolean visibilityDell){
        ListView lw = getListView();ViewGroup vg;
        int vis = View.GONE;
        if(visibilityDell) vis = View.VISIBLE;
        //Видимые и не видимые символы РЕДАКТИРОВАНИЯ
        for(int i=0;i < lw.getCount(); i++){
            vg =  (ViewGroup)lw.getChildAt(i);
            vg.getChildAt(2).setVisibility(vis);
        }
    }
    //http://developer.alexanderklimov.ru/android/theory/fragments.php
    //ДОБАВЛЕНИЯ СВОЕГО МЕНЮ ИЗ ФРАГМЕНТА!!
    // СЮДА прилетают ВСЕ клики по меню, также и кнопка назад!!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG,"android.R.id.home");
     //сохранение в файл
                ((MainActivity)getActivity()).mBluetoothLeServiceM.settingPutFile();
 //               adapter.notifyDataSetChanged();
                return true;
            case iconActionEdit:
                Log.i(TAG,"edit-");
//Видимые и не видимые символы РЕДАКТИРОВАНИЯ
                dellItem = !dellItem;
                setDellItemView(dellItem );
        //вызов активного окна для сканирования
    //!!ОБламывает, возвращяетс назад НЕ запускает!!            ((MainActivity)getActivity()).onScanDevice(1);

  //              adapter.notifyDataSetChanged();
                return true;
            case iconActionAdd:
                Log.i(TAG,"ADD+");
                addNoInitObject();

              //  if(menuFragment != null)menuFragment.clear();
                return true;
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }

    //получение плавающей кнопки НАСТОЯЩЕЙ- ГЛЮЧИТ от АРИ 19 к АРИ 25: появляется отступ у нее вверху и слева!!
    private View getFloatButton(){
        // ВАРИАТ 1 изначально как по документации -НО ГЛЯЧИТ в разных АРИ ОТСТУПЫ!!
        //-------------Установка плавающей копки --------------------------------
        //fbButton = 40dp - mini, 56dp-norm//getMetricsDispleyX();// float density = getResources().getDisplayMetrics().density;
        //http://java-help.ru/floating-action-button-android/
        FloatingActionButton fbButton_ = new FloatingActionButton(getContext());
        //fbButton.setForegroundGravity(Gravity.CENTER);
        fbButton_.setImageResource(R.drawable.ic_add_white_24dp);//ic_input_add
        // button.setImageResource();
        fbButton_.setSize(FloatingActionButton.SIZE_MINI);
        fbButton_.setLayoutParams(new LinearLayout.LayoutParams(ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.MATCH_PARENT));
return null;//fbButton_;

        // ВАРИАНТ 2) создаем кнопку внутри Слоя, чтоб он автоматически позиционировал ее по центру и ТД, но все бестолку!
        //добавляю к контэйнеру В КОТОРМ лежит и список отображаемых объектов, тоесть находится
        // на том же уровне и в том же пространстве
//        return View.inflate(getContext(),R.layout.poplist_button_float, null);

        //ДОБАВИТЬ НАДО К листу- иначе СКРОЛИНГ не ЗАХВАТЫВАЕТ кнопку? она исчезает за пределами экрана!
        // ((ViewGroup)lw.getRootView()).addView(fbButton);//((ViewGroup)lw.getRootView().getParent()).addView(button);
        //-----------------------------------------------
        //
//        LinearLayout myContainer = new LinearLayout(getContext());
//        myContainer.setLayoutParams(new LinearLayout.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
//                ActionMenuView.LayoutParams.MATCH_PARENT));
//        myContainer.setOrientation(LinearLayout.HORIZONTAL);
//        myContainer.setHorizontalGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
//        myContainer.addView(fbButton_);
        //       fbButton = (View)myContainer;
    }
    // добавить объект данные которого отображаются на листе
    public boolean addNoInitObject(){
        Log.i(TAG," addd------------");
        if(maxChidren <= adapter.getCount()) return false;
       // adapter.add(new Cheeses(index_object++));
        //без адреса включается иммитатор
        adapter.add(new Sensor());
        objectDataToView.moveButton();//позиционируем
        return true;
    }
    // добавить объект данные которого отображаются на листе
    public boolean addObject(Object object){
        Log.i(TAG," addd------------");
        if(maxChidren <= adapter.getCount()) return false;
        // adapter.add(new Cheeses(index_object++));
        //без адреса включается иммитатор
       // adapter.add(object);
 //       mBluetoothLeService.mbleDot.add((Sensor) object);
        ((MainActivity)getActivity()).mBluetoothLeServiceM.mbleDot.add((Sensor) object);
        objectDataToView.moveButton();//позиционируем
        return true;
    }
    ///распечатка ХАРАКТЕРИСТИКИ ДИСПЛЕЯ- ЭКРАНА устройства
    public void getMetricsDispleyX(){
        //http://developer.alexanderklimov.ru/android/theory/scales.php
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Log.i(TAG," widthPixels= "+dm.widthPixels + "   heightPixels= "+dm.heightPixels
        +"\n   densityDpi= "+dm.densityDpi+"   density= "+dm.density+"  scaledDensity= "+dm.scaledDensity
        +"\n   xdpi= " + dm.xdpi +"   ydpi= "+ dm.ydpi);
        //        getResources().getDisplayMetrics().widthPixels;//абсалютное количество пикселей по ширине
//        getResources().getDisplayMetrics().scaledDensity;//фактор масштабирования для шрифтов, отображаемых на дисплее
//        getResources().getDisplayMetrics().xdpi;//точное количество пикселей по ширине экрана на один дюймна 1 дюйм (25.4 мм)
//        getResources().getDisplayMetrics().ydpi;//точное количество пикселей по высоте экрана на один дюймна 1 дюйм (25.4 мм)
//        getResources().getDisplayMetrics().densityDpi;//примерная обобщенная оценка кол. пикселей на один дюймна 1 дюйм (25.4 мм)
    }
    /**
     * Класс обеспечивающий по событиям добавлять удалять объекты и вызывать события для вызова других окон
     */
    class ObjectDataToView{
        private boolean stateDell = false;//флаг начала сдвига
        public byte initShift = 5;//dp//первичное смещение - показ КРАСНОГО СООБЩЕНИЯ ОБ УДАЛЕНИИ- индикация режима выбора
         private int positionStartX = -1;//Позиция где сработало касания пальца- НАЧАЛА движения
        private int positionScroll = -1;//на сколько сдвинулись
        private int  maxScroll = 100;//dp -- макисмально количиство на сколько открываме в др
        // хранит данные, кторые отображаются в вювере
        private Object objectD = null;// объект с которым маипулируем, добавляем -удаляем, позицию в списке вычисляем через него
        //ObjectDataToView(){;}
        //получение Объекта по позиции
        private Object getObject(int position){
            if((position < 0) && (position >= adapter.getCount())) return null;
            return adapter.getItem(position);
        }
        //получение индекса по объекту
        public  int getIndex(Object obj){
            int i = -1;
            if(obj == null)return i;
            i = adapter.getPosition(objectD);//работаеем только с объектами, их запоминаем, позиции для временвх значение
            if((i < 0) || (i >= adapter.getCount())) return -1;
            return i;
        }
        // получение Вювера по объекту, данные которого он отображает
        public  View getView(Object obj){
            int i = getIndex(obj);
            if(i < 0)return null;
            return  getListView().getChildAt(i);
        }
        //смещение по оси Х Вювера, который отображает данные Объекта
        // при этои открывается нижний слой, либо УДАЛИТЬ, либо ИЗМЕНИТЬ
        public  boolean setScrollX_View(View vie, int x){
            if((vie == null) || (((ViewGroup)vie).getChildAt(1) == null)) return false;
            //
            vie = ((ViewGroup) vie).getChildAt(1);
            //работаем не с пикселами а Dp
            if((getDp(vie.getScrollX()) != x) && (Math.abs(x) <= DispleyWidthDp)){
                vie.setScrollX(getPixels(x));

            }
            return true;
        }
        //смещение по Объекту, на расстояние Х, Вювер вычисляется от объекта
        public  boolean setScrollX_View(Object obj, int x){return setScrollX_View(getView(obj), x);}
        private  boolean setScrollX_View(int x){return setScrollX_View(getView(objectD), x);}
        //вренуть наместщ (Х = 0)
        private boolean clearScrollX_View(){
            // берем 2 слой (индекс 1) и его сдвигаем над 1 слоем(индекс 0)
            boolean rez = setScrollX_View(getView(objectD), 0);
            objectD = null;//очищяем? чтоб больше не было операций с ним//затираем на него ссылку? с ним закончили
            stateDell = false;
            return rez;
        }
        //https://geektimes.ru/company/nixsolutions/blog/276128/ --для плавающей кнопки
        // http://ru-code-android.livejournal.com/5392.html
        /*
        -AccelerateInterpolator (@android:anim/accelerate_interpolator) - скорость изменения в начале низкая, а затем ускоряется
-AnticipateInterpolator (@android:anim/anticipate_interpolator) - изменения начинаются в обратную сторону, а затем резко двигаются вперед
-AnticipateOvershootInterpolator (@android:anim/anticipate_overshoot_interpolator) - изменения начинаются в обратную сторону, затем резко двигаются вперед и пролетают выше конечного значения, а затем возвращаются до конечного значения
-BounceInterpolator (@android:anim/bounce_interpolator) - скорость изменения увеличивается в конце
-CycleInterpolator (@android:anim/cycle_interpolator) - повторение анимации указанное число раз. Скорость изменения следует синусоиде
-DecelerateInterpolator (@android:anim/decelerate_interpolator) - скорость изменения уменьшается в конце
-LinearInterpolator (@android:anim/linear_interpolator) - скорость изменения постоянна
-OvershootInterpolator (@android:anim/overshoot_interpolator) - изменения резко двигаются вперед и пролетают выше конечного значения, а затем возвращаются до конечного значения
         */
        // Вювер имеет 3 слоя, 1 (0 индекс)- это имеджи ИЗМЕНИТЬ и удалить, которые неподвижны и открываются
        // если слой 2 (индекс 1), сдвигать ВЛЕВО / ВПРАВО
        // слой 3 (индекс 2)- это РЕДАКТИРОВАТЬ- подготовка к УДАЛЕНИЮ- невидим в исходном состояниии
        private boolean clearScrollX_View(boolean animate){
            // берем 2 слой (индекс 1) и его сдвигаем над 1 слоем(индекс 0)
            clearScrollX_View();
            if(true)return true;
            ViewGroup  vie =(ViewGroup)getView(objectD); boolean rez = false;
            // берем 2 слой (индекс 1) и его сдвигаем над 1 слоем(индекс 0)
            if((vie != null) && (vie.getChildAt(1) != null)){
                vie = (ViewGroup)vie.getChildAt(1);
//                Log.v( teg,"animate");
               // vie.animate().translationX(vie.getScrollX()).setInterpolator(new LinearInterpolator()).start();
                //vie.animate().translationX(vie.getScrollX()).setInterpolator(new AnticipateOvershootInterpolator()).start();

                //vie.animate().translationX(vie.getScrollX()).setInterpolator(new AccelerateInterpolator()).start();
               // vie.animate().translationX(vie.getScrollX()).setInterpolator(new DecelerateInterpolator()).start();
                vie.animate().translationX(getPixels(vie.getScrollX())).setInterpolator(new OvershootInterpolator()).setDuration(5000).start();
                rez = true;
            }
            objectD = null;//очищяем? чтоб больше не было операций с ним//затираем на него ссылку? с ним закончили
            stateDell = false;
            return rez;
        }
        // пермещение плавное кнопки, с учетом размера итемов и расстояний между ними, используем\
        // масштабный коэффициент плотности пикселов на дюйм, который примерный, поскольку он использукется для МЕНЮ
        public  void moveButton(){
            int offset, i= 0;ListView lw = getListView();
            //fbButton = 40dp - mini, 56dp-norm//getMetricsDispleyX();// float density = getResources().getDisplayMetrics().density;
            //позиционирование по середине= берем 1/2 экрана в пикселах, и ВЫЧИТАЕМ 1/2 ширины (40/2=20) кнопки УМНОЖЕННУЮ на density
   //         fbButton.setX((DispleyWidthPixels >> 1) -getPixels(hightButton >> 1 ));//getPixels(20) при density=3, получим 60dpi  !
 //           fbButton.setX(DispleyWidthPixels >> 4);
            fbButton.setVisibility(View.VISIBLE);//иногда ее выключаем? по этому !! всегда включаем((View)fbButton.getParent()).getWidth()
            if(adapter != null)i = adapter.getCount();
            if(i > 0) {// в дальнейше, ОПРЕДЕЛИ 64- через высоту реального объекта!!
                offset = i  * getPixels(hightPopListItem) + getPixels((dividerHeight) * i); //высота наших лист вюверов + РАССТОЯНИЕ МЕЖДУ ними!!
                offset -= getPixels(OverFloatButton + dividerHeight)  ;  //небольшой наезд на последнюю
            } else{
                offset = 0;
               // i -= getPixelsY(OverFloatButton) ;//скрывается под баром. ПОТОМ надо разобраться
            }
 //2016.11.23 выяснил, точные значения только к ГРАФИКЕ применяютс, ДЛЯ МЕНЮ КНОПОК и ТД- примерное!!
  // иначе РАСХОЖДЕНИЯ!! код ниже я пытался обойти это!
//            //ПОСКОЛЬКУ ошибки в СИМУЛЯТОРЕ и реальных устройства по расчету нахождения края Итемов
//            // берем просто ПОЛОЖЕНИЯ У у предпоследнего, поскольку ПОСЛЕДНИЙ ЕЩЕ НЕ сформирован!!
//            offset = - getPixels(OverFloatButton );//вычисляем смещение
//            ListView lw = getListView();
//            i = lw.getCount();
//            if(i < 2){
//                if(i == 1) offset += getPixels(hightPopListItem);
//            } else{
//                //берем просто ПОЛОЖЕНИЯ У у предпоследнего, поскольку ПОСЛЕДНИЙ ЕЩЕ НЕ сформирован!!  lw.getChildAt(lw.getCount() - 1) == null !!
//                View vie = lw.getChildAt(i - 2);
//                if(vie != null) offset = (int)(vie.getY() + 2 * vie.getHeight() - lw.getDividerHeight());
//            }
// Log.e( teg," getPixelsY(hightPopListItem)= " + getPixels(hightPopListItem)+ "   getPixelsY(OverFloatButton )= " + getPixels(OverFloatButton )
//            + "   getTop= " +lw.getTop() + "   getHeight= " +lw.getHeight() +"   getY= " +lw.getY());
//            Log.e( teg," density= " + density + " densityY= " + densityY + "  ydpi= " +getResources().getDisplayMetrics().ydpi + "  .density= " +getResources().getDisplayMetrics().density);
//            Log.e( teg," densityX= " + densityX + "  xdpi= " +getResources().getDisplayMetrics().xdpi+ "  ..densityDpi= " +getResources().getDisplayMetrics().densityDpi);

            //  Log.e( teg,"  getListPaddingTop= " +getListView().getListPaddingTop()  +"  getPaddingTop= " +getListView().getPaddingTop() +"  v= " + vie);
            //  Log.v( teg," getY= " + vie.getY() + " getTop= " + vie.getTop()+ " getScrollY= " + vie.getScrollY()+ "  v()= " + vie)
            //    fbButton.setY(-10f + offset  * getPixelsY(64));
            fbButton.animate().translationY(offset).setInterpolator(new LinearInterpolator()).start();

            if(fbButton2 != null)fbButton2.animate().translationY(offset).setInterpolator(new LinearInterpolator()).start();

//            View v = ((ViewGroup)getListView()).getChildAt(0);
//            Log.d( teg," (moveButton) getListView().getBaseline()= " + getListView().getBaseline()
//                   + " getDividerHeight= " + getListView().getDividerHeight()
//                    + " v.getHeight= " +(v == null?"null": v.getHeight()));
//            Log.v( teg," (moveButton) DispleyWidthDp= "+  DispleyWidthDp
//                    + "\n   PaddingLeft="+fbButton.getPaddingLeft() + "   Paddingtop="+fbButton.getPaddingTop()
//                    + "   Left="+fbButton.getLeft() + "   top="+fbButton.getTop()
//            );
//            Log.v( teg," (moveButton) H= "+  getResources().getDisplayMetrics().widthPixels
//                    + "   xdpi="+getResources().getDisplayMetrics().xdpi
//                    +"   density="+getResources().getDisplayMetrics().density
//                    +"   densityDpi="+getResources().getDisplayMetrics().densityDpi
//                    +"   heightPixels="+getResources().getDisplayMetrics().heightPixels
//            );
        }
        /**
         * ListView l -  содержит лист View (View = poplist_item.xml)
         * ------ ДЛЯ НАШЕГО СЛУЧАЯ -----------------
         * LongClick (true)-обрываем распространие события и (без onTouch_DOWN)  Click короткий НЕ сработает
         * длительное нажатие  onTouch_DOWN (false)->  LongClick (true) -> onTouch_MOVE(X) ->  onTouch_UP(true) end
         * коротеое нажатие  onTouch_DOWN (false)->  onTouch_MOVE(X) ->  onTouch_UP(false) -> Click end
         * тестирование Х  У на пределы!
        **/
        // обязательно синхронизируем, чтоб УБРАТЬ глюки
        synchronized boolean controlObject(int click, Object obj, int x){
            x = getDp(x);//сразу переводим в Dp
            int i;View vie = null; String str = "";
            boolean rez = false;// false ДАЕТ распространять события дальше, true- НЕ разрешает?после ONTach-Click, LongClick
            switch(click){
                case CLICK_SHORT: rez = true;//выбор,СОСТОЯНИЕ (удалитение, работа) в состоянии удаления все отменяем, иначе вызов соответсв пункта
                    i = adapter.getPosition(obj);//работаеем только с объектами, их запоминаем, позиции для временвх значение
                    if((objectD == null) || (i != adapter.getPosition(objectD))//если объект для удаления отсутствует или не совпадает позиция текущая и ранее выбранного объекта
                            || ((positionScroll > 0) && (positionStartX < (DispleyWidthDp -maxScroll)))//мы не попали тычком на картинку удалить
                            ||  ((positionScroll < 0) && (positionStartX > maxScroll))) {//мы не попали тычком на картинку редактировать
                        //то вызываем пункт меню!контроль указателя!
                        if(i >= 0) Toast.makeText(getActivity(), "onClick GoTo N= " + i, Toast.LENGTH_SHORT).show();
                        str = "CLICK_SHORT GoTo i= " + i;
                     }else{
                        if((i >= 0) && (objectD != null) && (i == adapter.getPosition(objectD))){
                            if ((positionScroll > 0) && (positionStartX > (DispleyWidthDp -maxScroll))){//удаления
        // УДАЛИТЬ--------------------------------
    clearScrollX_View(true);//затираем на него ссылку? с ним закончили
                                //
                                if((obj instanceof Sensor)){
                                    ((Sensor)obj).close();
                                    Log.w(TAG,"Dell sensor -- close GATT --");
                                }
                                adapter.remove(obj);// УДАЛИТЬ
                                objectD = null;//чтоб сдвижки поом НЕ было с NULL  объектом
                                moveButton();
                                if(i >= 0) Toast.makeText(getActivity(), "onClick Dell N= " + i, Toast.LENGTH_SHORT).show();
                                str = "CLICK_SHORT i= " + i + "  Remove obj= " + obj;
                            } else {
                                if ((positionScroll < 0) && (positionStartX < maxScroll)){//редактирование
                                    //имедж редактировать
  clearScrollX_View(true);//затираем на него ссылку? с ним закончили
                                    str = "CLICK_SHORT i= " + i + "  Edit obj= " + obj;
                                    if(i >= 0) Toast.makeText(getActivity(), "onClick Edit N= " + i, Toast.LENGTH_SHORT).show();
                                }else str = "CLICK_SHORT i= " + i + "  no Work obj= " + obj;
                                //str = "CLICK_SHORT i= " + i + (positionScroll > 0 ? "  Del":"  Edit");
                            }
                        }
                    }
  clearScrollX_View(true);//затираем на него ссылку? с ним закончили
                    break;
                 case CLICK_LONG: rez = true;//закончили обработки здесь, если нажати длинное (блокируем срабатывания короткого)
                     if((objectD != null) && (adapter.getPosition(objectD) >= 0)){
                         //второй раз долго держим? сбрасываем  этот объект в начало
                         clearScrollX_View();//затираем на него ссылку? с ним закончили
                     }
                     if(obj != null){
                        objectD = obj;//запомнили с кем работаем
                        maxScroll = 100;//определить Здесь ширину Зоны срабатывания перед операцией
                         setScrollX_View(initShift);//первичное смещение - показ КРАСНОГО СООБЩЕНИЯ ОБ УДАЛЕНИИ- индикация режима выбора
                        stateDell = true;//установили режим смещения изображения для выбора УДАЛЕНИЧЯ или НАСТРОЙКИ
                    }
                    str = "CLICK_LONG i= " + adapter.getPosition(obj) + "   obj= " + obj;
                    break;//всегда возвращяем false, чтоб не сработала потом короткое CLICK_SHORT
                case CLICK_DOWN://запомнили Х начало смещения!rez = true;//всегда возвращяем false, чтоб сработала потом CLICK_LONG или короткое CLICK_SHORT
                    positionStartX = x;
                    stateDell = false;// на всякий случай
                    str =  "CLICK_DOWN X= " + x;
                    break;
                case CLICK_MOVE://смещение вювера влево или вправо
                    positionScroll = positionStartX - x;
                    i = maxScroll + (maxScroll >> 1);
                    if(Math.abs(positionScroll) > i){//разрешаем сдвигать влево и вправо но НЕ БОЛЕЕ 1.5 МАХ(пол экрана)
                        if(positionScroll < 0) i = -i;//инверсия числа- отрицательно число
                        positionScroll = i;
                    }
                    setScrollX_View(positionScroll);
                    str =  "CLICK_MOVE--- X= " + x + "  positionScroll= " + positionScroll;
                    break;
                case CLICK_UP:
                    if(stateDell == true){//обработка СДВИГА
                        rez = true;//чтоб НЕ сработал короткий КЛИК!
                        if(Math.abs(positionScroll) > (maxScroll >> 1)){//сдвиг сработал, принимаем
                            if(positionScroll < 0) positionScroll = -maxScroll;
                            else positionScroll = maxScroll;
                            setScrollX_View(positionScroll);
                         } else {//сдвиг НЕ сработал, возвращяемся
                            //в состоянии удаления все отменяем
                            clearScrollX_View();//затираем на него ссылку? с ним закончили
                        }
                    } else{// сохраняем для выбора по короткому клику!!
                        positionStartX = x;
                    }
                    stateDell = false;//закончили смещение
                    str =  "CLICK_UP--- X= " + x+ "  positionScroll= " + positionScroll;
                    break;
                case CLICK_ADD:
                    if((objectD != null) && (adapter.getPosition(objectD) >= 0)){
                        //второй раз долго держим? сбрасываем  этот объект в начало
                        clearScrollX_View();//затираем на него ссылку? с ним закончили
                    }
                    if(addNoInitObject()) {//добавить объект
                        moveButton();
                    }
                    if(maxChidren > adapter.getCount()){
                        fbButton.setVisibility(View.VISIBLE);
                    }else{//уже объектов больше чем разрешено//ADD выключить
                        fbButton.setVisibility(View.INVISIBLE);
                    }
                    str = "CLICK_ADD i= " + adapter.getPosition(obj) + "   obj= " + obj;
                    break;
                default:
                    str = "onClickWorks---= img_??" + adapter.getPosition(obj) + "    obj= " + obj;
                    return false;
            }
            Log.v( TAG,str);
            return rez;
        }
    }
    // ListView l -  содержит лист View (View = poplist_item.xml)
    // l.getItemAtPosition(position) - получение объекта данные которого связаны с View
    //-- адаптнер позволяет по индексу находить Этот обект? свойства которого отображаем
    // по по самому объекту найти и отдать его индекс в массиве, пример ниже
    // adapter = (ArrayAdapter) getListAdapter();
    // ind = adapter.getPosition(l.getItemAtPosition(position));
    //--------------------Это для старой реализации --
    public String getItem(ListView l, int position){
        //final ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
        if(l == null) return "?";
        Object obj = adapter.getItem(position);//получение объекта данные которого связаны с View
        if(l == obj) return "??";
        if(obj instanceof Cheeses)System.out.println("position=" +position + "=" + obj+"  " +((Cheeses)obj).getObject());
     //   if(obj instanceof Sensor)System.out.println("position=" +position + "=" + obj+"  " +((Sensor)obj).getObject());
        if(l.getItemAtPosition(position) instanceof CharSequence) return (String) l.getItemAtPosition(position);
        else return l.getItemAtPosition(position).toString();
    }
    @Override   // Короткий клик- выбор пункта!!-- на исполнение? вызов следующегго ока
    public void onListItemClick(ListView listView, View v, int position, long id) {
        //Log.d(teg,"   getMeasuredHeight= " + v.getMeasuredHeight() +"   getHeight="+ v.getHeight() +"   gety= "+v.getY());
        objectDataToView.controlObject(CLICK_SHORT,adapter.getItem(position),0);
    }
    //View v- это конкретный вювер!!! с которым можно работать!!
    @Override
    //synchronized Здесь фиксируем РЕЖИМ на удаление. если до конца доведем и нажмем на него то ИТЕМ удалится!!
    public boolean onListItemLongClick(ListView l, View v, int position, long id) {
       //если НЕ разрешено редактирование!! выходим!
        if(dellItem == false) return false;
            return objectDataToView.controlObject(CLICK_LONG,adapter.getItem(position),0);
    }
    //дает данные по всему списку Х и У а также кнопе=ка нажата отжата и тд
    //View v - ЭТО на всех. тоесть группа -ViewGroup!!

    /**
     * http://android-helper.com.ua/events/
     * О том, что возвращают обработчики MotionEvent’ов
     * обработчики MotionEvent’ов должны возвращать boolean’овское значение.
     * В примерах до этого мы просто всегда возвращали true.Так для чего же этим методам вообще нужно возвращать значение?
     * Дело в том, что у одного View может быть несколько слушателей (например onTouchEvent в самом View и OnTouch у
     * внешнего слушателя) и они имеют некоторый приоритет: OnTouch вызывается первым (если он вообще имеется),
     * а уже после него может вызываться onTouchEvent.
     * Вызов следующего по приоритету обработчика как раз зависит от возвращаемого текущим обработчиком значения
     * (true — обрыв дальнейшей обработки, false — вызывается обработчик следующий по приоритету, если таковой имеется).
     * Таким образом Android позволяет нам разделять обязанности по обработке touch event’ов на несколько слушателей.
     * последовательность срабатывания  onTouch_DOWN (false)->  LongClick (false) -> onTouch_MOVE ->  onTouch_UP(false) -> Click end
     * последовательность срабатывания  onTouch_DOWN (true)-> LongClick (false) -> onTouch_MOVE ->  onTouch_UP(true) end
     * последовательность срабатывания  onTouch_DOWN (false)->  LongClick (true) -> onTouch_MOVE ->  onTouch_UP(X) end
     * последовательность срабатывания  onTouch_DOWN (true)-> onTouch_MOVE(X) ->  onTouch_UP(X) end
     * ------ ДЛЯ НАШЕГО СЛУЧАЯ -----------------
     * LongClick (true)-обрываем распространие события и (без onTouch_DOWN)  Click короткий НЕ сработает
     * длительное нажатие  onTouch_DOWN (false)->  LongClick (true) -> onTouch_MOVE(X) ->  onTouch_UP(true) end
     * коротеое нажатие  onTouch_DOWN (false)->  onTouch_MOVE(X) ->  onTouch_UP(false) -> Click end
     * @param v
     * @param event
     * @return
     */
    @Override
    //synchronized
    public boolean onTouchA(View v, MotionEvent event) {
////        System.out.println("oonTouchA:   X=" + event.getX() + "  event.getAction()="+event.getAction()
        if (event.getAction() == event.ACTION_MOVE){
            return objectDataToView.controlObject(CLICK_MOVE,null,(int) event.getX());
        }
        if (event.getAction()== event.ACTION_UP){
            return objectDataToView.controlObject(CLICK_UP,null,(int) event.getX());

        }
        if (event.getAction()==event.ACTION_DOWN){
            objectDataToView.controlObject(CLICK_DOWN,null,(int) event.getX());
            return false;
        }
        return false;
    }
    /**
     *
     * @param id
     * @param v
     */
    private void setVisibility(int id,View v){
        v.findViewById(id).setVisibility(View.VISIBLE);
    }
    /**
     *
     * @param id
     * @param v
     */
    private void setInVisibility(int id,View v){
        v.findViewById(id).setVisibility(View.INVISIBLE);
    }
    // private static int n=0;
    /**
     * A simple array adapter that creates a list of cheeses.
     *  похоже здесь ДОБАВЛЯТЬ сообщения на вювер!! дополнительные значения! когда он формруется-создается
     *  !! ПРИКАЖДОМ Добавлении или УДАЛЕНИИ объекта ЭТО вызываетСЯ!!
     *  в ArrayAdapter произодится присваивания текста (createViewFromResource). а потом вызывает этот метод- ТОЕСТЬ все можно переопределить!
     *  ---
     *  position - позиция View (делаеься из convertView) КОТОРАЯ формируется и будет записана в ViewGroup container
     *  при СТАРТЕ приложения convertView = null, тоесть в этот метод отдается возможность сформировать его полностью
     *  ArrayAdapter adapter - после первого раза, там длинна хранящихся объектов!
     *  ТАКИМ образом при запуске приложения convertView = null
     *  сдедующие вызовы ТОЛЬКО при удалении и добавлении объектов!!
     *  НАВЕРНО работает так- если получает назад null? то начинает строить сам!! визуальный объект!
     */
   // private static float  rr= 1;

    class PopupAdapter extends ArrayAdapter<Object> {
        PopupAdapter(ArrayList<Object> items) {
            //super(getActivity(), R.layout.list_item, android.R.id.text1, items);
            super(getActivity(), R.layout.poplist_item, android.R.id.text1, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // Let ArrayAdapter inflate the layout and set the text

            View view = super.getView(position, convertView, container);
            //-----------------------------------
            //при изменениях, всегда сбрасываем РЕДАКТИРОВАНИЕ!!и убираем ИКОНКУ редактирования!!!
            if(dellItem == true)dellItem = false;
            ViewGroup vg = (ViewGroup)view;
            //гасим рАЗРЕШЕНИЕ редактирования, ЭТО 2 слой!!
            if(vg != null){//Видимые и не видимые символы РЕДАКТИРОВАНИЯ
                View v  = vg.getChildAt(2);
                if(v != null)v.setVisibility(View.GONE);
                //
     //           v  = vg.getChildAt(1);
     //           v.setBackgroundColor(0x808080);
            }
            //-------------------------------------------------
            if((position + 1) >= adapter.getCount())objectDataToView.moveButton();//позиционируем
            //сдвигать можнл, но вехний перекрывает нижний итем
       //     if(convertView != null )convertView.setScrollY((int)(rr *20f));
       //     rr = rr *(-1f);

            Log.d(TAG,"position"+position+"  view"+ view+"  convertView=" + convertView +"  container= " +container);
            if(adapter.getItem(position) != null){
              //  ((Cheeses)adapter.getItem(position)).textValue = view.findViewById(R.id.numbe_cur);
                //берем объект


                //присвоил текущее значение для отображения
     ((Sensor)adapter.getItem(position)).intermediateValueView = view.findViewById(R.id.numbe_cur);
     ((Sensor)adapter.getItem(position)).rssiView = view.findViewById(R.id.signal);
      ((Sensor)adapter.getItem(position)).deviceLabelView = view.findViewById(R.id.imgTitle);
//                TreeMap<Integer,Object> map = ((Cheeses)adapter.getItem(position)).value;
//
//                //загоняем в мап что есть на Вюверах, таким образм, потом сравнивая что есть, то и выводим!
//                map.put(R.id.numbe_min,view.findViewById(R.id.numbe_min));
//                map.put(R.id.numbe_min, view.findViewById(R.id.numbe_min));
//                map.put(R.id.numbe_cur, view.findViewById(R.id.numbe_cur));
//                map.put(R.id.numbe_max, view.findViewById(R.id.numbe_max));
//                map.put(R.id.imgTitle, view.findViewById(R.id.imgTitle));
//                map.put(R.id.signal, view.findViewById(R.id.signal));
//                map.put(R.id.battery, view.findViewById(R.id.battery));
                //       map.put(R.id.text1, view.findViewById(R.id.text1));
            }
//            getActivity().runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    ;
//                }
//            });

            //--------Устанвливаем КОРОТКИЙ клик на View vie - потом и делаем его НЕВИДИМЫМ, чтоб НЕ реагировал
            //это пункт Удаление Объекта? данные которого представлены во вювере!
    //        view.findViewById(R.id.img_edit).setVisibility(View.INVISIBLE);
  ;

  //          setonClick_edit_dell_goTo(view.findViewById(R.id.img_dell), position);
            //группавое удаление обекта? при нажатии он выдвигается, можно нажать на все, потом всех по очереди удалить
  //          setonClick_edit_dell_goTo(view.findViewById(R.id.img_edit), position);
            //переход на настройки объекта
  //          setonClick_edit_dell_goTo(view.findViewById(R.id.img_goto), position);
            //
            //  TextView vw = (TextView) view.findViewById(R.id.textViewName);
//
//            System.out.println("0 container.getChildCount() =" + container.getChildCount()  +"  /"+position +
//            "   adapter.getCount()=" +adapter.getCount() + "  inst=" + (convertView instanceof ViewGroup)
//            + "  .."+ convertView);//+"    getChildCount()" +((ViewGroup) convertView).getChildCount()
            return view;
        }
    }
    /**
     * переход на настройки объекта
     * @param v
     * @param obj
     */
//    public void onClickWorks(View v, Object obj){
//        switch (v.getId()){
//            case img_edit:
//                System.out.println( "img_edit---= " + adapter.getPosition(obj) + "    obj= " + obj);
//                break;
//            case img_dell:
//                System.out.println( "img_dell---= " + adapter.getPosition(obj) + "    obj= " + obj);
//                break;
//            case img_goto:
//                System.out.println( "img_goto---= " + adapter.getPosition(obj) + "    obj= " + obj);
//                break;
//            default:
//                System.out.println( "onClickWorks---= img_??" + adapter.getPosition(obj) + "    obj= " + obj);
//        }
////    }
//
//    /**
//     * --------Устанвливаем КОРОТКИЙ клик на View vie - потом и делаем его НЕВИДИМЫМ, чтоб НЕ реагировал
//     * на выбор КОНТЕЙЕНЕРА, видимость- только после ДОЛГОГО НАЖАТИЯ - он становится видимым и реагирует на кликк
//     * на выход передает ОБЪЕКТ данные которого и отображаются!!
//     * @param vie-вювер по которому кликнули
//     * @param position- объект с которым он связа- отображает его состояние
//     */
//    private void setonClick_edit_dell_goTo(View vie, int position){
//        //входной контроль здесь
//        // контроль по установке слушателя, если он уже был установлен
//        if(vie == null) {
//            System.out.println("PopupListFragment.setonClick_edit_dell_goTo() ERROR: NO-findViewById");
//            return;
//        }
//        if(vie.hasOnClickListeners() || (position < 0) || (position >= adapter.getCount())) return;
//        //
//        final Object objectA =  adapter.getItem(position);
//        if(objectA == null) return;
//
//        //установим невидимое состояние, при этом он НЕ будет реагировать на клики мышкой!!
//      //  vie.setVisibility(View.INVISIBLE);
//        vie.setVisibility(View.VISIBLE);
//        vie.setOnClickListener(new OnClickListener() {
//            //
//            private final Object obj = objectA;
//            @Override
//            public void onClick(View v) {
//                //onClickWorks(v,obj);
//                objectDataToView.controlObject(v.getId(),obj,0);
//            }
//        });
//        System.out.println("setonClick_edit_dell OK pos=" + adapter.getPosition(objectA));
//    }
//    //ничего не срабатывает
//    @Override
//    public boolean onDragA(View v, DragEvent event){
//        Toast.makeText(getActivity(), "onDragA: " + event, Toast.LENGTH_SHORT).show();
//        return true;
//    }
    //    //запуск дополнительного меню? убрать
//    @Override
//    public void onClick(final View viewScroll) {
//        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
//        // correctly positioned. The reason being that the viewScroll may change position before the
//        // PopupMenu is shown.
//        //тыкаемся на рисунок
//        viewScroll.post(new Runnable() {
//            @Override
//            public void run() {
//                showPopupMenu(viewScroll);
//            }
//        });
//    }
//    // BEGIN_INCLUDE(show_popup) Если ткунились в картинку- отображение меню-
//    private void showPopupMenu(View viewScroll) {
//  //      viewScroll.setLongClickable(true);
////        viewScroll.setOnLongClickListener(new Runnable() {
////            @Override
////            public void run() {
////                ;//Toast.makeText(getActivity(), "setOnLongClickListener ", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        final PopupAdapter adapter = (PopupAdapter) getListAdapter();
//
//        // Retrieve the clicked item from viewScroll's tag
//        final String item = (String) viewScroll.getTag();
//
//        Toast.makeText(getActivity(), "Item Clicked: " + item, Toast.LENGTH_SHORT).show();
//
//        // Create a PopupMenu, giving it the clicked viewScroll for an anchor
//        PopupMenu popup = new PopupMenu(getActivity(), viewScroll);
//
//        // Inflate our menu resource into the PopupMenu's Menu
//        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
//
//        // Set a listener so we are notified if a menu item is clicked
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.menu_remove:
//                        // Remove the item from the adapter
//                        adapter.remove(item);
//                        return true;
//                }
//                return false;
//            }
//        });
//
//        // Finally show the PopupMenu
//        popup.show();
//    }
//    // END_INCLUDE(show_popup)
}
