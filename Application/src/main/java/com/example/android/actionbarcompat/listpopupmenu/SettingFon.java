package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.portfolio.alexey.connector.InputBox;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;


public class SettingFon extends Activity implements View.OnClickListener{
    //private  int mItem= 0;
    final   String TAG = getClass().getSimpleName();
    final int ACTIVITY_SETTING_COLOR = 67890;
    final int ACTIVITY_SETTING_IMG = 67891;
    final int ACTIVITY_SETTING_СOLLECTION = 67892;
    final int ACTIVITY_SETTING_PHOTO = 67893;

    public  final static String EXTRAS_FLOAT_VALUE = "EXTRAS_FLOAT_VALUE";

    private  int mItem= 0;
    private Sensor sensor;
    //private float fl=0;
    private boolean mHandlerWork = true;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_fon);
        //--------------------------
        final Intent intent = getIntent();
       // Log.w(TAG, "getParentActivityIntent()= " +getParentActivityIntent());
        mItem = intent.getIntExtra(Util.EXTRAS_ITEM,0);
        String title = intent.getStringExtra(Util.EXTRAS_BAR_TITLE);
        // все изменения будет писать сразу в сенсор
        //
        sensor = Util.getSensor(mItem,this);
        if(sensor == null) finish();

        //-------------------------------------------
        updateTextString();
        findViewById(R.id.imageButtonColor).setOnClickListener(this);
        findViewById(R.id.imageButtonImeg).setOnClickListener(this);
        findViewById(R.id.imageButtonСollection).setOnClickListener(this);
        findViewById(R.id.imageButtonPhoto).setOnClickListener(this);
        Util.setActionBar(getActionBar(),TAG, title);
    }

    private void updateTextString(){
        if(sensor == null) return;
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHandlerWork = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //здесь НЕ надо динамически обновлять значения
        mHandlerWork = false;
        //сам заводится и работает
        mHandler.postDelayed(new Runnable() {
            public void run() {
            //    Log.v(TAG,"mHandler --");
                updateTextString();
                // повторяем через каждые 300 миллисекунд
                if(mHandlerWork) mHandler.postDelayed(this, 400);
            }
        },500);
        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
        findViewById(R.id.activity_main_min_max).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String str ="";Uri uri;Float value;
         if((resultCode == RESULT_OK) && (sensor != null)){
             switch (requestCode){
                 case ACTIVITY_SETTING_COLOR:
                     str = data.getStringExtra(SettingName.EXTRAS_VALUE);
                      break;
                 case  ACTIVITY_SETTING_IMG:

                     break;
                 case  ACTIVITY_SETTING_СOLLECTION:

                     break;
                 case  ACTIVITY_SETTING_PHOTO:

                     break;
             }
             Log.i(TAG,str);
        } else{
            Log.e(TAG,"requestCode= "+ requestCode+"  resultCode= OBLOM");
        }
        updateTextString();
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG,"onOptionsItemSelected= "+ item);
        Intent intent = new Intent();
   //     intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, mName);
   //     intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, mAdress);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
    @Override
    public void onClick(View view) {
        Log.w(TAG,"onClick= "+view);
        Intent intent;
        if(sensor == null)  return;
        //
        switch (view.getId()){
            case android.R.id.home:
                Log.v(TAG,"home");
                intent = new Intent();
                //intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, mName);
                //intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, mAdress);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.imageButtonColor:
                Log.v(TAG,"imageButtonColor");
                 float value = 0;

                //----НАСТРОЙКА И ЗАПУСК окна ввода ЧИСЛА -----------
                intent = new Intent(this, SettingName.class);
                intent.putExtra(SettingName.EXTRAS_VALUE, String.format(" %2.1f",value));
                intent.putExtra(SettingName.EXTRAS_TYPE, SettingName.VALUE_TYPE_FLOAT);
                intent.putExtra(Util.EXTRAS_LABEL, "Уровень");
                intent.putExtra(SettingName.EXTRAS_HINT,"Введите число");

                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BA2");

                startActivityForResult(intent,ACTIVITY_SETTING_COLOR);
                //-----------------------------------
                break;
            case R.id.imageButtonImeg:
                Log.v(TAG,"imageButtonImeg");

                break;
            case R.id.imageButtonСollection:
                Log.v(TAG,"imageButtonСollection");
                break;
            case R.id.imageButtonPhoto:
                Log.v(TAG,"imageButtonPhoto");
                break;
            default:
        }
        return;
    }
}
