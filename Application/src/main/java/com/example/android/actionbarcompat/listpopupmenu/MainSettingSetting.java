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

//import android.widget.Switch;
import com.kyleduo.switchbutton.SwitchButton;
import com.portfolio.alexey.connector.InputBox;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

//public class MainSettingSetting extends AppCompatActivity implements View.OnClickListener{
public class MainSettingSetting  extends Activity implements View.OnClickListener{
  //  private final int EDIT_NAME= 456;
  //  private final int GET_URL_RING= 567;
    public final  static int ACTIVITY_SETTING = 3456781;
    public final  static int ACTIVITY_SETTING_DEVICE = 3456782;
    public final  static int ACTIVITY_SETTING_MAX = 3456783;
    public final  static int ACTIVITY_SETTING_MIN = 3456784;
    public final  static int ACTIVITY_SETTING_EDIT = 3456785;
    public final  static int ACTIVITY_SETTING_URL_MELODI = 3456786;
    public final  static int ACTIVITY_SETTING_DECOR = 3456787;
    private  int mItem= 0;
    private Sensor sensor;
    final   String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        //--------------------------
        final Intent intent = getIntent();
        mItem = intent.getIntExtra(MainActivityWork.EXTRAS_DEVICE_ITEM,0);
        //sensor = Util.getSensor(mItem,this);
        sensor = Util.getSensor(mItem);
        if(sensor == null) finish();
        Util.setActionBar(getActionBar(),TAG, intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //УСТАНАВИЛ флаг изменения, чтоб потом записать в ФАЙЛ на флеш,
        // ЕСЛИ сюда зашли, значит надо сохранятся!!пока так
        sensor.changeConfig = true;//установки считаны из ФЛЕШИ- не изменены!!
        //-------------------------------------------
        findViewById(R.id.textViewName).setOnClickListener(this);
        findViewById(R.id.imageButtonMarker).setOnClickListener(this);
        //
        findViewById(R.id.imageButtonTermometer).setOnClickListener(this);
        findViewById(R.id.switchVibration).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesAbove).setOnClickListener(this);
        findViewById(R.id.imageButtonTemperaturesBelow).setOnClickListener(this);
        findViewById(R.id.imageButtonMelody).setOnClickListener(this);
        findViewById(R.id.imageButtonDecor).setOnClickListener(this);
        //00000
        findViewById(R.id.textViewMeasurementMode).setOnClickListener(this);
        findViewById(R.id.imageButtonName).setOnClickListener(this);

