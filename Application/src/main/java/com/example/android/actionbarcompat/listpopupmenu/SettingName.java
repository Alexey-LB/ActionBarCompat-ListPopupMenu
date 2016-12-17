package com.example.android.actionbarcompat.listpopupmenu;

import android.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.portfolio.alexey.connector.Util;


//public class SettingName extends AppCompatActivity {//} implements View.OnKeyListener{
public class SettingName extends Activity {//} implements View.OnKeyListener{
    final String TEG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_name);
        final Intent intent = getIntent();
        Util.setTextToTextView(intent.getStringExtra(MainActivity.EXTRAS_DEVICE_NAME)
                ,R.id.editTextName,this,"?");
        //
        findViewById(R.id.editTextName).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER){//выходим с результатом
                    Intent intent = new Intent();
                    if(view instanceof EditText){
                        String name = ((EditText)view).getText().toString();
                        if((name != null) && (name.length() >= 1)){
                            if(name.length() > 32) name = name.substring(0,31);
                            intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME,name);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    setResult(RESULT_CANCELED,intent);
                    finish();
                }
                return false;
            }
        });//
        ActionBar actionBar = getActionBar();//getSupportActionBar();//??--это решалось в другом методе(getDelegate().getSupportActionBar();)
        if (actionBar != null) {
            Log.d(TEG,"actionBar != null--  ");
            //вместо ЗНачка по умолчанию, назначаемого выше, подставляет свой
            // actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_black_24dp);
            //------------------------------
            //разрешить копку доиой
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);//устанавливает надпись и иконку как кнопку домой(не требуется
            //-- срабатывают только если вместе, отменяют ИКОНКУ, если заменить- достаточно одного
            actionBar.setIcon(null);//actionBar.setIcon(R.drawable.ic_language_black_24dp);
            actionBar.setDisplayUseLogoEnabled(false);
            //--- все ниже както не работет или для другого предназаначена
            //actionBar.setIcon(null);
            //actionBar.setCustomView(null);
            //actionBar.setDisplayUseLogoEnabled(false);
            //actionBar.setLogo(null);
            // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные

        } else Log.e(TEG,"actionBar == null--" );
        //getParent()
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TEG,"onOptionsItemSelected= "+ item);
        Intent intent = new Intent();
        //  intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
        //  intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_CANCELED, intent);
        finish();
        return true;
    }
    @Override
    protected void onResume() {
        ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
        super.onResume();
        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
        findViewById(R.id.editTextName).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }
//    @Override
//    public boolean onKey(View view, int i, KeyEvent keyEvent) {
//
//        Log.e(TAG,"view= " + view +"\n  i= " + i +"   keyEvent= " + keyEvent + "   KeyEvent.KEYCODE_ENTER=" + KeyEvent.KEYCODE_ENTER);
//        return false;
//    }

//    protected void onLck(ListView l, View v, int position, long id) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//        if (device == null) return;
//        final Intent intent = new Intent(this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);//на подклшючение к устройству
//    }
//
//@Override//сюда прилетают ответы при возвращении из других ОКОН активити
//protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    // User chose not to enable Bluetooth.
//    if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//        finish();
//        return;
//    }
//    super.onActivityResult(requestCode, resultCode, data);
//}



//    static boolean flag = false;
//    @Override
//    public void onClick(View view) {
//        Log.d(TAG,"onClick= "+view);
//
//        switch (view.getId()){
//            case android.R.id.home:
//                Log.v(TAG,"home");
//                break;
//            case R.id.imageButtonName:
//                Log.v(TAG,"imageButtonName");
//
//                break;
//            case R.id.textViewName:
//                //if(view.isActivated())
//                   // view.setActivated(!view.isActivated());
//               view.setFocusable(flag);
//                view.setFocusableInTouchMode(flag);
//                view.refreshDrawableState();
//                flag = !flag;
//               // view.setEnabled(!view.isEnabled());
//                Log.v(TAG,"textViewName");
//                break;
//            case R.id.imageButtonMarker:
//                Log.v(TAG,"imageButtonMarker");
//                break;
//            case R.id.imageButtonTermometer:
//                Log.v(TAG,"imageButtonTermometer");
//                break;
//            case R.id.imageButtonMeasurementMode:
//                Log.v(TAG,"imageButtonMeasurementMode");
//                break;
//            case R.id.imageButtonMelody:
//                Log.v(TAG,"imageButtonMelody");
//                break;
//            case R.id.imageButtonVibration:
//                Log.v(TAG,"imageButtonVibration");
//                break;
//            case R.id.imageButtonTemperaturesAbove:
//                Log.v(TAG,"imageButtonTemperaturesAbove");
//                break;
//            case R.id.imageButtonTemperaturesBelow:
//                Log.v(TAG,"imageButtonTemperaturesBelow");
//                break;
//            case R.id.imageButtonDecor:
//                Log.v(TAG,"imageButtonDecor");
//                break;
//            default:
//        }
//        return;
//    }
}
