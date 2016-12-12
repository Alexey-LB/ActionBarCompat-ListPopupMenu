package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeServiceNew extends Service {
    private final static String TAG = BluetoothLeServiceNew.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    // private String mBluetoothDeviceAddress;

    // private BluetoothGatt mBluetoothGatt;
    //private int mConnectionState = STATE_DISCONNECTED;

    public  static final int STATE_DISCONNECTED = 0;
    public  static final int STATE_CONNECTING = 1;
    public  static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_ADRESS =
            "com.example.bluetooth.le.EXTRA_ADRESS";

    public ArrayList<Sensor> mbleDot = new ArrayList<Sensor>();
    //
    //-----------------------------------
//    //
//    private void contrDisconnect(BluetoothGatt gatt){
//        if((mbleDot.mConnectionState == STATE_DISCONNECTING) || (mbleDot.mConnectionState == STATE_DISCONNECTED)){
//            //В зависмости от СОТОЯНИЯ: КОННЕКТ - вызов disconnect(), ЕСЛИ ДИСКОННЕКТ- вызов close()
//            onCloseConnect(gatt);//ЕСЛИ КОННЕКТ-отключаемся, ЕСЛИ ДИСКОННЕНКТ-закрываем соединение//
//            return;
//        }
//    }

//    // проверяем КОЛБАК на то что он НАШ!! а не чужой// ЕСЛИ МЫ не одни, а прилетает ВСЕМ!!!берем только свое!!!
//    private boolean onlyMayGattAdr(BluetoothGatt gatt){
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if(mbleDot.mBluetoothGatt != null){
//            if(mbleDot.mBluetoothGatt.equals(gatt)) return true;
//            Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + mbleDot.mBluetoothGatt.toString() + " = " + gatt.toString());
//        }
//        //проверка адреса
//        if(isMayAdr(gatt)){//это наш адрес, но не наш Gatt! по этому закрываем это
//            //onCloseConnect(gatt);//ЕСЛИ КОННЕКТ-отключаемся, ЕСЛИ ДИСКОННЕНКТ-закрываем соединение//
//            setStateConnection(STATE_DISCONNECTING);
//            Log.e(TAG, "isMayGatt: isMayADRESS, No isMayGatt > Close");
//            return true;
//        }
//        return false;
//    }
    // проверяем КОЛБАК на то что он НАШ!! а не чужой//если это один и тот же адрес на моем же устройстве!!
//    private boolean isMayAdr(BluetoothGatt gatt){
//        if(gatt.getDevice().getAddress().compareTo(mbleDot.mBluetoothDeviceAddress) == 0)return true;
//        Log.e(TAG, "isMayGatt: devADRESS != mayADRESS");
//        return false;
//    }
//    private boolean isMayGatt(BluetoothGatt gatt){
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if((mbleDot.mBluetoothGatt != null) && (mbleDot.mBluetoothGatt.equals(gatt))) return true;
//        Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + mbleDot.mBluetoothGatt.toString() + " = " + gatt.toString());
//        //проверка адреса
//        return false;
//    }
//    // проверяем КОЛБАК на то что он НАШ!! а не чужой// ЕСЛИ МЫ не одни, а прилетает ВСЕМ!!!берем только свое!!!
//    private boolean isMayAdrGatt(BluetoothGatt gatt){
//        //проверка адреса
//        if(!isMayAdr(gatt)) return false;
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if(isMayGatt(gatt))return true;
//        Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + mbleDot.mBluetoothGatt.toString() + " = " + gatt.toString());
//        return false;
//    }

    /**
     *
     * @param adr
     * @return
     */
    private Sensor getBluetoothDevice(final String adr){
        for(Sensor sensor: mbleDot){
            if(sensor.mBluetoothDeviceAddress == null) continue;
            if(adr.compareTo(sensor.mBluetoothDeviceAddress) == 0)return sensor;
        }
        return null;
    }
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            //
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                sensor.mConnectionState = STATE_CONNECTED;

                broadcastUpdate(intentAction, sensor);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.v(TAG, "Attempting to start service discovery:" +
                        sensor.mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                sensor.mConnectionState = STATE_DISCONNECTED;
                Log.w(TAG, "Disconnected from GATT server.");

                broadcastUpdate(intentAction, sensor);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            //
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, sensor);
                sensor.enableTXNotification();
                sensor.loop_rssi = 0;
                Log.i(TAG, "onServicesDiscovered == BluetoothGatt.GATT_SUCCESS");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead--------------------");
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            //
            sensor.onCharacteristicRead();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
                Log.i(TAG, "onCharacteristicRead");
                sensor.setValue(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            sensor.setValue(characteristic);
            // на каждый 16 запрашиваем RSSI (запрос каждые примерно 16 секунды)
            sensor.readRSSIandBatteryLevel();
            //
            broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
            Log.i(TAG, "onCharacteristicChanged");
            sensor.onCharacteristicRead();
        }
        //
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //если у нас есть такое устройство//входной контроль
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            //
            sensor.rssi = rssi;
            Log.i(TAG, "onReadRemoteRssi= " + rssi);
            //sensor.mBluetoothGatt.readRemoteRssi();

        }
    };

    private void broadcastUpdate(final String action,final Sensor sensor) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_ADRESS, sensor.mBluetoothDeviceAddress);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action,final Sensor sensor,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
