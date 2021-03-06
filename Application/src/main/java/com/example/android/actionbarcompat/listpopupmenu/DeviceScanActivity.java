package com.example.android.actionbarcompat.listpopupmenu;

/**
 * Created by lesa on 12.12.2016.
 */

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static java.security.AccessController.getContext;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {//AppCompatActivity {//Activity {//ListActivity {
    private final static String TAG = DeviceScanActivity.class.getSimpleName();
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 3 seconds.
    private static final long SCAN_PERIOD = 10000;
    private  String mDeviceNnameFiltr;
      private Sensor sensor;
    private  int mItem= 0;
    private  RunDataHub app;
    @Override// Set up the {@link android.app.ActionBar}, if the API is available.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //урали вверху системный бар
        // работает отлично! один раз объевил, работает пока окно не умрет!
        Util.setFullscreen(this);// работает отлично! один раз объевил, работает пока окно не умрет!
        Log.e(TAG,"START onCreate--");
        //--------------------------
        final Intent intent = getIntent();
        mItem = intent.getIntExtra(MainActivityWork.EXTRAS_DEVICE_ITEM,0);
        //фильтр АДВАНСИНГ пакетов при поиске блутуз устройст
        mDeviceNnameFiltr = intent.getStringExtra(MainActivityWork.EXTRAS_DEVICE_NAME_FILTR);
        app = ((RunDataHub) getApplicationContext());
        if((app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)
                || (app.mBluetoothLeServiceM.arraySensors.size() <= 0)){
            finish();
            Log.e(TAG,"ERROR -- No sensor item= " + mItem);
        }
        sensor = app.mBluetoothLeServiceM.arraySensors.get(mItem);
        mBluetoothAdapter = app.mBluetoothLeServiceM.mBluetoothAdapter;
        Util.setActionBar(getActionBar(),TAG, intent.getStringExtra(Util.EXTRAS_BAR_TITLE));//"  BB3"
        //74:DA:EA:9F:4C:21-new
        mHandler = new Handler();
        //-------------------------------------------
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        //если блутуз не существует то и включать нечего!
        if(!app.mBluetoothLeServiceM.isBluetoothAdapterExist()) finish();//выходим
        //вызываем окно включения блутуз модуля

        if (!app.mBluetoothLeServiceM.mBluetoothAdapter.isEnabled()) {
            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    //вызывается при построениии и после вызова метода invalidateOptionsMenu();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
 mLeDeviceListAdapter.notifyDataSetChanged();
mLeDeviceListAdapter.notifyDataSetInvalidated();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case android.R.id.home://ккнопка ДОМОЙ!!
               // Toast.makeText(this,"Go to home", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, new Intent());
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        //проверяем разрешение на локацию-разрешено- можно делать поиск
        // если нет, пока пропускаем это шаг, выводим активити и
        // при запуске сканирования, запрашиваем на ЛОКАЦИЮ
        if(ContextCompat.checkSelfPermission(getApplicationContext()
                ,Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED){
            // разрешения ЕСТЬ, разрешаем сканирование
            scanLeDevice(true);
        }
    }

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK) {
                scanLeDevice(true);
            } else {
                Log.e(TAG,"--REQUEST_ENABLE_BT= Off,  finish()");
                finish();
            }
            return;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        mLeDeviceListAdapter.notifyDataSetChanged();
        mLeDeviceListAdapter.notifyDataSetInvalidated();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override//для лтист вюевера это короткий Клик, запускаем выбранное устройство на подключение
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
//       final Intent intent = new Intent(this, DeviceControlActivityNew.class);
//        intent.putExtra(DeviceControlActivityNew.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivityNew.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        Intent intent = new Intent();

        intent.putExtra(MainActivityWork.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(MainActivityWork.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_OK, intent);

        if (mScanning) {
            mScanning = false;

            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            app.mBluetoothLeServiceM.queueStoptLeScan(mLeScanCallback);
        }
        ///-- теперь подключаем ЗДЕСЬ!!..
        if(sensor != null){
            sensor.setNewAdressNameDevice(device.getAddress(), device.getName());
            //
            if((app.mBluetoothLeServiceM != null) && (sensor.mBluetoothDeviceAddress != null)){
////2017.01.23 ПРИНЯТО, название Термомето и Номер индекса по умолчанию ;                    // ПО умолчанию к имени уустройства дописываем 2 последние цифры адреса
//                    if(sensor.deviceLabel.compareTo(sensor.deviceLabelStringDefault) == 0){
//                        int i = sensor.mBluetoothDeviceAddress.length();
//                        //берем 2 символа последних из адреса устройства (16 адрес блутуз)
//                        sensor.deviceLabel = sensor.deviceLabelStringDefault + " "
//                                + sensor.mBluetoothDeviceAddress.substring(i-2);
//                    }
                //это запуск напрямую, работает хреново
               // app.mBluetoothLeServiceM.connect(sensor.mBluetoothDeviceAddress,true);
                //запуск на коннект через очередь!
                //начинаем конект ВСЕГДА с чистого ЛИСТА, тоесть настроек связи (BluetoothGatt)
                if(sensor.mBluetoothGatt == null)app.mBluetoothLeServiceM.queueSetConnect(sensor);
                else app.mBluetoothLeServiceM.queueSetDisconnectCloseConnect(sensor);
                Log.v(TAG,"sensor item= " + mItem + "  connectAdress= " + sensor.mBluetoothDeviceAddress);
            }
        }
        finish();
    }
    private final int REQUEST_PERMISSION_REQ_CODE = 8973;
    //сюда прилетает ответ по разрешению на локацию, если ДА повторяем запрос на поиск, если нет то СТОП сканирование
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //Log.v(TAG,"--- PERMISSION --- = " + permissions[0]);
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE:
                if (grantResults == null || grantResults.length <= 0 || grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
                    // Snackbar.make(getView(), (int) R.string.rationale_location_permission_denied, Snackbar.LENGTH_LONG).show();
                    Log.e(TAG,"--- PERMISSION_GRANTED NO");
                    scanLeDevice(false);
                } else {
                    Log.i(TAG,"--- PERMISSION_GRANTED OK");
                    scanLeDevice(true);
                }
                break;
            default:
        }
    }


    private void scanLeDevice(final boolean enable) {
        if(enable){
            //если сканировать, проверяем разрешение на ЛОКАЦИЮ- АПИ23, если его нет,
            // выбрасываем окно с запросом, при ответе НЕТ, ствим сканирование стоп иждем что дальше
            int i = ContextCompat.checkSelfPermission(getApplicationContext()
                    ,Manifest.permission.ACCESS_COARSE_LOCATION);
            Log.i(TAG,"--- LOCATION_SERVICE -- i= " + i + "  PERMISSION_GRANTED= "+ PermissionChecker.PERMISSION_GRANTED);
            if(i != PermissionChecker.PERMISSION_GRANTED){
                // разрешения нет, запрашиваем у пользователя
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
                }
                return;
            }
        }
        // разрешение есть, начинаем сканирование -------------------------
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning) {
                        mScanning = false;
                        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        app.mBluetoothLeServiceM.queueStoptLeScan(mLeScanCallback);
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            app.mBluetoothLeServiceM.queueStartLeScan(mLeScanCallback);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
//            }else {
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
//            }
        } else {
            if(mScanning) {
                mScanning = false;
                // mBluetoothAdapter.getScanMode()
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                app.mBluetoothLeServiceM.queueStoptLeScan(mLeScanCallback);
            }
        }
        //декларирует что Меню ИМЕНИЛОСЬ!!*? чтоб заново его прорисовали!!?? класс активити
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        private Sensor getBluetoothDevice(final String adr, ArrayList<Sensor> mbleDot){
            for(Sensor sensor: mbleDot){
                if(sensor.mBluetoothDeviceAddress == null) continue;
                if(adr.compareTo(sensor.mBluetoothDeviceAddress) == 0)return sensor;
            }
            return null;
        }
        // поиск устройст с СТРОКОЙ В РЕКЛАМЕ которая является фильтром
        public void addDevice(BluetoothDevice device) {
            // TODO: 17.12.2016 контроль всех адресов ЧТО есть, вывод внизу с ЗАТЕМНЕНИЕМ, которые НЕ прошли по филтру 

            RunDataHub app = ((RunDataHub) getApplicationContext());
            if((app == null) || (device == null))  return;
            if(app.mBluetoothLeServiceM == null) return;
            if(app.mBluetoothLeServiceM.arraySensors == null)return;
            //ОКАЗЫВАЕТСЯ прилетает че попало и при фильтрации возникают ошибки!!когда мы берем подстроку длиннее чем есть!
            if((device.getName() == null) || (device.getName().length() < 2))return;

            //фильтр АДВАНСИНГ пакетов при поиске блутуз устройст
            if((mDeviceNnameFiltr != null) && (mDeviceNnameFiltr.length() > 0)){
                //устройство можно сравнивать, если длинна фильта по имени не больше !
                if(device.getName().length() >= mDeviceNnameFiltr.length()){
                    //Фильтр указан, сравниваем ПЕРВЫЕ символы В РЕКЛАМНЕ блутуз устройства
                    String str = device.getName().substring(0, mDeviceNnameFiltr.length());//берем Часть и РЕКЛАМНОГО сообщения, равной размеру ФИЛЬТРА
                    if(!str.equals(mDeviceNnameFiltr)){
                        Log.e(TAG, "ПОИСК ищем= \"" +mDeviceNnameFiltr + "\"   Найден= \""+ device.getName()+"\"");
                        return;
                    }
                } else {
                    Log.e(TAG, "ПОИСК ищем= \"" +mDeviceNnameFiltr + "\"   Найден= \""+ device.getName()+"\"");
                    return;
                }
            }
            //смотрим, есть ли у нас уже зарегестрированный такой адрес!!
            if(getBluetoothDevice(device.getAddress(),
                    app.mBluetoothLeServiceM.arraySensors) != null) {
                Log.e(TAG, "ПОИСК- НАЙден зарегестрированный УЖЕ термометр!!");
                return;
            }
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {

            mLeDevices.clear();
 mLeDeviceListAdapter.notifyDataSetChanged();
mLeDeviceListAdapter.notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            //  if((mBluetoothLeService == null) || (mBluetoothLeService.arraySensors == null))return 0;
            //   return mBluetoothLeService.arraySensors.size();
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            //  return mBluetoothLeService.arraySensors.get(i);
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            //если первое построение, В view.setTag(viewHolder) - сохранили объект с данными который отображать
            if (view == null) {
                // if((i & 1) != 0) view = mInflator.inflate(R.layout.listitem_device2, null);
                // else
                view =     mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                // если обновление то он уже там
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            //
            viewHolder.deviceAddress.setText(device.getAddress());
            //----------------------------------------
//            Sensor device = mBluetoothLeService.arraySensors.get(i);
//            final String deviceName = device.getName();
//            if (deviceName != null && deviceName.length() > 0)
//                viewHolder.deviceName.setText(deviceName);
//            else
//                viewHolder.deviceName.setText(R.string.unknown_device);
//            //
//            viewHolder.deviceAddress.setText(device.getAddress());
            //--------------------------------------
            return view;
        }
    }

//    // Device scan callback.
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private android.bluetooth.le.ScanCallback mleScanCallback =
//            new android.bluetooth.le.ScanCallback() {
//
//            };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(BluetoothDevice device_, int rssi, byte[] scanRecord) {
                    final int mrssi = rssi;
                    final byte[] mscanRecord = scanRecord;
                    final BluetoothDevice device = device_;

                    Log.v(TAG, " --- LeScanCallback --- device= "+(device_==null?"null":device_.getAddress()) + "   Rssi= " + mrssi
                            + "   scanRecord= " + (mscanRecord==null?"null":mscanRecord));

                    if(device == null) return;//иногда прилетает непогятно что!!контролтируем!!

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w(TAG,"FIND device= " + device);
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                            mLeDeviceListAdapter.notifyDataSetInvalidated();
                        }
                    });
                }
            };

    //заснуть на млсек
    private  void sleep(long mls) {
        Log.d(TAG, " Sleep");
        try {
            Thread.sleep(mls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }    //заснуть на млсек
    //
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}