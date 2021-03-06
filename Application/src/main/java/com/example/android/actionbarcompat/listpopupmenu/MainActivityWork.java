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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.BluetoothLeServiceNew;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import java.util.ArrayList;

//import android.app.FragmentManager;
//import android.app.Fragment;

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
public class MainActivityWork extends AppCompatActivity {// ActionBarActivity {
    public  final static String TAG = "MAINwork";
    public  final static String EXTRAS_DEVICE_NAME = "EXTRAS_DEVICE_NAME";
    public  final static String EXTRAS_DEVICE_NAME_FILTR = "EXTRAS_DEVICE_NAME_FILTR";
    public  final static String EXTRAS_DEVICE_ADDRESS = "EXTRAS_DEVICE_ADDRESS";
    public  final static String EXTRAS_DEVICE_ITEM = "EXTRAS_DEVICE_ITEM";

 //ОБЯЗАТЕЛЬНО !! ввести в практику передачу тила для порожденного окна!!
    public  final static String EXTRA_BAR_TITLE = "EXTRA_BAR_TITLE";

    public  final static int MAINACTIVITY = 0xFFFF0011;
    public final  static int ACTIVITY_SETTING_SETTING = 0xFFFF0022;
    public final  static int ACTIVITY_SETTING_MAKER = 0xFFFF0044;
    public final  static int ACTIVITY_FIND_DEVICE = 0xFFFF0055;

    private  final   int mainIdFragmentWork = R.id.mainFragmentWork;

    public PopupListFragment popupListFragment;
    RunDataHub app;
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
    private boolean onStartApp = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullscreen(this);// работает отлично! один раз объевил, работает пока окно не умрет!
        setContentView(R.layout.activity_main_work);//
        final Intent intent = getIntent();
        //--------ПРИМЕМ ЕСЛИ сервис не запущен и нет доступа к сенсорам, выходим!--
        app = ((RunDataHub) getApplicationContext());
        if((app == null) || (app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)){

            Log.e(TAG,"ERR = ((app == null) || (app.mBluetoothLeServiceM == null)" +
                    "                || (app.mBluetoothLeServiceM.arraySensors == null))");
            finish();
        }//
        app.mainActivityWork = this;


         //------------------------------
//!!??  УБРАЛ в РЕЗЮМЕ и удаление в паузе-, ИНАЧЕ В ФОНЕ РАБОТАЛО!! Util.changeFragment(mainIdFragmentWork, new PopupListFragment(), getSupportFragmentManager());
        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
        //настраиваем и включаем тулбар
        Util.setSupportV7appActionBar(getSupportActionBar(),TAG,
                intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //устанавливаем еденицы измерения
        if(app.mBluetoothLeServiceM.arraySensors.size() > 0){
            //для всех еденицы устанавливаются одинакова, по этому берем первый попавшийся
            setOnFahrenheit(app.mBluetoothLeServiceM.arraySensors.get(0).onFahrenheit);
        } else{//по умолчанию ставим целсия
            setOnFahrenheit(false);
        }
  //выключил нижний бар переключения ЦЕЛСИЙ/фаренгейт
  findViewById(R.id.FrameLayoutFahrenheit).setVisibility(View.GONE);
   //     updateView();
        //-------------КНОПКИ УСТАНОВКИ ЕДЕНИЦ ИЗМЕРЕНИЯ --
        findViewById(R.id.textViewC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //установка измерения в ЦЕЛЬСИЯХ
                setOnFahrenheit(false);
            }
        });
        findViewById(R.id.textViewF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //установка измерения в ФАРЕНГЕЙТАХ
                setOnFahrenheit(true);
            }
        });
        Log.e(TAG, "----onCreate END-----");
    }
