package com.example.android.actionbarcompat.listpopupmenu;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

        import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.portfolio.alexey.connector.Util;


//public class SettingInput extends AppCompatActivity {//} implements View.OnKeyListener{
public class SettingInput extends Activity {//} implements View.OnKeyListener{
    final String TAG = getClass().getSimpleName();
    public  final static int VALUE_TYPE_INT = 10;
    public  final static int VALUE_TYPE_FLOAT = 20;
    public  final static int VALUE_TYPE_STRING = 30;
    public  final static int VALUE_TYPE_FLOAT_TEMPERATURE = 40;

    public  final static String EXTRAS_TYPE = "EXTRAS_TYPE";

    public  final static String EXTRAS_VALUE = "EXTRAS_VALUE";
    public  final static String EXTRAS_HINT = "EXTRAS_HINT";

    public  final static String EXTRAS_FLOAT_MIN = "EXTRAS_FLOAT_MIN";
    public  final static String EXTRAS_FLOAT_MAX = "EXTRAS_FLOAT_MAX";


    public  final static String EXTRAS_INT_MIN = "EXTRAS_INT_MIN";
    public  final static String EXTRAS_INT_MAX = "EXTRAS_INT_MAX";
    public Float max = 70f;//диапазон работы SeekBar
    public Float min = -20f;
    public Float accuracy = 0.1f;//точность представления- для  SeekBar
    public int offset = 0;//смещение 0- для  SeekBar (у бара минимум 0, - нет!!)
    private SeekBar sb;
    private TextView tv;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullscreen(this);// работает отлично! один раз объевил, работает пока окно не умрет!
        setContentView(R.layout.setting_input);
        String extras; Float fl;
        final Intent intent = getIntent();

        //Метка поля, указываетчто вводим
        Util.setTextToTextView(intent.getStringExtra(Util.EXTRAS_LABEL)
                ,R.id.textViewLabelName,this,"");

        //установка напрямую численного значения
        sb = (SeekBar)findViewById(R.id.seekBar);
        //поле ввода, по умолчанию ДОСТУПНЫ ВСЕ СИМВОЛЫ
        tv= (TextView)findViewById(R.id.editTextName);

        // посказка, если ввод пустой!
        tv.setHint(intent.getStringExtra(EXTRAS_HINT));

        Util.setTextToTextView(intent.getStringExtra(EXTRAS_VALUE)
                ,R.id.editTextName,this,"");
         //определяем че читаем и настраиваем ввод значения
        // СТРОКА или ЧИСЛО!! по умолчанию строка
        type = intent.getIntExtra(EXTRAS_TYPE,VALUE_TYPE_STRING);
//-----------
 //2017.01.24- пока принимаем ВСЕГДА показывать клавиатуру
 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //---------
        switch(type){
            case VALUE_TYPE_INT:
                tv.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
                sb.setVisibility(View.VISIBLE);
                fl = Util.parseFloat(intent.getStringExtra(EXTRAS_VALUE));
                setSettingSeekBar(fl);
  //2017.01.24- пока принимаем ВСЕГДА показывать клавиатуру
 //               /// всегда скрыват клавиатуру при вводе числа (есть сикБАР)!!
 //               getWindow().setSoftInputMode(
  //                      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;
            case VALUE_TYPE_FLOAT :
                tv.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
                sb.setVisibility(View.VISIBLE);
                fl = Util.parseFloat(intent.getStringExtra(EXTRAS_VALUE));
                max = Util.parseFloat(intent.getStringExtra(EXTRAS_FLOAT_MAX));
                min = Util.parseFloat(intent.getStringExtra(EXTRAS_FLOAT_MIN));
                setSettingSeekBar(fl);
                /// всегда скрыват клавиатуру при вводе числа (есть сикБАР)!!
   //2017.01.24- пока принимаем ВСЕГДА показывать клавиатуру
  //              getWindow().setSoftInputMode(
  //                      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;
            case VALUE_TYPE_STRING:
                sb.setVisibility(View.GONE);
               // tv.setShowSoftInputOnFocus(true);
                /// всегда показывать клавиатуру при вводе имени!!
  //2017.01.24- пока принимаем ВСЕГДА показывать клавиатуру
 //               getWindow().setSoftInputMode(
 //                       WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                break;
            default:
        }
        //
        tv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(i == KeyEvent.KEYCODE_ENTER){//выходим с результатом
                    onFinish(view, RESULT_OK);
                }else{
                    //при изменении числа- меняем Положение движка
                    if(view instanceof TextView){
                       Float fl = Util.parseFloat(((TextView)view).getText().toString());
                        setSeekBar(fl);
                 //       Log.i(TAG,"fl= " + fl);
                    }
                }
                return false;
            }
        });//
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean start = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(start)updateTextString(getValueString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {start = true;}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {start = false;}
        });//
        Util.setActionBar(getActionBar(),TAG, intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //getParent()
        Log.w(TAG, "min= " + min+"   max= " + max + "  offset"+offset + "   sb.getMax()= "+ sb.getMax());
    }

    private void onFinish(View view,int result){
        Intent intent = new Intent();
        if((result == RESULT_OK) && (view instanceof EditText)){
            String str = ((EditText)view).getText().toString();
            if((str != null) && (str.length() >= 1)){
                if(str.length() > 32) str = str.substring(0,31);
                intent.putExtra(MainActivityWork.EXTRAS_DEVICE_NAME,str);
                intent.putExtra(EXTRAS_VALUE,str);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        setResult(RESULT_CANCELED,intent);
        finish();
    }
    private void setSeekBar(Float fl){
        if(fl != null){
            //с учетом смещения
            sb.setProgress((int)(fl*10) + offset);
        }
    }
    private void setSettingSeekBar(Float fl){
        int set = (int)((Math.abs(max) + Math.abs(min)) / accuracy);
        offset = (int)(Math.abs(min) / accuracy);
        sb.setMax(set);
        setSeekBar(fl);
    }
    private String getValueString(int progress){
        int i = progress - offset;
        return String.format("%d.%d",i/10,Math.abs(i%10));
    }

    private void updateTextString(final String value){
        Util.setTextToTextView(value,R.id.editTextName,this,"");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG,"onOptionsItemSelected= "+ item);
        //если отказались от изменений, возвращяем все назад
        if(item.getItemId() != DONE){
            onFinish(tv, RESULT_CANCELED);
        } else onFinish(tv, RESULT_OK);
        return true;
    }
    private final int DONE = 754849;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE,DONE,Menu.NONE,getResources().getString(R.string.sAccept))
              //  .setIcon(R.drawable.ic_clear_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onResume() {
       // ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
        super.onResume();

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
