package com.portfolio.alexey.connector;

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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.android.actionbarcompat.listpopupmenu.RunDataHub;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

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

    public ArrayList<Sensor> arraySensors = new ArrayList<Sensor>();//arraySensors
    //
    //-----------------------------------
//    //
//    private void contrDisconnect(BluetoothGatt gatt){
//        if((arraySensors.mConnectionState == STATE_DISCONNECTING) || (arraySensors.mConnectionState == STATE_DISCONNECTED)){
//            //В зависмости от СОТОЯНИЯ: КОННЕКТ - вызов disconnect(), ЕСЛИ ДИСКОННЕКТ- вызов close()
//            onCloseConnect(gatt);//ЕСЛИ КОННЕКТ-отключаемся, ЕСЛИ ДИСКОННЕНКТ-закрываем соединение//
//            return;
//        }
//    }

//    // проверяем КОЛБАК на то что он НАШ!! а не чужой// ЕСЛИ МЫ не одни, а прилетает ВСЕМ!!!берем только свое!!!
//    private boolean onlyMayGattAdr(BluetoothGatt gatt){
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if(arraySensors.mBluetoothGatt != null){
//            if(arraySensors.mBluetoothGatt.equals(gatt)) return true;
//            Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + arraySensors.mBluetoothGatt.toString() + " = " + gatt.toString());
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
//        if(gatt.getDevice().getAddress().compareTo(arraySensors.mBluetoothDeviceAddress) == 0)return true;
//        Log.e(TAG, "isMayGatt: devADRESS != mayADRESS");
//        return false;
//    }
//    private boolean isMayGatt(BluetoothGatt gatt){
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if((arraySensors.mBluetoothGatt != null) && (arraySensors.mBluetoothGatt.equals(gatt))) return true;
//        Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + arraySensors.mBluetoothGatt.toString() + " = " + gatt.toString());
//        //проверка адреса
//        return false;
//    }
//    // проверяем КОЛБАК на то что он НАШ!! а не чужой// ЕСЛИ МЫ не одни, а прилетает ВСЕМ!!!берем только свое!!!
//    private boolean isMayAdrGatt(BluetoothGatt gatt){
//        //проверка адреса
//        if(!isMayAdr(gatt)) return false;
//        //поскольку открывали сами, значит и адрес должен совпадать!
//        if(isMayGatt(gatt))return true;
//        Log.e(TAG, "isMayGatt: mBluetoothGatt != gatt" + arraySensors.mBluetoothGatt.toString() + " = " + gatt.toString());
//        return false;
//    }

    /**
     *
     * @param adr поиск сенсора с указанным адресом
     * @return
     */
    private Sensor getBluetoothDevice(final String adr){
        for(Sensor sensor: arraySensors){
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
                // состояние промежуточное-МЫ подключились- но ЕЩЕ НЕ СЧИТАЛИ СЕРВИСЫ доступные на этом устройстве
                sensor.mConnectionState = STATE_CONNECTING;
                //для отображения состояния подключения волны расходятся от значка--
                sensor.rssi = STATE_CONNECTING;//показываем что коннектимся
//Запускаем считывание СЕРВИСОВ и характеристик (discovery)
 // broadcastUpdate(intentAction, sensor);
                // Attempts to discover services after successful connection.
                boolean ds = sensor.mBluetoothGatt.discoverServices();

                Log.v(TAG, "Attempting to start service discovery:" + ds
                        +"  adress= "+sensor.mBluetoothDeviceAddress);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // просто отключение-оБлом
                sensor.mConnectionState = STATE_DISCONNECTED;
                Log.w(TAG, "Disconnected from GATT server   adress= " + sensor.mBluetoothDeviceAddress);

                sensor.rssi = STATE_DISCONNECTED;//показываем что отключились
   //             intentAction = ACTION_GATT_DISCONNECTED;
  // broadcastUpdate(intentAction, sensor);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;

            //
            if (status == BluetoothGatt.GATT_SUCCESS) {
 // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, sensor);
                // закончили ЧТЕНИЕ СЕРВИСОВ и характеристик, готовы к работе
                sensor.rssi = STATE_CONNECTED;//показываем что готовы к работе
                sensor.mConnectionState = STATE_CONNECTED;
 //sensor.enableTXNotification();
 //setNotificationIndication
 sensor.writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, PartGatt.UUID_HEALTH_THERMOMETER
                        , PartGatt.UUID_INTERMEDIATE_TEMPERATURE, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);
                Log.w(TAG, "onServicesDiscovered == GATT_SUCCESS  adress= " + sensor.mBluetoothDeviceAddress);
            } else {
                Log.e(TAG, "onServicesDiscovered ERROR status: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
           // Log.w(TAG, "onCharacteristicRead--------------------");

            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
            //

            if (status == BluetoothGatt.GATT_SUCCESS) {
 // broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
            //    Log.i(TAG, "   adress= " + sensor.mBluetoothDeviceAddress);

      sensor.setValue(characteristic, false);
                sensor.onCharacteristicRead();//постоянно запрашивает характеристику- и обламывает остальное!!
                Log.w(TAG, "-RCr--onCharacteristicRead gatt= " + gatt.getDevice().getAddress()+"   status= " + status
                        + "  characteristic= " +  Util.getUidStringMost16Bits(characteristic)
                        +"  service= " + Util.getUidStringMost16Bits(characteristic.getService()));//characteristic.getUuid().toString());
            }else{
                Log.e(TAG, "-RCr--onCharacteristicRead ERROR gatt= " + gatt.getDevice().getAddress()+"   status= " + status
                        + "  characteristic= " +  Util.getUidStringMost16Bits(characteristic)
                        +"  service= " + Util.getUidStringMost16Bits(characteristic.getService()));//characteristic.getUuid().toString());

            }
            // Ready for next transmission
            processTxQueue();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
       //     Log.v(TAG, "onCharacteristicChanged--------------------");
            //если у нас есть такое устройство

            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if(sensor == null) return;
    sensor.goToConnect = false;//подключение закончилочсь УДАЧНО!!
            sensor.setValue(characteristic, false);

            //постоянно запрашивает характеристику- и обламывает остальное!! убираем
            //       sensor.onCharacteristicRead();
            // на каждый 16 запрашиваем RSSI (запрос каждые примерно 16 секунды)

    if(sensor.readRSSIandBatteryLevel() == false){
        sensor.onCharacteristicRead();
    }

        //ЗАПРАШИВАТЬ (или записыват) ЗА 1 РАЗ можно только 1 характеристику
        // или свойства - иначе НЕ отвечает
            //
 // broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
     //       Log.i(TAG, "   adress= " + sensor.mBluetoothDeviceAddress);
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
          //  Log.i(TAG, "onReadRemoteRssi= " + rssi);
            //sensor.mBluetoothGatt.readRemoteRssi();

        }
        //.. https://gist.github.com/SoulAuctioneer/ee4cb9bc0b3785bbdd51 -- пример
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {


          //  Log.w(TAG, "----------------------onCharacteristic_Write");
//            String deviceName = gatt.getDevice().getName();
//            String serviceName = BleNamesResolver.resolveServiceName(characteristic.getService().getUuid().toString().toLowerCase(Locale.getDefault()));
//            String charName = BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString().toLowerCase(Locale.getDefault()));
//            String description = "Device: " + deviceName + " Service: " + serviceName + " Characteristic: " + charName;
//
//            // Ready for next transmission
//            processTxQueue();
//
//            // we got response regarding our request to write new value to the characteristic
//            // let see if it failed or not
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "-WCr--onCharacteristicWrite gatt= " + gatt.getDevice().getAddress()+"   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        +"  service= " + Util.getUidStringMost16Bits(characteristic.getService()));
   //             mUiCallback.uiSuccessfulWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description);
            } else {
                Log.e(TAG, "-WCr--onCharacteristicWrite ERROR gatt= " + gatt.getDevice().getAddress()+"   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        +"  service= " + Util.getUidStringMost16Bits(characteristic.getService()));
                //             mUiCallback.uiFailedWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description + " STATUS = " + status);
            }
            // Ready for next transmission
            processTxQueue();
        }
        /**
         * Callback indicating the result of a descriptor write operation.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#writeDescriptor}
         * @param descriptor Descriptor that was written to the associated
         *                   remote device.
         * @param status The result of the write operation
         *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "-WDe--onDescriptorWrite gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  descriptor= " + Util.getUidStringMost16Bits(descriptor)
                        +"  Characteristic= " + Util.getUidStringMost16Bits(descriptor.getCharacteristic()));
            }else{
                Log.e(TAG, "-WDe--onDescriptorWrite ERROR gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  descriptor= " + Util.getUidStringMost16Bits(descriptor)
                        +"  Characteristic= " + Util.getUidStringMost16Bits(descriptor.getCharacteristic()));
            }
            // Ready for next transmission
            processTxQueue();
        }
    };
//==============--------------------------------------------------------------------
       /* An enqueueable write operation - notification subscription or characteristic write */
    private class TxQueueItem
    {
        Sensor sensor;
        BluetoothGattCharacteristic characteristic;
        byte[] dataToWrite; // Only used for characteristic write
        boolean enabled; // Only used for characteristic notification subscription
        public TxQueueItemType type;
        public int retry = 0;//количество повторов запроса
    }

    /**
     * The queue of pending transmissions
     */
    private Queue<TxQueueItem> txQueue = new LinkedList<TxQueueItem>();

    private boolean txQueueProcessing = false;

    private enum TxQueueItemType {
        ReadCharacteristic,
        WriteCharacteristic,
        SubscribeCharacteristic
    }

    /* queues enables/disables notification for characteristic */
    public void queueSetNotificationForCharacteristic(final Sensor sens, BluetoothGattCharacteristic ch, final byte[] dataToWrite, boolean enabled)
    {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.enabled = enabled;
        txQueueItem.dataToWrite = dataToWrite;
        txQueueItem.type = TxQueueItemType.SubscribeCharacteristic;
        addToTxQueue(txQueueItem);
    }

    /* queues enables/disables notification for characteristic */
    public void queueWriteDataToCharacteristic(final Sensor sens, final BluetoothGattCharacteristic ch, final byte[] dataToWrite)
    {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.dataToWrite = dataToWrite;
        txQueueItem.type = TxQueueItemType.WriteCharacteristic;
        addToTxQueue(txQueueItem);
    }

    /* request to fetch newest value stored on the remote device for particular characteristic */
    public void queueRequestCharacteristicValue(final Sensor sens, BluetoothGattCharacteristic ch) {

        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.type = TxQueueItemType.ReadCharacteristic;
        addToTxQueue(txQueueItem);
    }

    /**
     * Add a transaction item to transaction queue
     * @param txQueueItem
     */
    private void addToTxQueue(TxQueueItem txQueueItem) {

        txQueue.add(txQueueItem);

        // If there is no other transmission processing, go do this one!
        if (!txQueueProcessing) {
            processTxQueue();
        }
    }
    private boolean errorTxQueue = false;
    private Handler mHandlerTxQueue = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            errorTxQueue = true;
            processTxQueue();
            errorTxQueue = false;
        }
    };
    /**
     * Call when a transaction has been completed.
     * Will process next transaction if queued
     */
    TxQueueItem mTxQueueItem;
    private void processTxQueue()
    {
        if (txQueue.size() <= 0)  {
            txQueueProcessing = false;
            return;
        }
        txQueueProcessing = true;//заблокировались
        //-----------------------------
        // продолжаем работать, убираем колбак и заново запускаем его для новой передачи
        //необходимо всегда УДАЛЯТЬ старые запуски, иначе ОНИ НАЧИНАЮТ ЖИТЬ ВСЕ ВМЕСТЕ!!!
        mHandlerTxQueue.removeCallbacks(runnable);
        //-- запускаем контроль запроса по времени, устангавливаем 10 секунд
        // если не уложились, то текущий запрос возвращяем в очередь и увеличиваем попытку
        // передачи, передаем 5 раз и облом! ему, запрашиваемому параметру
        if(errorTxQueue){
            Log.e(TAG,"processTxQueue ERROR mTxQueueItem.retry= "+ mTxQueueItem.retry
                    + "  sensor= "+mTxQueueItem.sensor.getAddress() + "   uid= "+mTxQueueItem.characteristic.getUuid());
            if(mTxQueueItem.retry++ <= 5)addToTxQueue(mTxQueueItem);//повтор запроса
        }
        //если очередь НЕ пуста, запускаем контроль по времени снова!
        if (txQueue.size() > 0)  {
            mHandlerTxQueue.postDelayed(runnable,10000);//-- запускаем контроль запроса по времени, устангавливаем 10 секунд
        }
        errorTxQueue = false;
        //-----------------------
        mTxQueueItem = txQueue.remove();
        switch (mTxQueueItem.type) {
            case WriteCharacteristic:
                writeDataToCharacteristic(mTxQueueItem.sensor, mTxQueueItem.characteristic
                        , mTxQueueItem.dataToWrite);
                break;
            case SubscribeCharacteristic:
                setNotificationForCharacteristic(mTxQueueItem.sensor,mTxQueueItem.characteristic,
                        mTxQueueItem.dataToWrite, mTxQueueItem.enabled);
                break;
            case ReadCharacteristic:
                requestCharacteristicValue(mTxQueueItem.sensor,mTxQueueItem.characteristic);
        }
    }
    /* set new value for particular characteristic */
    public void writeDataToCharacteristic(final Sensor sensor,final BluetoothGattCharacteristic ch, final byte[] dataToWrite)
    {
        if (mBluetoothAdapter == null || sensor.mBluetoothGatt == null || ch == null) return;
        // first set it locally....
        ch.setValue(dataToWrite);
        // ... and then "commit" changes to the peripheral
        sensor.mBluetoothGatt.writeCharacteristic(ch);
    }

    /* enables/disables notification for characteristic */
    // BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
    //BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
    //BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
    public void setNotificationForCharacteristic(final Sensor sensor,BluetoothGattCharacteristic ch,final  byte[] bluetoothGattDescriptorValue, boolean enabled)
    {
        if (mBluetoothAdapter == null || sensor.mBluetoothGatt == null) return;
        boolean success = sensor.mBluetoothGatt.setCharacteristicNotification(ch, enabled);
        if(!success) {
            Log.e("------", "Seting proper notification status for characteristic failed!");
        }
        // This is also sometimes required (e.g. for heart rate monitors) to enable notifications/indications
        // see: https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorViewer.aspx?u=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
        BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if(descriptor != null) {
//            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            //если сбросываем, то ЗНАЧЕНИЕ для сброса ОДИНАКОВО ДЛя нотификации и ИДИКАЦИИ ()
            byte[] val = enabled ? bluetoothGattDescriptorValue: BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            descriptor.setValue(val);
            sensor.mBluetoothGatt.writeDescriptor(descriptor);
        }
    }
    /* request to fetch newest value stored on the remote device for particular characteristic */
    public void requestCharacteristicValue(final Sensor sensor,BluetoothGattCharacteristic ch) {
        if (mBluetoothAdapter == null || sensor.mBluetoothGatt == null) return;

        sensor.mBluetoothGatt.readCharacteristic(ch);
        // new value available will be notified in Callback Object
    }
//===================================================================================================
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
   synchronized public boolean initialize() {
       //если мы прошли ХОТЬ 1 инициализацию- болше НЕ надо
       // все заново делать и ЧИТАТЬ НАСТРОЙКИ сенсоров
       if(mBluetoothAdapter != null ) return true;
       // For API level 18 and above, get a reference to BluetoothAdapter through
       // BluetoothManager.
       if (mBluetoothManager == null) {
           mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
           if (mBluetoothManager == null) {
               Log.e(TAG, "Service--- Unable to initialize BluetoothManager.");
               return false;
           }
       }
       mBluetoothAdapter = mBluetoothManager.getAdapter();
       if (mBluetoothAdapter == null) {
           Log.e(TAG, "Service--- Unable to obtain a BluetoothAdapter.");
           return false;
       }
       Log.e(TAG,"Service------get BluetoothAdapter = OK----");
       //---// Читаем сеттинги из фалов НА флеши------------------
       settingGetFileGoToConnect();
       //myThread.setPriority(10);
       myThread.setDaemon(true);// Указывает на то чтоб убивать когда будет прибито ПРИЛОЖЕНИЕ его породившее
    //   myThread.start();
        Log.e(TAG,"Servise------init-- init-- OK");
        return true;
    }
    private int loopI = 0;
    Thread myThread = new Thread( // создаём новый поток
            new Runnable() { // описываем объект Runnable в конструкторе
                public void run() {
                    int itemSensor= 0 , timerConnectSensor = 0;
                    Sensor sensor; boolean onStart = true;
                    // вызываем метод воспроизведения
                    while (true){
                        //------Здесь цикл обработки и потдержания работы коннекта----------------
                        if((arraySensors != null) && (arraySensors.size() > 0)){
                            //--запускаем на соннект
                            if(itemSensor >= arraySensors.size()) onStart = false;
                            if(onStart){
                                sensor = arraySensors.get(itemSensor);
                                if(sensor != null){
                                    if((sensor.goToConnect == false)
                                            && (sensor.mConnectionState < STATE_CONNECTED) //если 1 раз запускаем соннект
                                            &&(sensor.mBluetoothDeviceAddress != null)
                                            &&(sensor.mBluetoothDeviceAddress.length() == 17)){
                                        // запускаем на соннект
                                        connect(sensor.mBluetoothDeviceAddress, true);
                                        Log.w("-ServisThread>","--GO connect sensor= "+itemSensor+ "  adress= " + sensor.mBluetoothDeviceAddress);
                                    } else{
                                        //здесь ждем нормального подключения и делаем повтор если облом!но только 1 раз
                                        timerConnectSensor++;
                                        if(((sensor.mConnectionState >= STATE_CONNECTED) && (sensor.goToConnect == false))
                                                || timerConnectSensor > 60){
                                            timerConnectSensor = 0;

                                           if(sensor.mConnectionState >= STATE_CONNECTED) Log.i("-ServisThread>"
                                                   , "--OK connect sensor= "+itemSensor+ "  adress= " + sensor.mBluetoothDeviceAddress);
                                           else  Log.e("-ServisThread>","--ERROR connect sensor= "+itemSensor+ "  adress= "
                                                   + sensor.mBluetoothDeviceAddress);
                                            itemSensor++;
                                        }
//                                        else{
//                                            //если за 30 секунд не подключились повтор
//                                            if(timerConnectSensor > 30) {
//                                                timerConnectSensor = 0;
//                                                sensor.goToConnect = false;
//                                                Log.i("-ServisThread>","--ERROR connect (rep.) sensor= "+itemSensor+ "  adress= " + sensor.mBluetoothDeviceAddress);
//                                            }
//
//                                        }
                                    }
                                } else {
                                    onStart = false;
                                }

                            }else {

                            }
                        }

                        //--
                       if((loopI & 0xF) == 0) Log.w("-DEmonN:", "--"+loopI);
                        /////////////////////////----------------------------------
                        try {
                            myThread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    @Override
    public void onCreate() {
        super.onCreate();
        //---------
        // похоже не успевает все устанавить, а коннект уже налаживает, и обламывается
        //перенесем в инициализацию в приложение!!app
        //     initialize();
        Log.e(TAG,"Service------START -- onCreate()--------------");
    }
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mySettings";
    private SharedPreferences mSettings;

    public void settingGetFileGoToConnect(){
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mSettings == null)  mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // Читаем данные
        if(mSettings != null){
            int listSizeBluetooth = mSettings.getInt("listSizeBluetooth", 0);
            int i;
            for(i= 0; i < listSizeBluetooth;i++){
                String devAdr = mSettings.getString("item"+i, null);
                Log.v(TAG,"onCreate: get sensor from flash= "+i +"   devAdr= " +devAdr + "  size= "+listSizeBluetooth);
                if (devAdr != null) {
                    //читаем УСТРОЙСТВО в файле отдельном
                    SharedPreferences mSettingsDevace =
                            getSharedPreferences(devAdr, Context.MODE_PRIVATE);
                    //getApplication().

                    //RunDataHub app = ((RunDataHub)getApplicationContext())   ;
                    RunDataHub app = ((RunDataHub)getApplication())   ;
                    Sensor sensor = new Sensor(mSettingsDevace, app);
                    arraySensors.add(sensor);
                    Log.i(TAG,"onCreate: get sensor from flash= "+i +"   adress= " +sensor.mBluetoothDeviceAddress);
                    //--запускаем на соннект
                    if((sensor.mBluetoothDeviceAddress != null) &&(sensor.mBluetoothDeviceAddress.length() == 17)){
                        // запускаем на соннект
                        connect(sensor.mBluetoothDeviceAddress, true);
                        Log.w(TAG,"connect sensor= "+i+ "  adress= " + sensor.mBluetoothDeviceAddress);
                    }
                }
            }
            //если нет никого то Пишем своего
            if(i == 0){
               ;// connect("74:DA:EA:9F:54:C9",true);
                //  connect("B4:99:4C:30:41:BA",true);
            }
        }else{
            Log.e(TAG,"getSharedPreferences= null!");
        }
    }
    public void settingPutFile(){
        // Запоминаем данные
        if(mSettings == null) mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings != null){
            Sensor sensor;int i;SharedPreferences settingsDevace;String defName;
            // TODO: 09.12.2016 ОБЯЗАТЕЛЬНО ввести контроль изменения!! и толко при наличие изменений Записывать данные
            // это относится к списку устройств, поскольку устройства сами контролируют свои изменения!
            // в первом файле храним количство и адреса блутуз устройств
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("listSizeBluetooth", arraySensors.size());
            //Остальные файлы- имена ЭТО БЛУЗ АДРЕСА, в них все настройки
            for(i= 0; i < arraySensors.size(); i++){
                defName = "item"+i;
                //сохраняем УСТРОЙСТВО в файле отдельном
                sensor = arraySensors.get(i);
                // "74:DA:EA:9F:54:C9"= 17
                if((sensor.mBluetoothDeviceAddress != null)
                        && (sensor.mBluetoothDeviceAddress.length() == 17)){
                    //занесли в список устройст ЕГО АДРЕС под номером в листе
                    editor.putString(defName, arraySensors.get(i).mBluetoothDeviceAddress);
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

           // RunDataHub app = ((RunDataHub)getApplicationContext())   ;
            RunDataHub app = ((RunDataHub)getApplication())   ;
            sensor = new Sensor(address,app);
            arraySensors.add(sensor);
            Log.w(TAG, " connect: NEW sensor");
        }else{
            sensor = getBluetoothDevice(address);
            Log.w(TAG, " connect: OLd sensor (get from setting file)");
        }
        if(sensor.goToConnect == true) {
            Log.w(TAG, " connect: sensor GoTo connect Old");
        }
        // Previously connected device.  Try to reconnect.
        if (sensor.mBluetoothGatt != null) {
            Log.w(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (sensor.mBluetoothGatt.connect()) {

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
  //      sensor.mBluetoothDeviceAddress = address;
  //      sensor.mConnectionState = STATE_CONNECTING;
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
        for(Sensor sensor: arraySensors) sensor.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        for(Sensor sensor: arraySensors) {
            sensor.close();//обнуление гата
        }
    }

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
        //   intent.putExtra(EXTRA_DATA, sensor.getStringIntermediateValue(false,true));
        intent.putExtra(EXTRA_ADRESS, sensor.mBluetoothDeviceAddress);
        sendBroadcast(intent);
        return;
    }

    public class LocalBinder extends Binder {
        public  BluetoothLeServiceNew getService() {
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
}