//    private void updateView(){
//        //
//        RunDataHub app = ((RunDataHub) getApplicationContext());
//        //а первый запуск показываем заставку несколько секунд, там и потом убираем системный бар
//        // в случа нуля и если мы не первый раз уже просыпаемся то тогда надо убират
//        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
//        if((app == null) || (app.getStartApp() == true)){
//            //прячем наш бар на время
//            getSupportActionBar().hide();
//            // frameHeadband - ЭТОТ слой сверху, ПО ЭТОМУ он загрывает все что снизу слои
//            // переключение едениц измерения- не гасим, он закрыт слоем ЗАСТАВКИ релсиба
//            findViewById(R.id.frameHeadband).setVisibility(View.VISIBLE);
//            //ВЫБОР едениц изображения-БАР внизу, только сворачиваю изображение
////            findViewById(R.id.LinearLayoutFahrenheit).setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.frameHeadband).setVisibility(View.GONE);
//            //запускаем подключение если еше не ставили его
//            if(!onFrameHeadband){
//                onFrameHeadband = true;
//                Util.changeFragment(mainIdFragmentWork, new PopupListFragment()
//                        , getSupportFragmentManager());
//            }
//
//
//        }
//        //устанавливаем еденицы измерения
//        if((app != null) && (app.mBluetoothLeServiceM != null)
//        && (app.mBluetoothLeServiceM.arraySensors != null)
//                && (app.mBluetoothLeServiceM.arraySensors.size() > 0)){
//            //для всех еденицы устанавливаются одинакова, по этому берем первый попавшийся
//            setOnFahrenheit(app.mBluetoothLeServiceM.arraySensors.get(0).onFahrenheit);
//        } else{//по умолчанию ставим целсия
//            setOnFahrenheit(false);
//        }
//    }
    private void setOnFahrenheit(boolean fahrenheit){
        TextView viewC = (TextView)findViewById(R.id.textViewC);
        TextView viewF = (TextView)findViewById(R.id.textViewF);
        if(fahrenheit){
            viewC.setBackgroundResource(R.drawable.rectangle_line_corners_left_5dp);
            viewF.setBackgroundResource(R.drawable.rectangle_corners_right_5dp);
            viewC.setTextColor(getResources().getColor(R.color.color_blue_light));
            //поменяли цвета текста на кнопках между собой
            viewF.setTextColor(getResources().getColor(R.color.colorBackground));
           //viewF.setTextColor(ContextCompat.getColor(context, R.color.color_blue_light));
        }else{
            viewC.setBackgroundResource(R.drawable.rectangle_corners_left_5dp);
            viewF.setBackgroundResource(R.drawable.rectangle_line_corners_right_5dp);
            //поменяли цвета текста на кнопках между собой
            viewC.setTextColor(getResources().getColor(R.color.colorBackground));
            viewF.setTextColor(getResources().getColor(R.color.color_blue_light));
        }
        //
        RunDataHub app = ((RunDataHub) getApplicationContext());
        //если масива сенсоров нет, то и устанавливать значений некому!
        if((app == null) || (app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)
                || (app.mBluetoothLeServiceM.arraySensors.size() == 0)) return;
        //устанавливаем еденицы измерения ДЛЯ ВСЕХ одинаково!
        ArrayList <Sensor> als = app.mBluetoothLeServiceM.arraySensors;
        for(Sensor sensor: als){
            if(sensor.onFahrenheit != fahrenheit){
                sensor.onFahrenheit = fahrenheit ;
                sensor.changeConfig = true;//установили изменеие сонфигурации ДЛЯ сохранения во ФЛЕШИ телефона
            }
        }
    }
