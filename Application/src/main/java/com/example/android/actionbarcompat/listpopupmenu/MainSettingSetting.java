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
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.Sensor;

//public class MainSettingSetting extends AppCompatActivity implements View.OnClickListener{
public class MainSettingSetting  extends Activity implements View.OnClickListener{
    private final int EDIT_NAME= 456;
    private final int GET_URL_RING= 567;
    //private  int mItem= 0;
    private Sensor sensor;
    final   String TAG = getClass().getSimpleName();
//    private void getSttingInSensor(int idView, Object object ){
//        View view;
//        view = findViewById(idView);
//        if((view == null) || (sensor == null)) return;
//        //
//        view.setOnClickListener(this);
//        if((object instanceof String) && (view instanceof TextView)){
//
//        }
//        if(sensor != null){
//            if(view instanceof TextView) {
//                if(sensor.deviceLabel != null) ((TextView)view).setText(sensor.deviceLabel);
//                else ((TextView)view).setText("?");
//            }
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        //--------------------------
        final Intent intent = getIntent();
        int i = intent.getIntExtra(MainActivity.EXTRAS_DEVICE_ITEM,0);
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if((app.mBluetoothLeServiceM != null)
                && (app.mBluetoothLeServiceM.mbleDot != null)
                && (app.mBluetoothLeServiceM.mbleDot.size() > 0)){
            sensor = app.mBluetoothLeServiceM.mbleDot.get(i);
            Log.v(TAG,"sensor item= " + i);
        } else {
            finish();
        }
        //-------------------------------------------
        View view;
        view = findViewById(R.id.textViewName);
        view.setOnClickListener(this);
        if(sensor != null){
            if(view instanceof TextView) {
               if(sensor.deviceLabel != null) ((TextView)view).setText(sensor.deviceLabel);
                else ((TextView)view).setText("?");
            }
        }
        //view = findViewById(R.id.textViewName).setOnClickListener(this);
        findViewById(R.id.imageButtonMarker).setOnClickListener(this);
        view = findViewById(R.id.imageButtonMarker);
        String key = "white";
        if(sensor != null){
            key = marker. markerKey[sensor.markerColor];
        }
        view.setBackgroundResource(marker.get(key));
        view.setTag(key);
        //
        findViewById(R.id.imageButtonTermometer).setOnClickListener(this);
        findViewById(R.id.imageButtonVibration).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesAbove).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesBelow).setOnClickListener(this);
        findViewById(R.id.imageButtonMelody).setOnClickListener(this);
        findViewById(R.id.imageButtonDecor).setOnClickListener(this);
        //   findViewById(R.id.imageButtonMeasurementMode).setOnClickListener(this);
        findViewById(R.id.textViewMeasurementMode).setOnClickListener(this);
        findViewById(R.id.imageButtonName).setOnClickListener(this);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG,"onOptionsItemSelected= "+ item);
        Intent intent = new Intent();
      //  intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
      //  intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_OK, intent);
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
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case EDIT_NAME:
                    name = data.getStringExtra("EXTRAs_DEVICE_NAME");
                    str = "DEVICE_NAME= " + name;
                    View edv = findViewById(R.id.textViewName);
                    if(edv instanceof TextView){
                        if((name != null) && (name.length() > 0)) ((TextView)edv).setText(name);
                    }
                    if(sensor != null){
                        if(name != null)sensor.deviceLabel = name;
                    }
                    break;
                case GET_URL_RING:
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    urI = uri;
                    str = "Uri= " + uri;
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
    private Uri urI = null;

    static private Marker marker = new Marker();
    static boolean flag = false;
    private int index = 0;
    @Override
    public void onClick(View view) {
        Log.w(TAG,"onClick= "+view);
        String action="";Intent intent;Vibrator vibrator;
        Uri alert;

        switch (view.getId()){
            case android.R.id.home:
                Log.v(TAG,"home");

                break;
            case R.id.imageButtonName:
                Log.v(TAG,"imageButtonName");
                intent = new Intent(this, SettingName.class);
                View edv = findViewById(R.id.textViewName);
                if(edv instanceof TextView){
                    intent.putExtra("EXTRAs_DEVICE_NAME", ((TextView)edv).getText().toString());
                    //     Log.v(TAG,"imageButtonName= " + ((TextView)edv).getText().toString());
                    //startActivity(intent);//на подклшючение к устройству
                    startActivityForResult(intent,EDIT_NAME);
                }
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

                //   ((LevelListDrawable)view.getBackground()).setLevel(1);
                // if(view.getBackground().getLevel() >= 2) view.getBackground().setLevel(0);
                // else view.getBackground().setLevel(view.getBackground().getLevel() + 1);
//пока временно решил сделать так, через фон
                String key = "white";
                if(view.getTag() != null) key = view.getTag().toString();
                key = marker.getNextKey(key);
                view.setBackgroundResource(marker.get(key));
                view.setTag(key);
                if(sensor != null) sensor.markerColor = marker.getItemKey(key);
                break;
            case R.id.imageButtonTermometer:
                Log.v(TAG,"imageButtonTermometer");

                break;
            //case R.id.imageButtonMeasurementMode:
            case R.id.textViewMeasurementMode:
                Log.v(TAG,"imageButtonMeasurementMode");
                if(view instanceof TextView){
                    TextView v = (TextView)view;
                    String str = "Медицинский";
                    if((v.getText() != null) &&(v.getText().length() > 1)){
                        if(v.getText().toString().compareTo(str) == 0)str = "Универсальный";
                        v.setText(str);
                    }
                }
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
                playerRingtone(0f, urI);
                break;
            case R.id.imageButtonDecor:
                Log.v(TAG,"imageButtonDecor");
                playerRingtone(1f,  urI);
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
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");

        // for existing ringtone
        Uri urie;
//        urie =     RingtoneManager.getActualDefaultRingtoneUri(
//                getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        urie = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, urie);

        startActivityForResult(intent, GET_URL_RING);
    }
    // https://geektimes.ru/post/232885/
// Что бы звук не был тихим:
// stackoverflow.com/questions/8278939/android-mediaplayer-volume-is-very-low-already-adjusted-volume
    //пример: playerRingtone(0.8f, null); рингтонг 0.8 от макимума звука и мелодия по умолчанию
    //пример: playerRingtone(0f, Uri); ГРОМКОСТ звука СИТЕМНОЙ настройки проигрывателя и мелодия по Uri
    public void playerRingtone(Float setVolume, Uri uriRingtone ){
        if(uriRingtone == null){// Сигнал по умолчанию
            uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        MediaPlayer mediaPlayer= new MediaPlayer();
        if(setVolume == 0){//ГРОМКОСТЬ системных настроек
            setVolume = 1f;
        }else{
            // (Завист только от setVolume)НА ПОЛНУЮ гмкость ВНЕ зависмости от УСТАНОВКИ в СИСТЕМЕ!!!
//http://stackoverflow.com/questions/8278939/android-mediaplayer-volume-is-very-low-already-adjusted-volume
            AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            // int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            int maxVolume = amanager.getStreamVolume(AudioManager.STREAM_ALARM);
            amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM); // this is important.
        }
        //устанавливает ОТ максимума!!!
        mediaPlayer.setVolume(setVolume, setVolume);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uriRingtone);
            //  mediaPlayer.setLooping(looping);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                //после проигрывания попадает сюда
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //останавливается ПЛЕЕР и выбрасывается из памяти
                    mp.release();//Это закончит, освободить, отпустить
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error default media ", Toast.LENGTH_LONG).show();
            Log.e(TAG, "  Ringtone ERR= " + e);
        }

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
