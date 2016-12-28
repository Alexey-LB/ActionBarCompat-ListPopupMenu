package com.example.android.actionbarcompat.listpopupmenu;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.portfolio.alexey.connector.BluetoothLeServiceNew;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import java.security.acl.Group;

import static java.lang.Thread.sleep;

/**
 * Created by lesa on 27.12.2016.
 */

public class MainActivityThermometer  extends AppCompatActivity {// ActionBarActivity {
    public  final static String TAG = "MA.Therm";
    private Menu menuFragment;
    //    public  final static String EXTRAS_DEVICE_NAME = "EXTRAS_DEVICE_NAME";
//    public  final static String EXTRAS_DEVICE_NAME_FILTR = "EXTRAS_DEVICE_NAME_FILTR";
//    public  final static String EXTRAS_DEVICE_ADDRESS = "EXTRAS_DEVICE_ADDRESS";
//    public  final static String EXTRAS_DEVICE_ITEM = "EXTRAS_DEVICE_ITEM";
//
// //ОБЯЗАТЕЛЬНО !! ввести в практику передачу тила для порожденного окна!!
//    public  final static String EXTRA_BAR_TITLE = "EXTRA_BAR_TITLE";
//
    public  final static int MAIN_ACTIVITY_THERMOMETER = 0xFFFF1235;

    private View thermometer;
    private int itemSensor = 0;
    private Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_thermometer);
        //-------------------------------------------------------
        final Intent intent = getIntent();
        itemSensor = getIntent().getIntExtra(MainActivityWork.EXTRAS_DEVICE_ITEM, itemSensor);
        RunDataHub app = ((RunDataHub) getApplicationContext());
        //--------ПРИМЕМ ЕСЛИ сервис не запущен и нет доступа к сенсорам, выходим!--
        if((app == null) || (app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.mbleDot == null)
                || (app.mBluetoothLeServiceM.mbleDot.get(itemSensor) == null)){

            Log.e(TAG,"ERR = ((app == null) || (app.mBluetoothLeServiceM == null)" +
                    "                || (app.mBluetoothLeServiceM.mbleDot == null) || (sensor == null)");
            finish();
        }//
        sensor = app.mBluetoothLeServiceM.mbleDot.get(itemSensor);
        //------------------------------
        //настраиваем и включаем тулбар

        Util.setSupportV7appActionBar(getSupportActionBar(),TAG,
                intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //убираем системный бар
        thermometer = findViewById(R.id.LayoutMainThermometer);
        thermometer.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        thermometer.findViewById(R.id.reset_min_max).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //сброс минимум и максимум
                if(sensor != null) sensor.resetMinMaxValueTemperature();
            }
        });
        setThermometerView();
        Log.e(TAG, "----onCreate END-----");
    }
    private void setThermometerView(){
      //  thermometer_column
        ImageView fon, column;
        fon = (ImageView)thermometer.findViewById(R.id.thermometer_fon);
        column = (ImageView)thermometer.findViewById(R.id.thermometer_column);
        Drawable d = new Thermometer();

        fon.setBackground(d);

    }