//    //инициализация фрейма
//    public void init(){
//        Log.e(TAG, "----init() ----------");
//        RunDataHub app = ((RunDataHub) getApplicationContext());
//        if((app != null) && (app.mBluetoothLeServiceM != null)){
//            if(app.mBluetoothLeServiceM.initialize()) {
//                Log.e(TAG, "----init() ---------- OK OK");
//
//            } else{
//                Log.e(TAG, "----init() ---------- ERROR!");
//            }
//            if(app.mBluetoothLeServiceM.arraySensors.size() > 0){
//                //ПРИ первом пуске установили еденицы измерения
//                setOnFahrenheit(app.mBluetoothLeServiceM.arraySensors.get(0).onFahrenheit);
//            }
//        }
// //!!       popupListFragment.initList();
//    }
// Device scan callback.
//private BluetoothAdapter.LeScanCallback mLeScanCallback =
//        new BluetoothAdapter.LeScanCallback() {
//
//            @Override
//            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                final int mrssi = rssi;
//                final byte[] mscanRecord = scanRecord;
//                Log.v("NAIN", "Rssi= " + mrssi + "   scanRecord= " + mscanRecord);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.w(TAG,"FIND ---------- device= " + device);
//                        BluetoothLeServiceNew bs = Util.getAppBleService();
//                        if(bs.mBluetoothAdapter != null){
//                            Sensor sensor = bs.getBluetoothDevice(device.getAddress());
//                            if(sensor != null) bs.connect(sensor.getAddress(),true);
//                        }
//
//                    }
//                });
//            }
//        };
    @Override
    protected void onResume() {
        super.onResume();
        Util.changeFragment(mainIdFragmentWork, new PopupListFragment(), getSupportFragmentManager());

        Log.e(TAG, "----onResume() ----------");

        //если блутуз не существует то и включать нечего!
        if(!app.mBluetoothLeServiceM.isBluetoothAdapterExist()) return;//выходим на запрос ВКЛ блутуза 2 раза!!
        //вызываем окно включения блутуз модуля
        if (!app.mBluetoothLeServiceM.mBluetoothAdapter.isEnabled()) {

            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public static final int REQUEST_ENABLE_BT = 15;
    @Override
    public  void onPause() {
        super.onPause();
        //УДАЛЕНИЕ фрагмента , используем библиотеку потдержки старых устройств (4.4)
        Util.removeFragment(getSupportFragmentManager());
    }

//Develop API Guides User Interface Меню
    // https://developer.android.com/guide/topics/ui/menus.html#context-menu
final int iconActionEdit = 12345678;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
 //       MenuInflater menuInflater= getMenuInflater();
 //       menuInflater.inflate(R.menu.poplist_menu,menu);
        // inflater.inflate(R.menu.myfragment_options, menu);
       //пока отключил редактирование НЕ к чему, ДА программно ПОРОЖДАЯ- встает в нужном месте
//        menu.add(Menu.NONE,iconActionEdit,Menu.NONE,"Edit")
//                .setIcon(R.drawable.ic_clear_black_24dp)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
 //       menu.add(Menu.NONE,iconActionEdit,Menu.NONE,"Ed").setIcon(R.drawable.rectangle_line_corners_left_5dp);
        return super.onCreateOptionsMenu(menu);
    }
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        Log.v(TAG,"onActivityResult requestCode= " + requestCode + "   resultCode= " +resultCode);
//        if (requestCode == MAINACTIVITY && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
        if (requestCode == MAINACTIVITY && resultCode == Activity.RESULT_OK) {
            Log.w(TAG,"NAME= " + data.getStringExtra(EXTRAS_DEVICE_NAME)
            +"   EXTRAS_DEVICE_ADDRESS= " + data.getStringExtra(EXTRAS_DEVICE_ADDRESS));
            //запуск на коннект!!
            RunDataHub app = ((RunDataHub) getApplicationContext());
            if(app.mBluetoothLeServiceM != null){
                app.mBluetoothLeServiceM.connect(data.getStringExtra(EXTRAS_DEVICE_ADDRESS),true);
            }
//!!            if(mBluetoothLeServiceM != null){
//!!                mBluetoothLeServiceM.connect(data.getStringExtra(EXTRAS_DEVICE_ADDRESS),true);
//!!            }
            return;
        }
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.bluetooth_adapter_is_turned_off), Toast.LENGTH_LONG).show();
            Log.e(TAG, getString(R.string.bluetooth_adapter_is_turned_off));
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onScanDevice(int i){
        final Intent intent = new Intent(this, DeviceScanActivity.class);
          intent.putExtra(MainActivityWork.EXTRAS_DEVICE_ITEM, i);
        //  intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        Log.i(TAG,"startActivity SCAN");
        startActivityForResult(intent,MAINACTIVITY);//на подклшючение к устройству
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG,"Menu-edit_a  item= " +item );

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG,"android.R.id.home--");
                //сохранение в файл
                RunDataHub app = ((RunDataHub) getApplicationContext());
                if(app.mBluetoothLeServiceM != null){
                    //записываем УСТАНОВКИ если есть изменения в них
                    app.mBluetoothLeServiceM.testChangesAndSettingPutFile();//app.mBluetoothLeServiceM.settingPutFile();
                    Log.v(TAG,"android.R.id.home--  settingPutFile()--");
                }
    // Добавить вызов ЗАСТАВКИ!!релсиба
                finish();

           return false;//установили ФАЛШ, чтоб вызов попал в ФРАГМЕНТ, в которм будет обработан!!
            case iconActionEdit:
                Log.i(TAG,"edit-");
//        //вызов активного окна для сканирования
//             //   onScanDevice(1);
//                //-------Setting --
//                final Intent intent = new Intent(this, MainSettingSetting.class);
//                intent.putExtra(MainActivity.EXTRAS_DEVICE_ITEM, 0);
//
//                startActivityForResult(intent,MAINACTIVITY);//

                return true;
            case R.id.edit_a://.new_game_:
                View v =((View)findViewById(R.id.textViewName));
//                FragmentManager fm= getSupportFragmentManager();
//
//                Fragment f =  fm.findFragmentById(R.layout.list_item);
//
//                LayoutInflater lf = getLayoutInflater();
//                Resources r =getResources();

                System.out.println("Menu-edit_a  v=" + v);
                ;

                return true;
            case R.id.add_a://
                //взаимодействие АКТИВНОсТИ и фрагмента, вызов явно метода из фрагмента, по ссылке на него!
                //там же взаимодействи обратное, работа с АкшионБар и КНОПКА НАЗАД!
                // http://developer.alexanderklimov.ru/android/theory/fragments.php
                popupListFragment.addNoInitObject();

                android.app.ActionBar  ab = getActionBar();
                System.out.println("Menu-add_a  ab=" + getActionBar());
                ;return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            Log.e(TAG,"onDestroy() ==============isFinishing() =========== isFinishing() ======");
 //!!           mBluetoothLeServiceM = null;
        } else {
            Log.e(TAG,"onDestroy() -----------WORK ------------- WORK -------------");
           ; //It's an orientation change.
        }
    }
}