// TODO: 09.12.2016        intent.putExtra(EXTRA_DATA, PartGatt.getValue(characteristic));
        //    intent.putExtra(EXTRA_DATA, PartGatt.getValue(characteristic));
        intent.putExtra(EXTRA_DATA, sensor.getStringIntermediateValue(false,true));
        intent.putExtra(EXTRA_ADRESS, sensor.mBluetoothDeviceAddress);
        sendBroadcast(intent);
        return;
    }

    public class LocalBinder extends Binder {
        BluetoothLeServiceNew getService() {
            return BluetoothLeServiceNew.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
   synchronized public boolean initialize() {
//        // For API level 18 and above, get a reference to BluetoothAdapter through
//        // BluetoothManager.
//        if (mBluetoothManager == null) {
//            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            if (mBluetoothManager == null) {
//                Log.e(TAG, "Unable to initialize BluetoothManager.");
//                return false;
//            }
//        }
//
//        mBluetoothAdapter = mBluetoothManager.getAdapter();
//        if (mBluetoothAdapter == null) {
//            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
//            return false;
//        }
        Log.e(TAG,"Servise------init-- init--");
        return true;
    }
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mySettings";
    private SharedPreferences mSettings;
    @Override
    public void onCreate() {
        super.onCreate();//---------
        Log.e(TAG,"Service------START -- onCreate()--------------");
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Service--- Unable to initialize BluetoothManager.");
                return;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Service--- Unable to obtain a BluetoothAdapter.");
            return;
        }
        Log.e(TAG,"Service------get BluetoothAdapter = OK----");
        //---------------------
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // Читаем данные
        if(mSettings != null){
            int listSizeBluetooth = mSettings.getInt("listSizeBluetooth", 0);

            for(int i= 0; i < listSizeBluetooth;i++){
                String devAdr = mSettings.getString("item"+i, null);
                Log.v(TAG,"onCreate: get sensor from flash= "+i +"   devAdr= " +devAdr + "  size= "+listSizeBluetooth);
                if (devAdr != null) {
                    //читаем УСТРОЙСТВО в файле отдельном
                    SharedPreferences mSettingsDevace =
                            getSharedPreferences(devAdr, Context.MODE_PRIVATE);
                    Sensor sensor = new Sensor(mSettingsDevace);
                    mbleDot.add(sensor);
                    Log.i(TAG,"onCreate: get sensor from flash= "+i +"   adress= " +sensor.mBluetoothDeviceAddress);
                    //--запускаем на соннект
                    if((sensor.mBluetoothDeviceAddress != null) &&(sensor.mBluetoothDeviceAddress.length() == 17)){
                        // запускаем на соннект
                        connect(sensor.mBluetoothDeviceAddress, true);
                        Log.w(TAG,"connect sensor= "+i+ "  adress= " + sensor.mBluetoothDeviceAddress);
                    }
                }
            }
        }else{
            Log.e(TAG,"getSharedPreferences= null!");
        }
        //------------------------------------------------------
    }
    public void settingPutFile(){
        // Запоминаем данные
        if(mSettings != null){
            Sensor sensor;int i;SharedPreferences settingsDevace;String defName;
            // TODO: 09.12.2016 ОБЯЗАТЕЛЬНО ввести контроль изменения!! и толко при наличие изменений Записывать данные
            // это относится к списку устройств, поскольку устройства сами контролируют свои изменения!
            // в первом файле храним количство и адреса блутуз устройств
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("listSizeBluetooth", mbleDot.size());
            //Остальные файлы- имена ЭТО БЛУЗ АДРЕСА, в них все настройки
            for(i= 0; i < mbleDot.size(); i++){
                defName = "item"+i;
                //сохраняем УСТРОЙСТВО в файле отдельном
                sensor = mbleDot.get(i);
                // "74:DA:EA:9F:54:C9"= 17
                if((sensor.mBluetoothDeviceAddress != null)
                        && (sensor.mBluetoothDeviceAddress.length() == 17)){
                    //занесли в список устройст ЕГО АДРЕС под номером в листе
                    editor.putString(defName, mbleDot.get(i).mBluetoothDeviceAddress);
                    //
                    settingsDevace = getSharedPreferences(sensor.mBluetoothDeviceAddress, Context.MODE_PRIVATE);
                    Log.i(TAG,"SettingPutFile item= " +i+ "  name= " + sensor.mBluetoothDeviceAddress);
                }else {//имя ЗАПИСЫВАЕМОМУ файлу даем по умолчанию!!!
                    editor.putString(defName, defName);
                    //
                    settingsDevace = getSharedPreferences(defName, Context.MODE_PRIVATE);
                    Log.i(TAG,"SettingPutFile item= " +i+ "  name= " + defName);
                }
                SharedPreferences.Editor settingsDevaceEditor = settingsDevace.edit();
                sensor.putConfig(settingsDevaceEditor);
                settingsDevaceEditor.apply();
            }
            editor.apply();
            Log.i(TAG,"getSharedPreferences, Write OK, size= " + i);
        }else{
            Log.e(TAG,"getSharedPreferences= null!");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // Запоминаем данные
        settingPutFile();
        //-------
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        Log.e(TAG,"----SERVICE ---onDestroy----");
        //------------------------------------------------------
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    synchronized public boolean connect(final String address, boolean avtoConnect) {
        // "74:DA:EA:9F:54:C9"= 17
        if (mBluetoothAdapter == null || address == null || address.length() != 17) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        //если у нас есть такое устройство
        final Sensor sensor;
        if(getBluetoothDevice(address) == null) {
            sensor = new Sensor(address);
            mbleDot.add(sensor);
            Log.w(TAG, " connect: NEW sensor");
        }else{
            sensor = getBluetoothDevice(address);
            Log.w(TAG, " connect: OLd sensor (get from setting file)");
        }
        if(sensor.goToConnect == true) {
            Log.w(TAG, " connect: sensor GoTo connect Old");
        }
        // Previously connected device.  Try to reconnect.
        if (sensor.mBluetoothDeviceAddress != null && address.equals(sensor.mBluetoothDeviceAddress)
                && sensor.mBluetoothGatt != null) {
            Log.w(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (sensor.mBluetoothGatt.connect()) {
                sensor.mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        // mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        sensor.mBluetoothGatt = device.connectGatt(this, avtoConnect, mGattCallback);
        Log.v(TAG, "Trying to create a new connection.");
        sensor.mBluetoothDeviceAddress = address;
        sensor.mConnectionState = STATE_CONNECTING;
        sensor.goToConnect = true;//указали, что коннект заказан!
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    //дисконнект для всех!
    public void disconnect() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for(Sensor sensor: mbleDot) sensor.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        for(Sensor sensor: mbleDot) sensor.close();
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(final BluetoothGattCharacteristic characteristic,final String bluetoothAdress) {
        final Sensor sensor = getBluetoothDevice(bluetoothAdress);
        if (mBluetoothAdapter == null || sensor.mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        sensor.mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(final BluetoothGattCharacteristic characteristic,
                                              final boolean enabled,final String bluetoothAdress) {
        final Sensor sensor = getBluetoothDevice(bluetoothAdress);
        if (mBluetoothAdapter == null || sensor.mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }

        sensor.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (PartGatt.UUID_INTERMEDIATE_TEMPERATURE.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptor(PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            sensor.mBluetoothGatt.writeDescriptor(descriptor);
        }

//        // This is specific to Heart Rate Measurement.
//        if (PartGatt.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }

//        if (UUID_TEMPERATURE_MEASUREMENT.equals(characteristic.getUuid())) {
//
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices(final String bluetoothAdress) {
        final Sensor sensor = getBluetoothDevice(bluetoothAdress);

        if ((sensor == null) || (sensor.mBluetoothGatt == null)) return null;

        return sensor.mBluetoothGatt.getServices();
    }
}