//вызывается при смене ориентации экрана, необходимо указать разрешений в МАНИФЕСТЕ
    //перебрасывает минимум и максимум в другой слой и все
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //-- если горизонтально расположен ---
        ViewGroup horizontal, vertical;View v,v2;
        horizontal = (ViewGroup)thermometer.findViewById(R.id.telephon_horizontal);
        vertical = (ViewGroup)thermometer.findViewById(R.id.telephon_vertical);

        switch (newConfig.orientation){
            case  Configuration.ORIENTATION_PORTRAIT://вкртикально
                if(horizontal.getChildCount() > 0){//были в горизонтальном положении
                    v = horizontal.getChildAt(0);
                    v2 = horizontal.getChildAt(1);
                    horizontal.removeAllViews();
                    vertical.addView(v,0);
                    vertical.addView(v2);
                    horizontal.setVisibility(View.GONE);
                }
                break;
            case  Configuration.ORIENTATION_LANDSCAPE:
                if(horizontal.getChildCount() == 0){
                    v = vertical.getChildAt(0);
                    v2 = vertical.getChildAt(2);
                    vertical.removeViewAt(2);
                    vertical.removeViewAt(0);
                    horizontal.addView(v);
                    horizontal.addView(v2);
                    horizontal.setVisibility(View.VISIBLE);
                }
                break;
        }
        Log.w(TAG," Orientation==== " +newConfig.orientation + "   "+ Configuration.ORIENTATION_LANDSCAPE);
    }

    final int iconActionSetting = 234567896;
    public boolean onCreateOptionsMenu(Menu menu){
        menuFragment = menu;//запомил, чтоб потом  изменить меню или удалить ПРИ ВЫХОДЕ из фрейма

        menu.add(Menu.NONE,iconActionSetting,Menu.NONE,"Setting")
                .setIcon(R.drawable.ic_settings_blue_32dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        // menu.clear();
        //       MenuInflater menuInflater= getMenuInflater();
        //       menuInflater.inflate(R.menu.poplist_menu,menu);
        // inflater.inflate(R.menu.myfragment_options, menu);
        //пока отключил редактирование НЕ к чему, ДА программно ПОРОЖДАЯ- встает в нужном месте
//        menu.add(Menu.NONE,iconActionEdit,Menu.NONE,"Edit")
//                .setIcon(R.drawable.ic_clear_black_24dp)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    synchronized private void updateViewItem(Sensor sensor, View view){
        if((sensor == null) || (view == null)) return;
        String str;int bl,rssi;
        boolean b = (sensor.mConnectionState == BluetoothLeServiceNew.STATE_CONNECTED);
        //     || (sensor.mBluetoothDeviceAddress == null);//режим ИММИТАЦИИ- отладки
        //
        // по умолчанию из метода toString -> заталкивается в R.id.text1, по этому мы сами это НЕ делаем
        Util.setDrawableToImageView(sensor.markerColor,R.id.marker, view);

        Util.setTextToTextView(sensor.getStringTime(),R.id.time, view);
        Util.setTextToTextView(sensor.getString_1_ValueTemperature(true),R.id.numbe_min, view);
        Util.setTextToTextView(b? sensor.getString_2_ValueTemperature(true):"Нет подкл."
                , R.id.numbe_cur, view);
        Util.setTextToTextView(sensor.getString_3_ValueTemperature(true),R.id.numbe_max, view);

        Util.setLevelToImageView(b? sensor.battery_level: 0, R.id.battery, view);
        Util.setLevelToImageView(sensor.rssi, R.id.signal, view);
     }
    private boolean mHandlerWork = true;
    private Handler mHandler = new Handler();
    @Override
    public  void onPause() {
        super.onPause();
        mHandlerWork = false;
    }
    @Override
    public  void onResume() {
        super.onResume();
        //при возвращениие из других окон, может быть системный бар, по этому еще раз его отменяем
        thermometer.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //---
        mHandlerWork = true;
        //сам заводится и работает
        mHandler.postDelayed(new Runnable() {
            public void run() {
                // Log.v(TAG,"mHandler --");
                updateViewItem(sensor, thermometer);
                // повторяем через каждые 300 миллисекунд
                if(mHandlerWork) mHandler.postDelayed(this, 300);
            }
        },500);
        Log.e(TAG, "----onResume() ----------");
    }

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(menuFragment != null)menuFragment.clear();
        if (isFinishing()) {
            Log.e(TAG,"onDestroy() ==============isFinishing() =========== isFinishing() ======");
        } else {
            Log.e(TAG,"onDestroy() -----------WORK ------------- WORK -------------");
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
                finish();
                return true;
            case iconActionSetting:
                Log.i(TAG,"iconActionSetting-");
                  //-------Setting --
                  final Intent intent = new Intent(this, MainSettingSetting.class);
                  intent.putExtra(MainActivityWork.EXTRAS_DEVICE_ITEM, itemSensor);
                  intent.putExtra(Util.EXTRAS_BAR_TITLE, "  B4/B5");
                  startActivity(intent);//
               // startActivityForResult(intent,MainActivityThermometer.MAIN_ACTIVITY_THERMOMETER);//
                return true;
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }
}
