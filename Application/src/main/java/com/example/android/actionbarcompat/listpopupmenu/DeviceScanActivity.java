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
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import java.util.ArrayList;

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
    @Override// Set up the {@link android.app.ActionBar}, if the API is available.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"START onCreate--");
        //урали вверху системный бар
        if(getListView() != null ) getListView().getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //--------------------------
        final Intent intent = getIntent();
        mItem = intent.getIntExtra(MainActivityWork.EXTRAS_DEVICE_ITEM,0);
        //фильтр АДВАНСИНГ пакетов при поиске блутуз устройст
        mDeviceNnameFiltr = intent.getStringExtra(MainActivityWork.EXTRAS_DEVICE_NAME_FILTR);
        RunDataHub app = ((RunDataHub) getApplicationContext());
        if((app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)
                || (app.mBluetoothLeServiceM.arraySensors.size() <= 0)){
            finish();
            Log.e(TAG,"ERROR -- No sensor item= " + mItem);
        }
        sensor = app.mBluetoothLeServiceM.arraySensors.get(mItem);
        Util.setActionBar(getActionBar(),TAG, intent.getStringExtra(Util.EXTRAS_BAR_TITLE));//"  BB3"
        //-------------------------------------------
        //74:DA:EA:9F:4C:21-new
        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }
       ////--------

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

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();

        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);

    }

    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
//        mLeDeviceListAdapter.clear();
// mLeDeviceListAdapter.notifyDataSetChanged();
// mLeDeviceListAdapter.notifyDataSetInvalidated();
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
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        ///-- теперь подключаем ЗДЕСЬ!!..
        if(sensor != null){
                // TODO: 19.12.2016 может реализовать запрос на поиск через очередь?
                // со старым устройством //если был коннект- отключаем нафиг
                if((sensor.mBluetoothDeviceAddress == null)
                        || (sensor.mBluetoothDeviceAddress.length() < 15)){//длинна адреса 17
                    if(sensor.mBluetoothGatt != null)sensor.close();
                    //сбрасываем все строковые значения которые касаются модели и номеров прошивок
                    //все считываемпотом заново!
                    sensor.softwareRevision = null;
                    sensor.firmwareRevision = null;
                    sensor.hardwareRevision = null;
                    sensor.serialNumber = null;
                    sensor.modelNumber = null;
                    sensor.manufacturerName = null;
                }
                sensor.mBluetoothDeviceAddress = device.getAddress();
                sensor.mDeviceName = device.getName();
                //
                RunDataHub app = ((RunDataHub) getApplicationContext());
                if((app.mBluetoothLeServiceM != null) && (sensor.mBluetoothDeviceAddress != null)){
                    // ПО умолчанию к имени уустройства дописываем 2 последние цифры адреса
                    if(sensor.deviceLabel.compareTo(sensor.deviceLabelStringDefault) == 0){
                        int i = sensor.mBluetoothDeviceAddress.length();
                        //берем 2 символа последних из адреса устройства (16 адрес блутуз)
                        sensor.deviceLabel = sensor.deviceLabelStringDefault + " "
                                + sensor.mBluetoothDeviceAddress.substring(i-2);
                    }
                    //это запуск напрямую, работает хреново
                   // app.mBluetoothLeServiceM.connect(sensor.mBluetoothDeviceAddress,true);
                    //запуск на коннект через очередь!
                    app.mBluetoothLeServiceM.queueSetConnect(sensor);
                    Log.v(TAG,"sensor item= " + mItem + "  connectAdress= " + sensor.mBluetoothDeviceAddress);
                }
        }
        finish();
       // startActivity(intent);//на подклшючение к устройству
    }

//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_REQ_CODE:
//                if (grantResults == null || grantResults.length <= 0 || grantResults[0] != PERMISSION_GRANTED) {
//                    Snackbar.make(getView(), (int) R.string.rationale_location_permission_denied, Snackbar.LENGTH_LONG).show();
//                } else {
//                    //startScan();
//                    scanLeDevice(true);
//                }
//                break;
//            default:
//        }
//    }
    private void scanLeDevice(final boolean enable) {
//        //проверяем а разрешения доступа сканирования ДОЛЖНО БАТЬ РАЗРЕШЕНИЕ НА ЛОКАЦИЮ
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
//            // разрешения нет, запрашиваем у пользователя
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
//            return;
//        }
        // разрешение есть, начинаем сканирование
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            if(mScanning) {
                mScanning = false;
                // mBluetoothAdapter.getScanMode()
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
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