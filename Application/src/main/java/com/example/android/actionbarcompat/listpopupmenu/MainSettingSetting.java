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
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

//public class MainSettingSetting extends AppCompatActivity implements View.OnClickListener{
public class MainSettingSetting  extends Activity implements View.OnClickListener{
    private final int EDIT_NAME= 456;
    private final int GET_URL_RING= 567;
    private  int mItem= 0;
    private Sensor sensor;
    final   String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
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
        findViewById(R.id.textViewName).setOnClickListener(this);
        Util.setTextToTextView(sensor.deviceLabel,R.id.textViewName,this,"?");
        findViewById(R.id.imageButtonMarker).setOnClickListener(this);

        if(Util.isNoNull(sensor)){
            int im = 0x7 & sensor.markerColor;
            ((ImageView)findViewById(R.id.imageButtonMarker))
                    .setBackgroundResource(Marker.getIdImg(im));
        }
        //
        findViewById(R.id.imageButtonTermometer).setOnClickListener(this);
        findViewById(R.id.imageButtonVibration).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesAbove).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesBelow).setOnClickListener(this);
        findViewById(R.id.imageButtonMelody).setOnClickListener(this);
        findViewById(R.id.imageButtonDecor).setOnClickListener(this);
        //   findViewById(R.id.imageButtonMeasurementMode).setOnClickListener(this);
        //00000
        findViewById(R.id.textViewMeasurementMode).setOnClickListener(this);
        Util.setTextToTextView(sensor.getStringMeasurementMode()
                ,R.id.textViewMeasurementMode,this,"?");
        udateMeasurementMode();

        findViewById(R.id.imageButtonName).setOnClickListener(this);
        Util.setActionBar(getActionBar(),TAG, "  B4/B5");
