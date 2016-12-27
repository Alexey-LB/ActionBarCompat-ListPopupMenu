package com.example.android.actionbarcompat.listpopupmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.portfolio.alexey.connector.Util;

import static java.lang.Thread.sleep;

/**
 * Created by lesa on 27.12.2016.
 */

public class MainActivityThermometer  extends AppCompatActivity {// ActionBarActivity {
    public  final static String TAG = "MA.Therm";
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_thermometer);
        //-------------------------------------------------------
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if(app == null)finish();
        //------------------------------
        //настраиваем и включаем тулбар
        final Intent intent = getIntent();
        Util.setSupportV7appActionBar(getSupportActionBar(),TAG,
                intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //убираем системный бар
        thermometer = findViewById(R.id.LayoutMainThermometer);
        thermometer.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        Log.e(TAG, "----onCreate END-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //при возвращениие из других окон, может быть системный бар, по этому еще раз его отменяем
        thermometer.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        Log.e(TAG, "----onResume() ----------");
    }

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
