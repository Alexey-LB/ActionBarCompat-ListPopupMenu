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


public class SettingMinMax extends Activity implements View.OnClickListener{
    //private  int mItem= 0;
    final   String TAG = getClass().getSimpleName();
    final int ACTIVITY_SETTING_MIN_MAX_VALUE = 67890;
    final int ACTIVITY_SETTING_URL_MELODI = 67891;
    private  int mItem= 0;
    private Sensor sensor;
    //private float fl=0;
    private boolean mHandlerWork = true;
    private Handler mHandler = new Handler();
    private boolean maxValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_min_max);
        //--------------------------
        final Intent intent = getIntent();
       // Log.w(TAG, "getParentActivityIntent()= " +getParentActivityIntent());
        mItem = intent.getIntExtra(Util.EXTRAS_ITEM,0);
        String title = intent.getStringExtra(Util.EXTRAS_BAR_TITLE);
        // все изменения будет писать сразу в сенсор
        //fl = intent.getFloatExtra(Util.EXTRAS_FLOAT_1,0);
        //
        sensor = Util.getSensor(mItem,this);
        if(sensor == null) finish();

        //-------------------------------------------
        updateTextString();
        findViewById(R.id.imageButtonMelody).setOnClickListener(this);
        findViewById(R.id.imageButtonValue).setOnClickListener(this);
        findViewById(R.id.switchOn).setOnClickListener(this);;
        findViewById(R.id.switchVibration).setOnClickListener(this);
        Util.setActionBar(getActionBar(),TAG, title);
    }

    private void updateTextString(){
        if(sensor == null) return;
        //
        if(maxValue){
            Util.setTextToTextView(sensor.getStringMaxTemperature(true)
                    ,R.id.textViewValue, this);
            ((Switch)findViewById(R.id.switchVibration))
                    .setChecked(sensor.onMaxVibration);
        }else{
            Util.setTextToTextView(sensor.getStringMinTemperature(true)
                    ,R.id.textViewValue, this);
            ((Switch)findViewById(R.id.switchVibration))
                    .setChecked(sensor.onMinVibration);
        }
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
    private final float max = 70f;
    private final float min = -20f;
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String str ="";Uri uri;float value;
         if((resultCode == RESULT_OK) && (sensor != null)){
             switch (requestCode){
                 case ACTIVITY_SETTING_MIN_MAX_VALUE:
                     str = data.getStringExtra(MainActivity.EXTRAS_DEVICE_NAME);
                     if((str != null) && (str.length() > 0))value = Float.valueOf(str);
                     else break;
                     if (value > max) value = max;
                     if (value < min) value = min;
                     //
                     if(maxValue) sensor.maxTemperature  = value;
                     else  sensor.minTemperature  = value;
                     str = "  float= " + str + "  max/min= " + sensor.maxTemperature +" / "+ sensor.minTemperature;
                     break;
                 case ACTIVITY_SETTING_URL_MELODI:
                     uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                     //если без звука= то нулл!
                      if(uri != null){
                         str = uri.toString();
                     } else str = null;
                     if(maxValue) sensor.maxMelody = str;
                     else sensor.minMelody = str;
                     str = "   Uri= " + str;
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
            case R.id.imageButtonValue:
                Log.v(TAG,"imageButtonValue");
                 float value;
                if(maxValue) value = sensor.maxTemperature;
                else value = sensor.maxTemperature;

                intent = new Intent(this, SettingName.class);
                intent.putExtra(Util.EXTRAS_FLOAT_1, String.format("%2.1f",value));
                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BC3");
                startActivityForResult(intent,ACTIVITY_SETTING_MIN_MAX_VALUE);
                break;
            case R.id.imageButtonMelody:
                Log.v(TAG,"imageButtonMelody");
                InputBox.pickRingtone(ACTIVITY_SETTING_URL_MELODI, "    BC4"
                        , sensor.minMelody,TAG, this);
                break;
            case R.id.switchOn:


                break;
            case R.id.switchVibration:
                if(maxValue) {
                    sensor.onMaxVibration = ((Switch)findViewById(R.id.switchVibration))
                            .isChecked();
                }  else {
                    sensor.onMinVibration = ((Switch)findViewById(R.id.switchVibration))
                            .isChecked();
                }
                break;
            default:
        }
        return;
    }
}