//
//        ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
//        if (actionBar != null) {
//            Log.d(TAG,"actionBar != null--");
//            //вместо ЗНачка по умолчанию, назначаемого выше, подставляет свой
//            // actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_black_24dp);
//            //------------------------------
//          //  actionBar.
//            //разрешить копку доиой
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);//устанавливает надпись и иконку как кнопку домой(не требуется
//           // actionBar.setDisplayUseLogoEnabled(true);
//        //    actionBar.setHomeButtonEnabled(true); метод - actionBar.setDisplayHomeAsUpEnabled(true);)
//            //чето не показыввет ее
////-- срабатывают только если вместе, отменяют ИКОНКУ, если заменить- достаточно одного
//            actionBar.setIcon(null);//actionBar.setIcon(R.drawable.ic_language_black_24dp);
//            actionBar.setDisplayUseLogoEnabled(false);
////------------------------------------------------------
//            //actionBar.setCustomView(null);
//            //actionBar.setLogo(null);
//        } else Log.e(TAG,"actionBar == null--");
//        //   SampleGattAttributes.attributes.get("dd");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG,"onOptionsItemSelected= "+ item);
        Intent intent = new Intent();
      //  intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
      //  intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_OK, intent);
        //--сохранение настроек
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if(app.mBluetoothLeServiceM != null){
            app.mBluetoothLeServiceM.settingPutFile();
        }
        finish();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // установка ИЗОБРАЖЕНИЕ на всь экран, УБИРАЕМ СВЕРХУ И СНИЗУ панели системные
        findViewById(R.id.textViewName).getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }


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
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name,str = "?";Uri uri=null;
        if(resultCode == RESULT_OK) {
            switch(requestCode){
                case EDIT_NAME:
                    name = data.getStringExtra(MainActivity.EXTRAS_DEVICE_NAME);
                    str = MainActivity.EXTRAS_DEVICE_NAME + name;
                    if(name.length() > 64) name = name.substring(0,63);
                    if(Util.isNoNull(sensor, name))sensor.deviceLabel = name;
                    //
                    Util.setTextToTextView(name,R.id.textViewName,this,"?");
                    break;
                case GET_URL_RING:
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    //если без звука= то нулл!
                    if(Util.isNoNull(sensor)){
                        if(uri != null){
                            sensor.endMelody = uri.toString();
                            str = "Uri= " + uri.toString();
                        } else sensor.endMelody = null;
                    }
                    str = str + "   Uri= " + uri;
                    break;
                case MainActivity.ACTIVITY_SETTING_SETTING:
                    //обновить отображение
                    break;
            }

            Log.v(TAG,"requestCode= "+ requestCode +"  resultCode= RESULT_OK    " +str);
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
    private void udateMeasurementMode(){
        View v,v2;
        if(sensor == null) return;
        if(sensor.getMeasurementMode() == 0){//медецинский
           v = findViewById(R.id.activity_main_setting_item2);
            v2 = findViewById(R.id.activity_main_setting_item3);
        }else{
            v = findViewById(R.id.activity_main_setting_item3);
            v2 = findViewById(R.id.activity_main_setting_item2);
        }
        if(v != null) v.setVisibility(View.VISIBLE);
        if(v2 != null) v2.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        Log.w(TAG,"onClick= "+view);
        String action="";Intent intent;Vibrator vibrator;
        Uri alert; String str = "";

        switch (view.getId()){
            case android.R.id.home:
                Log.v(TAG,"home");

                break;
            case R.id.imageButtonName:
                Log.v(TAG,"imageButtonName");
                if(Util.isNoNull(sensor, sensor.deviceLabel)) str = sensor.deviceLabel;
                intent = new Intent(this, SettingName.class);
                intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, str);
                startActivityForResult(intent,EDIT_NAME);
                break;
            case R.id.textViewName:
                Log.v(TAG,"textViewName");
                //   intent = new      Intent(Intent.ACTION_EDIT);//ACTION_PROCESS_TEXT
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
//
//                urie = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, urie);

                //  startActivityForResult(intent, 77);
                //if(view.isActivated())
                // view.setActivated(!view.isActivated());
//               view.setFocusable(flag);
//                view.setFocusableInTouchMode(flag);
//                view.refreshDrawableState();
//                flag = !flag;
//               // view.setEnabled(!view.isEnabled());
//
                break;



            case R.id.imageButtonMarker:
                Log.v(TAG,"imageButtonMarker");
//======= ЭТО работает тОЛЬКО с ВЕКТОРАМИ и бит мап, с ШЕПАМИ НЕ работает!!===================================
//               index++;
//                if(index > 2) index = 0;
//                ((ImageView)view).setImageLevel(index);
// ========================================
//пока временно решил сделать так, через фон
                if(sensor != null){
                    sensor.markerColor = Marker.getNextItem(sensor.markerColor);
                    ((ImageView)view).setBackgroundResource(Marker.getIdImg(sensor.markerColor));
                    Log.v(TAG,"imageButtonMarker= " + sensor.markerColor);
                }
                break;
            //ПОИСК ТЕРМОmЕТРА!!!
            case R.id.imageButtonTermometer:
                Log.v(TAG,"imageButtonTermometer");
                if(sensor != null){
                    intent = new Intent(this, SettingMaker.class);
                    intent.putExtra(MainActivity.EXTRAS_DEVICE_ITEM , mItem);
                    startActivityForResult(intent,MainActivity.ACTIVITY_SETTING_SETTING);
                }
                break;
            //case R.id.imageButtonMeasurementMode:
            case R.id.textViewMeasurementMode:
                if(sensor != null){
                    sensor.changeMeasurementMode();//меняем моду измерения
                    str = sensor.getStringMeasurementMode();
                    Util.setTextToTextView(str, view,"!");
                }
                udateMeasurementMode();
                break;
            case R.id.imageButtonMelody:
                Log.v(TAG,"imageButtonMelody");
//работает
//                intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("*/*");
//                startActivityForResult(intent, 1);
                //====================
//                intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
//                startActivityForResult(intent,77);

                pickRingtone();
                break;
            case R.id.imageButtonVibration:
                Log.v(TAG,"imageButtonVibration");
                action = VIBRATOR_SERVICE;
                //startActivity(new Intent(action));
                // https://geektimes.ru/post/232885/
                vibrator = (Vibrator) getSystemService (VIBRATOR_SERVICE);
                try {
                    vibrator.vibrate(400);
                }catch (Exception e){
                    Log.e(TAG, " vibrator ERR= " + e);
                }
                break;
            case R.id.imageButtonTemperaturesAbove:
                Log.v(TAG,"imageButtonTemperaturesAbove");
// https://geektimes.ru/post/232885/
                vibrator = (Vibrator) getSystemService (VIBRATOR_SERVICE);
                try {
                    vibrator.vibrate(400);
                }catch (Exception e){
                    Log.e(TAG, " vibrator ERR= " + e);
                }
                break;
            case R.id.imageButtonTemperaturesBelow:
                Log.v(TAG,"imageButtonTemperaturesBelow");
                if(sensor != null){
                    Util.playerRingtone(0f, sensor.endMelody , this,TAG);
                } else Util.playerRingtone(0f, (Uri) null , this,TAG);
                break;
            case R.id.imageButtonDecor:
                Log.v(TAG,"imageButtonDecor");
                  if(sensor != null){
                    Util.playerRingtone(1f, sensor.endMelody , this,TAG);
                } else Util.playerRingtone(1f, (Uri)null , this,TAG);
                break;
            default:
        }
        return;
    }
    //http://stackoverflow.com/questions/7671637/how-to-set-ringtone-with-ringtonemanager-action-ringtone-picker
// http://www.ceveni.com/2009/07/ringtone-picker-in-android-with-intent.html
    public void pickRingtone() {
        // TODO Auto-generated method.   stub

        Intent intent = new      Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "  BB4");

        // for existing ringtone
        Uri urie;
//        urie =     RingtoneManager.getActualDefaultRingtoneUri(
//                getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        if(Util.isNoNull(sensor)){
            // TODO: 17.12.2016 обработать в случае ошибок парсера
            if(sensor.endMelody != null) {
                urie = Uri.parse(sensor.endMelody);
                Log.v(TAG,"goto change melody carent= "+ urie.toString());
            } else {
                urie = null;//тоесть БЕЗ звука!!
                Log.v(TAG,"Звука нет urie == null");
            }

        } else urie = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, urie);

        startActivityForResult(intent, GET_URL_RING);
    }

    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //"notifications_new_message_ringtone" - ключи полей в ХМЛ указаны
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MainSettingSetting.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty(stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary("silent");

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary(null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };
}
