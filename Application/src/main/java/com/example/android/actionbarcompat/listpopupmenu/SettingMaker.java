package com.example.android.actionbarcompat.listpopupmenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

//public class MainSettingSetting extends AppCompatActivity implements View.OnClickListener{
public class SettingMaker extends Activity implements View.OnClickListener{
    //private  int mItem= 0;
    final   String TAG = getClass().getSimpleName();
    private  int mItem= 0;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_maker);
        //--------------------------
        final Intent intent = getIntent();
        mItem = intent.getIntExtra(MainActivity.EXTRAS_DEVICE_ITEM,0);
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if((app.mBluetoothLeServiceM != null)
                && (app.mBluetoothLeServiceM.mbleDot != null)
                && (app.mBluetoothLeServiceM.mbleDot.size() > 0)){
            sensor = app.mBluetoothLeServiceM.mbleDot.get(mItem);
            Log.v(TAG,"sensor item= " + mItem);
        } else {
            finish();
        }
        //-------------------------------------------
        updateTextString();
        findViewById(R.id.imageButtonFind).setOnClickListener(this);

        ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
        if (actionBar != null) {
            Log.d(TAG,"actionBar != null--");
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
//------------------------------------------------------
            //actionBar.setCustomView(null);
            //actionBar.setLogo(null);


        } else Log.e(TAG,"actionBar == null--");
        //   SampleGattAttributes.attributes.get("dd");
    }
    private void updateTextString(){
        if(sensor != null){
            Util.setTextToTextView(sensor.deviceName,R.id.textViewFindName, this);
//показывем только последних 5 цифр адреса
            String str = sensor.mBluetoothDeviceAddress;
            if(sensor.mBluetoothDeviceAddress != null){
                int i = str.length() - 7;
                if(i < 0) i = 0;
                str = str.substring(i);
            }
            Util.setTextToTextView(str,R.id.textViewFindAdress, this);
            Util.setTextToTextView(sensor.modelNumber,R.id.textViewModelNumber, this);
            Util.setTextToTextView(sensor.serialNumber,R.id.textViewSerialNumber, this);
            Util.setTextToTextView(sensor.firmwareRevision,R.id.textViewFirmwareRevision, this);
            Util.setTextToTextView(sensor.hardwareRevision,R.id.textViewHardwareRevision, this);
            Util.setTextToTextView(sensor.softwareRevision,R.id.textViewSoftwareRevision, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
        findViewById(R.id.textViewFindName).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }
    private  String mName;
    private  String mAdress;
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri=null;
        if((resultCode == RESULT_OK) && (requestCode == MainActivity.ACTIVITY_SETTING_MAKER)){
            mName = data.getStringExtra(MainActivity.EXTRAS_DEVICE_NAME);
            mAdress = data.getStringExtra(MainActivity.EXTRAS_DEVICE_ADDRESS);
//            Util.setTextToTextView(mName,R.id.textViewFindName, this);
//            Util.setTextToTextView(mAdress,R.id.textViewFindAdress, this);
            if(sensor != null){
                if(mName != null)sensor.deviceName = mName;
                if(mAdress != null){
                    sensor.mBluetoothDeviceAddress = mAdress;
                    //
                    RunDataHub app = ((RunDataHub) getApplicationContext());
                    if(app.mBluetoothLeServiceM != null){
                        app.mBluetoothLeServiceM.connect(mAdress,true);
                        Log.v(TAG,"sensor item= " + mItem + "  connectAdress= " + mAdress);
                    }
                }
            }

            updateTextString();
            Log.v(TAG,"requestCode= "+ requestCode +"  resultCode= RESULT_OK    name= " +mName
            +"   adress= "+ mAdress);
        } else{
            Log.e(TAG,"requestCode= "+ requestCode+"  resultCode= OBLOM");
        }
        // User chose not to enable Bluetooth.
//    if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//        finish();
//        return;
//    }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG,"onOptionsItemSelected= "+ item);
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, mName);
        intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, mAdress);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
    @Override
    public void onClick(View view) {
        Log.w(TAG,"onClick= "+view);
        Intent intent;
        switch (view.getId()){
            case android.R.id.home:
                Log.v(TAG,"home");
//                intent = new Intent();
//                intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, mName);
//                intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, mAdress);
//                setResult(RESULT_OK, intent);
//                finish();
                break;
            case R.id.imageButtonFind:
                Log.v(TAG,"imageButtonFind");

                intent = new Intent(this, DeviceScanActivity.class);
                // фильтр поиска устройств
                intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME_FILTR, "");
                startActivityForResult(intent, MainActivity.ACTIVITY_SETTING_MAKER);//на поиск к устройству
                break;
            default:
        }
        return;
    }
}