        updateTextString();
    }
    private void updateTextString(){
        Util.setTextToTextView(sensor.deviceLabel,R.id.textViewName,this,"?");


        if(sensor.maxLevelNotification.switchNotification){
            Util.setTextToTextView(sensor.getStringMaxTemperature(true)
                    ,R.id.textViewTemperaturesAbove,this,"-");
        } else{
            Util.setTextToTextView("-",R.id.textViewTemperaturesAbove,this,"-");
        }

        if(sensor.minLevelNotification.switchNotification){
            Util.setTextToTextView(sensor.getStringMinTemperature(true)
                    ,R.id.textViewTemperaturesBelow,this,"-");
        } else{
            Util.setTextToTextView("-",R.id.textViewTemperaturesBelow,this,"-");
        }

        Util.setDrawableToImageView(sensor.markerColor,R.id.imageButtonMarker, this);

//        ((Switch)findViewById(R.id.switchVibration))
//                .setChecked(sensor.onEndVibration);

        udateMeasurementMode();
    }
    private void udateMeasurementMode(){
        View v,v2;
        if(sensor == null) return;

        Util.setTextToTextView(sensor.getStringMeasurementMode(),
                findViewById(R.id.textViewMeasurementMode),"!");

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

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name,str = "?";Uri uri=null;float fl;
        if(resultCode == RESULT_OK) {
            switch(requestCode){
                case ACTIVITY_SETTING_EDIT:
                    name = data.getStringExtra(SettingName.EXTRAS_VALUE);
                    str = SettingName.EXTRAS_VALUE + name;
                    if(name.length() > 64) name = name.substring(0,63);
                    if((sensor != null) && (name != null))sensor.deviceLabel = name;
                    //
                    Util.setTextToTextView(name,R.id.textViewName,this,"?");
                    break;
                case ACTIVITY_SETTING_URL_MELODI:
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    //если без звука= то нулл!
                    if(sensor != null){
                        if(uri != null){
                            sensor.endMeasurementNotification.melody = uri.toString();
                            str = "Uri= " + uri.toString();
                        } else sensor.endMeasurementNotification.melody = null;
                    }
                    str = str + "   Uri= " + uri;
                    break;
                case ACTIVITY_SETTING_DEVICE:
                    //обновить отображение
                    break;
                case ACTIVITY_SETTING_MAX:
                    //все уже записано в сенсор
                    //обновить отображение
                    Util.setTextToTextView(sensor.getStringMaxTemperature(true)
                            ,R.id.textViewTemperaturesAbove,this,"-");
                    break;
                case ACTIVITY_SETTING_MIN:
                    //все уже записано в сенсор
                    //обновить отображение
                    Util.setTextToTextView(sensor.getStringMinTemperature(true)
                            ,R.id.textViewTemperaturesBelow,this,"-");
                    break;
            }
            updateTextString();
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

    @Override
    public void onClick(View view) {
        Log.w(TAG,"onClick= "+view);
        Intent intent;String str = "";
        if(sensor == null) return;
        //УСТАНОВИЛИ, что меняли настройки, необходимо при сохранении ЗАПИСИ во флеш
        sensor.changeConfig = true;
        //
        switch (view.getId()){
            case android.R.id.home:
                Log.v(TAG,"home");
                break;
            case R.id.imageButtonName:
                Log.v(TAG,"imageButtonName");
                if(sensor.deviceLabel != null) str = sensor.deviceLabel;
                intent = new Intent(this, SettingName.class);
                intent.putExtra(SettingName.EXTRAS_VALUE, str);
                intent.putExtra(SettingName.EXTRAS_TYPE, SettingName.VALUE_TYPE_STRING);
                intent.putExtra(Util.EXTRAS_LABEL, "Имя");
                intent.putExtra(SettingName.EXTRAS_HINT,"Введите имя");
                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BB1");

                startActivityForResult(intent,ACTIVITY_SETTING_EDIT);
                break;
            case R.id.textViewName:
                Log.v(TAG,"textViewName");
                break;
            case R.id.imageButtonMarker:
                Log.v(TAG,"imageButtonMarker");
//======= ЭТО работает тОЛЬКО с ВЕКТОРАМИ и бит мап, с ШЕПАМИ НЕ работает!!===================================
//               index++;
//                if(index > 2) index = 0;
//                ((ImageView)view).setImageLevel(index);
// ========================================
//пока временно решил сделать так, через фон
                sensor.markerColor = Marker.getNextItem(sensor.markerColor);
                Util.setDrawableToImageView(sensor.markerColor,R.id.imageButtonMarker, this);
                Log.v(TAG,"imageButtonMarker= " + sensor.markerColor);
                break;
            //ПОИСК ТЕРМОmЕТРА!!!
            case R.id.imageButtonTermometer:
                Log.v(TAG,"imageButtonTermometer");
                intent = new Intent(this, SettingMaker.class);
                intent.putExtra(MainActivityWork.EXTRAS_DEVICE_ITEM , mItem);
                startActivityForResult(intent,ACTIVITY_SETTING_DEVICE);
                break;
            //case R.id.imageButtonMeasurementMode:
            case R.id.textViewMeasurementMode:
                sensor.changeMeasurementMode();//меняем моду измерения
                break;
            case R.id.imageButtonMelody:
                Log.v(TAG,"imageButtonMelody");
                InputBox.pickRingtone(ACTIVITY_SETTING_URL_MELODI, "  BB4"
                        ,sensor.endMeasurementNotification.melody,TAG, this);
                //pickRingtone();
                break;
            case R.id.switchVibration:
                Log.v(TAG,"imageButtonVibration");
            //    sensor.onEndVibration = ((Switch)findViewById(R.id.switchVibration))
            //            .isChecked();//SwitchButton
                sensor.endMeasurementNotification.switchVibration = ((SwitchButton)findViewById(R.id.switchVibration))
                        .isChecked();//SwitchButton
                if(sensor.endMeasurementNotification.switchVibration)Util.playerVibrator(300);
                break;
            case R.id.imageButtonTemperaturesAbove:
                Log.v(TAG,"imageButtonTemperaturesAbove");
                // Util.playerVibrator(400,this);

                intent = new Intent(this, SettingMinMax.class);
                intent.putExtra(Util.EXTRAS_ITEM, mItem);
                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BC1    Max");
                // все изменения будет писать сразу в сенсор
                //указали флаг установки максимума, все остальное делется НАПРЯМУЮ с данными
                intent.putExtra(SettingMinMax.EXTRAS_MAX, SettingMinMax.EXTRAS_MAX);
                startActivityForResult(intent,ACTIVITY_SETTING_MAX);
                break;
            case R.id.imageButtonTemperaturesBelow:
                Log.v(TAG,"imageButtonTemperaturesBelow");
                intent = new Intent(this, SettingMinMax.class);
                intent.putExtra(Util.EXTRAS_ITEM, mItem);
                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BC2 Min");
                // все изменения будет писать сразу в сенсор
                // по умолчанию устанавливаем минимум, все остальное делется НАПРЯМУЮ с данными
                startActivityForResult(intent,ACTIVITY_SETTING_MIN);

//              Util.playerRingtone(0f, sensor.endMelody , this,TAG);
//              Util.playerRingtone(0f, (Uri) null , this,TAG);
                break;
            case R.id.imageButtonDecor:
                Log.v(TAG,"imageButtonDecor");
                //----НАСТРОЙКА И ЗАПУСК окна ввода ЧИСЛА -----------
                intent = new Intent(this, SettingFon.class);
                intent.putExtra(Util.EXTRAS_BAR_TITLE, "   BA1");
                startActivityForResult(intent,ACTIVITY_SETTING_DECOR);
//                  if(sensor != null){
//                    Util.playerRingtone(1f, sensor.endMelody , this,TAG);
//                } else Util.playerRingtone(1f, (Uri)null , this,TAG);
                break;
            default:
        }
        updateTextString();
        return;
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
