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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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

import com.portfolio.alexey.connector.Util;

import java.util.ArrayList;
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
    private static final long SCAN_PERIOD = 3000;

    @Override// Set up the {@link android.app.ActionBar}, if the API is available.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"START onCreate--");
        //74:DA:EA:9F:4C:21-new
        Util.setActionBar(getActionBar(),TAG, "  BB3");
//        ActionBar actionBar = getActionBar();//getSupportActionBar();??--это решалось в другом методе(getDelegate().getSupportActionBar();)
//        if (actionBar != null) {
//            actionBar.setTitle(R.string.title_devices);//getActionBar().setTitle(R.string.title_devices);
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            //вместо ЗНачка по умолчанию, назначаемого выше, подставляет свой
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
//            //------------------------------
//            //  actionBar.setHomeButtonEnabled(true); //устанавливает надпись и иконку как кнопку домой(не требуется метод - actionBar.setDisplayHomeAsUpEnabled(true);)
//            //--- все ниже както не работет или для другого предназаначена
//            //actionBar.setIcon(null);
//            //actionBar.setCustomView(null);
//            //actionBar.setDisplayUseLogoEnabled(false);
//            //actionBar.setLogo(null);
//        }
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
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //урали вверху системный бар
        if(getListView() != null ) getListView().getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        mLeDeviceListAdapter.clear();
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

        intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_OK, intent);

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
            sleep(1000);//для того чтоб закончилося останов поиска окончательно
        }
        finish();
       // startActivity(intent);//на подклшючение к устройству
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
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

        public void addDevice(BluetoothDevice device) {
            // TODO: 17.12.2016 контроль всех адресов ЧТО есть, вывод внизу с ЗАТЕМНЕНИЕМ, которые НЕ прошли по филтру 
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            //  if((mBluetoothLeService == null) || (mBluetoothLeService.mbleDot == null))return 0;
            //   return mBluetoothLeService.mbleDot.size();
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            //  return mBluetoothLeService.mbleDot.get(i);
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
//            Sensor device = mBluetoothLeService.mbleDot.get(i);
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
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    final int mrssi = rssi;
                    final byte[] mscanRecord = scanRecord;
                    Log.v("NAIN", "Rssi= " + mrssi + "   scanRecord= " + mscanRecord);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w(TAG,"FIND device= " + device);
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
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