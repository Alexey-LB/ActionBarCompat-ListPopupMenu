package com.portfolio.alexey.connector;

import android.app.Activity;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.actionbarcompat.listpopupmenu.R;
import com.example.android.actionbarcompat.listpopupmenu.RunDataHub;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import static com.portfolio.alexey.connector.Util.context;
import static java.lang.Thread.sleep;
import static java.security.AccessController.getContext;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeServiceNew extends Service {
    private boolean debug = true;
    private final static String TAG = BluetoothLeServiceNew.class.getSimpleName();

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;

    // private String mBluetoothDeviceAddress;

    // private BluetoothGatt mBluetoothGatt;
    //private int mConnectionState = STATE_DISCONNECTED;

    static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

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
    Semaphore semaphore = new Semaphore(1); // 1 разрешение
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
    public boolean setFahrenheit(Boolean fahren) {
        //если масива сенсоров нет, то и устанавливать значений некому!
        if ((arraySensors == null) || (arraySensors.size() == 0)) return false;
        //устанавливаем еденицы измерения ДЛЯ ВСЕХ одинаково!
        for (Sensor sensor : arraySensors) {
            if (sensor.onFahrenheit != fahren) {
                sensor.onFahrenheit = fahren;
                sensor.changeConfig = true;//установили изменеие сонфигурации ДЛЯ сохранения во ФЛЕШИ телефона
            }
        }
        return fahren;
    }

    public boolean getFahrenheit() {
        //если масива сенсоров нет, то и устанавливать значений некому!
        if ((arraySensors == null) || (arraySensors.size() <= 0)) return false;
        // У всех еденицы измерения одинаковы берем первый!
        return arraySensors.get(0).onFahrenheit;
    }

    /**
     * @param adr поиск сенсора с указанным адресом
     * @return
     */
    public Sensor getBluetoothDevice(final String adr) {
        for (Sensor sensor : arraySensors) {
            if (sensor.mBluetoothDeviceAddress == null) continue;
            if (adr.compareTo(sensor.mBluetoothDeviceAddress) == 0) return sensor;
        }
        return null;
    }

    //!!!ПОКА СЕМАФОРЫ ОТКЛЮЧИЛ!!
    // http://movejava.blogspot.ru/2013/06/javautilconcurrentsemaphore.html
    // http://metanit.com/java/tutorial/8.6.php
    //ожидание разрешения у семафора
    private void blocking() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //освобождение семафора
    private void release() {
        semaphore.release();
    }

    //blocking();release();
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            blocking();
            super.onConnectionStateChange(gatt, status, newState);
            onConnectionStateChangeWork(gatt, status, newState);
            release();
        }

        private void onConnectionStateChangeWork(BluetoothGatt gatt, int status, int newState) {
            //если у нас есть такое устройство
            String intentAction, str;
            boolean ds = false;
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            str = " status= " + status + "   adress= " + sensor.mBluetoothDeviceAddress;
            //
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                if (status == 133) {
                    sensor.battery_level = 0;// уровня батареи нет
                    sensor.rssi = STATE_DISCONNECTED;//показываем что отключились
                    Log.e(TAG, "---ERROR -- STATE_CONNECTED -- go to STATE_DISCONNECTED" + str);
                    queueSetTimer(sensor, 5000);
                    queueSetDisconnectCloseConnect(sensor);//в дисконнекте есть востановление коннекта
                    return;
                } else {
                    Log.w(TAG, "-- STATE_CONNECTED --" + str);
                }
                intentAction = ACTION_GATT_CONNECTED;
                // состояние промежуточное-МЫ подключились- но ЕЩЕ НЕ СЧИТАЛИ СЕРВИСЫ доступные на этом устройстве
                sensor.mConnectionState = STATE_CONNECTING;
                //для отображения состояния подключения волны расходятся от значка--
                sensor.rssi = STATE_CONNECTING;//показываем что коннектимся
                //Запускаем считывание СЕРВИСОВ и характеристик (discovery)
                // broadcastUpdate(intentAction, sensor);
                // Attempts to discover services after successful connection.
//   пытался без дисковери запросить характеристику НЕ получилось, не отвечает сенсор!
//                // BluetoothGattService#SERVICE_TYPE_SECONDARY
//                BluetoothGattCharacteristic ch;BluetoothGattService sv;
//                sv = new BluetoothGattService(PartGatt.UUID_BATTERY_SERVICE,0);
//                ch  = new BluetoothGattCharacteristic(PartGatt.UUID_BATTERY_LEVEL,0,0);
//                sv.addCharacteristic(ch);
//                ch = sv.getCharacteristic(PartGatt.UUID_BATTERY_LEVEL);
//                queueRequestCharacteristicValue(sensor,ch);
                queueSetDiscover(sensor);
// ds= sensor.mBluetoothGatt.discoverServices();
// Log.v(TAG, "Attempting to start service discovery:" + ds +"  adress= "+sensor.mBluetoothDeviceAddress);
                // сбрасываем запрос на коннект, он выполнен//сброс команды
                filtrInTxQueue(sensor, TxQueueItemType.Connect, null, status);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // просто отключение-оБлом
                sensor.mConnectionState = STATE_DISCONNECTED;
                Log.w(TAG, "--- STATE_DISCONNECTED --- " + str);
                sensor.battery_level = 0;// уровня батареи нет
                sensor.rssi = STATE_DISCONNECTED;//показываем что отключились
                //intentAction = ACTION_GATT_DISCONNECTED;
                // broadcastUpdate(intentAction, sensor);//сброс команды
                filtrInTxQueue(sensor, TxQueueItemType.DisconnectCloseConnect, null, status);//BluetoothGatt.GATT_SUCCESS);
            }
        }

        //-------------------------------------------------------------------------------------------------------
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            blocking();
            super.onServicesDiscovered(gatt, status);
            onServicesDiscoveredWork(gatt, status);
            release();
        }

        private void onServicesDiscoveredWork(BluetoothGatt gatt, int status) {
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;

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
//                после нотификации, даем паузу, а то на некторых устройствах
//                 ПИШЕТ С ОШИБКОЙ дескрипотр и переходит дисконнект И ТАК ЦИКЛИТ НЕСКОЛЬКО РАЗ
//                  при тестировании хватило 100мкс, на всякий случай поставим 500мкс
                //перенес в метод нотификации               queueSetTimer(sensor,500);
                Log.w(TAG, "--- STATE_DISCOVERED_OK ---  GATT_SUCCESS  adress= " + sensor.mBluetoothDeviceAddress);
            } else {
                Log.e(TAG, "--- STATE_DISCOVERED_ERROR ---  ERROR status: " + status);
            }
            //сброс команды
            filtrInTxQueue(sensor, TxQueueItemType.DiscoverServices, null, status);
        }

        //-------------------------------------------------------------------------------------------------------
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            blocking();
            super.onCharacteristicRead(gatt, characteristic, status);
            onCharacteristicReadWork(gatt, characteristic, status);
            release();
        }

        private void onCharacteristicReadWork(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic, int status) {
            // Log.w(TAG, "onCharacteristicRead--------------------");
            //если у нас есть такое устройство
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            //
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
                //    Log.i(TAG, "   adress= " + sensor.mBluetoothDeviceAddress);

                sensor.setValue(characteristic, false);
                sensor.onCharacteristicRead();//постоянно запрашивает характеристику- и обламывает остальное!!
                Log.w(TAG, "-- onCharacteristicRead -- gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        + "  service= " + Util.getUidStringMost16Bits(characteristic.getService()));//characteristic.getUuid().toString());
            } else {
                Log.e(TAG, "-- onCharacteristicRead --  ERROR gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        + "  service= " + Util.getUidStringMost16Bits(characteristic.getService()));//characteristic.getUuid().toString());

            }
            // Ready for next transmission//сброс команды
            filtrInTxQueue(sensor, TxQueueItemType.ReadCharacteristic, characteristic.getUuid(), status);
        }

        //----------------------------------------------------------------------------------------------
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            blocking();
            super.onCharacteristicChanged(gatt, characteristic);
            onCharacteristicChangedWork(gatt, characteristic);
            release();
        }

        private void onCharacteristicChangedWork(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic) {
            //     Log.v(TAG, "onCharacteristicChanged--------------------");
            //если у нас есть такое устройство

            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            sensor.goToConnect = false;//подключение закончилочсь УДАЧНО!!
            sensor.setValue(characteristic, false);

            sensor.onCharacteristicRead();
            //ЗАПРАШИВАТЬ (или записыват) ЗА 1 РАЗ можно только 1 характеристику
            // или свойства - иначе НЕ отвечает
            //
            // broadcastUpdate(ACTION_DATA_AVAILABLE, sensor, characteristic);
            //       Log.i(TAG, "   adress= " + sensor.mBluetoothDeviceAddress);
            //если текшая команда нотификации, пытаемся сбросить и от сюда,
            // иногда ОБРАТНЫЙ ВЫЗОВ ЗАПИСИ ДЕСКРИПТОРА не срабатывает, по этому дублируем!
            if ((mTxQueueItem != null) && (mTxQueueItem.type == TxQueueItemType.WriteDescriptor)) {
                if (debug)
                    Log.v(TAG, "---------onCharacteristicChanged --, RESET WriteDescriptor ---");
                filtrInTxQueue(sensor, TxQueueItemType.WriteDescriptor, characteristic.getUuid(), BluetoothGatt.GATT_SUCCESS);
            }
        }

        //----------------------------------------------------------------------------------------------
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            blocking();
            super.onReadRemoteRssi(gatt, rssi, status);
            onReadRemoteRssiWork(gatt, rssi, status);
            release();
        }

        private void onReadRemoteRssiWork(BluetoothGatt gatt, int rssi, int status) {
            //если у нас есть такое устройство//входной контроль
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            ////  Log.i(TAG, "onReadRemoteRssi= " + rssi);
            sensor.rssi = rssi;
            //сброс команды
            filtrInTxQueue(sensor, TxQueueItemType.ReadRSSI, null, status);
        }

        //-------------------------------------------------------------------------
        //.. https://gist.github.com/SoulAuctioneer/ee4cb9bc0b3785bbdd51 -- пример
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            blocking();
            super.onCharacteristicWrite(gatt, characteristic, status);
            onCharacteristicWriteWork(gatt, characteristic, status);
            release();
        }

        private void onCharacteristicWriteWork(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //  Log.w(TAG, "----------------------onCharacteristic_Write");
//            // we got response regarding our request to write new value to the characteristic
//            // let see if it failed or not
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "-- onCharacteristicWrite -- gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        + "  service= " + Util.getUidStringMost16Bits(characteristic.getService()));
                //             mUiCallback.uiSuccessfulWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description);
            } else {
                Log.e(TAG, "-- onCharacteristicWrite -- ERROR gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  characteristic= " + Util.getUidStringMost16Bits(characteristic)
                        + "  service= " + Util.getUidStringMost16Bits(characteristic.getService()));
                //             mUiCallback.uiFailedWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description + " STATUS = " + status);
            }
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            // Ready for next transmission//сброс команды
            filtrInTxQueue(sensor, TxQueueItemType.WriteCharacteristic, characteristic.getUuid(), status);
        }
        //-------------------------------------------------------------------------

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
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            blocking();
            super.onDescriptorWrite(gatt, descriptor, status);
            onDescriptorWriteWork(gatt, descriptor, status);
            release();
        }

        private void onDescriptorWriteWork(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "-- onDescriptorWrite -- gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  descriptor= " + Util.getUidStringMost16Bits(descriptor)
                        + "  Characteristic= " + Util.getUidStringMost16Bits(descriptor.getCharacteristic()));
            } else {
                Log.e(TAG, "-- onDescriptorWrite -- ERROR gatt= " + gatt.getDevice().getAddress() + "   status= " + status
                        + "  descriptor= " + Util.getUidStringMost16Bits(descriptor)
                        + "  Characteristic= " + Util.getUidStringMost16Bits(descriptor.getCharacteristic()));
            }
            final Sensor sensor = getBluetoothDevice(gatt.getDevice().getAddress());
            if (sensor == null) return;
            // Ready for next transmission
            filtrInTxQueue(sensor, TxQueueItemType.WriteDescriptor, descriptor.getCharacteristic().getUuid(), status);
        }
    };

    //==============--------------------------------------------------------------------
    /* An enqueueable write operation - notification subscription or characteristic write */
    private class TxQueueItem {
        Sensor sensor;
        BluetoothGattCharacteristic characteristic;
        byte[] dataToWrite; // Only used for characteristic write
        boolean enabled; // Only used for characteristic notification subscription
        public TxQueueItemType type;
        public int retry = 0;//количество повторов запроса
        public int timer = 0;//длительность задержки Тайм аута
        public BluetoothAdapter.LeScanCallback leScanCallback;//обратный вызов для поиска

        @Override
        public String toString() {
            String str;
            //return super.toString();
            if ((sensor == null) || (sensor.getAddress() == null)) str = "null";
            else str = Util.getAddress16Bits(sensor.getAddress());
            str = "   adress= " + str + "   type= " + type + "   retry= " + retry;
            if (characteristic == null) return str;
            else return str + "   characteristic" + Util.getUidStringMost16Bits(characteristic);
        }
    }

    /**
     * The queue of pending transmissions
     */
    private Queue<TxQueueItem> txQueue = new LinkedList<TxQueueItem>();

    public int getSizeTxQueue() {
        return txQueue.size();
    }

    private boolean txQueueProcessing = false;

    private enum TxQueueItemType {
        ReadCharacteristic//чтение характеристики
        , WriteCharacteristic//запись характеристики
        , WriteDescriptor//запись дескриптора
        , DiscoverServices//запрос у сенсора сервисов и характеристик
        , Connect//запрос коннекта
        , DisconnectCloseConnect//запрос дисконнекта  и потом снова на коннект!
        , Timer //временная пауза - используется для окончания переходных процессов или состояний
        , ReadRSSI //чтение амплитуды сигнала сенсора
        , StartLeScanCallback, StopLeScanCallback
    }

    /* queues  */
    public void queueSetTimer(final Sensor sens, int timer) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.type = TxQueueItemType.Timer;
        int i = timer;
        //ограничиваем время от 0.1сек до 10 сек
        if (i < 100) i = 100;
        else {
            if (i > 10000) i = 10000;
        }
        txQueueItem.timer = i;
        //    if(debug)Log.v(TAG," --- queueSetTimer("+timer+") --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    public void queueStartLeScan(BluetoothAdapter.LeScanCallback leScanCallback_) {//mBluetoothAdapter.stopLeScan(mLeScanCallback);
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.type = TxQueueItemType.StartLeScanCallback;
        txQueueItem.leScanCallback = leScanCallback_;
        //    if(debug)Log.v(TAG," --- queueSetTimer("+timer+") --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    public void queueStoptLeScan(BluetoothAdapter.LeScanCallback leScanCallback_) {//mBluetoothAdapter.stopLeScan(mLeScanCallback);
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.type = TxQueueItemType.StopLeScanCallback;
        txQueueItem.leScanCallback = leScanCallback_;
        //    if(debug)Log.v(TAG," --- queueSetTimer("+timer+") --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    /* queues  */
    public void queueSetConnect(final Sensor sens) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.type = TxQueueItemType.Connect;
        //контроль адреса
        if ((sens.mBluetoothDeviceAddress == null)
                || (sens.mBluetoothDeviceAddress.length() != 17)) {
            Log.e(TAG, "queueSetConnect() Adress= ERROR");
            return;
        }
        //       if(debug)Log.v(TAG," --- queueSetConnect --- START, " + txQueueItem.toString());
       // queueSetTimer(sens, 5000);
        addToTxQueue(txQueueItem);
    }

    public void queueSetDisconnectCloseConnect(final Sensor sens) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.type = TxQueueItemType.DisconnectCloseConnect;
        //   if(debug)Log.v(TAG," --- queueSetDisconnectCloseConnect --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    /* queues  */
    public void queueSetDiscover(final Sensor sens) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.type = TxQueueItemType.DiscoverServices;
        //    if(debug)Log.v(TAG," --- queueSetDiscover --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
        //на всякий случай делаем паузу после дисковери, он один раз только выполняется
        queueSetTimer(sens, 3000);
    }

    // ЧТЕНИЕ уровня сгнала СЕНСОРА, время ожидания не более 2 сек
    // установили Тайм аут
    public void queueReadRSSI(final Sensor sens) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.type = TxQueueItemType.ReadRSSI;
        txQueueItem.timer = 2000;//время ожидания не более 2 сек
        //    if(debug)Log.v(TAG," --- queueReadRSSI --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    /* queues enables/disables notification for characteristic */
    public void queueSetNotificationForCharacteristic(final Sensor sens, BluetoothGattCharacteristic ch, final byte[] dataToWrite, boolean enabled) {
        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.enabled = enabled;
        txQueueItem.dataToWrite = dataToWrite;
        txQueueItem.type = TxQueueItemType.WriteDescriptor;
        //    if(debug)Log.v(TAG," --- queueSetNotificationForCharacteristic --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
        //после нотификации, даем паузу, а то на некторых устройствах
        // ПИШЕТ С ОШИБКОЙ дескрипотр и переходит дисконнект И ТАК ЦИКЛИТ НЕСКОЛЬКО РАЗ
        //  при тестировании хватило 100мкс, на всякий случай поставим 500мкс
        queueSetTimer(sens, 1000);
    }

    /* queues enables/disables notification for characteristic */
    public void queueWriteDataToCharacteristic(final Sensor sens, final BluetoothGattCharacteristic ch, final byte[] dataToWrite) {

        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.dataToWrite = dataToWrite;
        txQueueItem.type = TxQueueItemType.WriteCharacteristic;
        //     if(debug)Log.v(TAG," --- queueWriteDataToCharacteristic --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
        //  на всякий случай, в нотификации это оказалось важным!здесь остановимся на 100мкс
        queueSetTimer(sens, 100);
    }

    /* request to fetch newest value stored on the remote device for particular characteristic */
    public void queueRequestCharacteristicValue(final Sensor sens, BluetoothGattCharacteristic ch) {

        // Add to queue because shitty Android GATT stuff is only synchronous
        TxQueueItem txQueueItem = new TxQueueItem();
        txQueueItem.sensor = sens;
        txQueueItem.characteristic = ch;
        txQueueItem.type = TxQueueItemType.ReadCharacteristic;
        //  if(debug)Log.v(TAG," --- queueRequestCharacteristicValue --- START, " + txQueueItem.toString());
        addToTxQueue(txQueueItem);
    }

    private Handler mHandlerTxQueue = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Thread myThread = new Thread( // создаём новый поток
                    new Runnable() { // описываем объект Runnable в конструкторе
                        public void run() {
                            processTxQueue_(false); // вызываем метод воспроизведения
                        }
                    }
            );
            myThread.setDaemon(true);
            //-------
            //если установлен тайм айт, просто дождались заданного времени и сбросили эту команду
            if (mTxQueueItem.type == TxQueueItemType.Timer) {
                if (debug) Log.w(TAG, "--- HANDLER QUEUE --- " + mTxQueueItem.toString());
 processTxQueue_(false);
//myThread.start();
                return;
            }
            // если коннект, по тайм ауту тоже сбрасываем, поскольку в адаптере запоминается
            // автоматическое поднятие соединения. И если наступит КОННЕКТ после дисконнекта,
            // до запустится автоматически сервис дисковери, который (если 5 раз не пройдет)
            // снова ЗАКРОЕТ канал, потом ожидание и снова коннект!!
            // по этому- коннет даем только 1 раз без повтора. Он может быть сброшен на
            // прямую из обратного вызова при коннете
            if (mTxQueueItem.type == TxQueueItemType.Connect) {
                if (debug) Log.w(TAG, "--- HANDLER QUEUE --- " + mTxQueueItem.toString());
 processTxQueue_(false);
// myThread.start();
                return;
            }
            //при дисконнекте повторов НЕ надо
            if (mTxQueueItem.type == TxQueueItemType.DisconnectCloseConnect) {
                if (debug) Log.w(TAG, "--- HANDLER QUEUE --- " + mTxQueueItem.toString());
 processTxQueue_(false);
// myThread.start();
                return;
            }
            //при старте и стопе сканирования повторов НЕ надо
            if ((mTxQueueItem.type == TxQueueItemType.StartLeScanCallback)
                    || (mTxQueueItem.type == TxQueueItemType.StopLeScanCallback)) {
                if (debug) Log.w(TAG, "--- HANDLER QUEUE --- " + mTxQueueItem.toString());
 processTxQueue_(false);
//myThread.start();
                return;
            }
            //-- запускаем контроль запроса по времени, устангавливаем 10 секунд
            // если не уложились, то текущий запрос возвращяем в очередь и увеличиваем попытку
            // передачи, передаем 5 раз и облом! ему, запрашиваемому параметру
            if (debug) Log.e(TAG, "--- HANDLER QUEUE ---  ERROR " + mTxQueueItem.toString());
            if (mTxQueueItem.retry++ <= 5) {
                //---------------------------------------
                //если после поиска, подождать кода закончатся адвансинг пакеты передавать!
                // то есть сенсор отключится, в этот момент запустиь коннект, то соединение
                // адаптер зафиксирует, даст КОННЕКТ, но дисковери команда ОБЛОМИТСЯ, и в таком состоянии КОННЕКТ
                // будет висеть, подачи команд дисковери не вызовут колбак дисковери! п
                // ПО ЭТОМУ!!--
                //дисковери сервис, если он не прошел  3 раза, считаем что разрыв связи,
                //рвем соннект ставим ДИСКОННЕКТ, - ?! а потом коннект?
                if (mTxQueueItem.retry >= 4) {//здесь при любых состояниях, 4 раза облом, обнуляем связь!
                    // сенсора нет на связи, для сброса зависшего состоянияя, переводим в
                    // полное отключение КЛОУС и заново порождаем соединение, оно переходит в состояние дисконнект
                    // если просто закрыть- останется в коннекте!// в ОЧЕРЕДЬ!!!
                    queueSetDisconnectCloseConnect(mTxQueueItem.sensor);
                    if (debug)
                        Log.v(TAG, "--- HANDLER QUEUE ---  ERROR >=3 count " + mTxQueueItem.toString());
                } else {
                    //---------------------------------------
                    //добавляем из начала прямо в КОНЕЦ очереди!
                    txQueue.add(mTxQueueItem);//повтор запроса
                }
            }
processTxQueue_(false);//тайм аут по ответу КОТОРЫЙ НЕ ПРИШЕЛ!!
//myThread.start();
        }
    };

    private void log(String str) {
        if (debug) Log.e(TAG, str);
    }
    //получает все ответы по блутузу сюда, и если они не соответствуют, блокирует их,
    // а очередь заново их запускает на выполнение

    synchronized private void filtrInTxQueue(Sensor sensor, TxQueueItemType type, UUID uuid, int status) {
         Thread  myThread = new Thread( // создаём новый поток
                new Runnable() { // описываем объект Runnable в конструкторе
                    public void run() {
                        try {
                            sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        processTxQueue_(false); // вызываем метод воспроизведения
                    }
                }
        );
        myThread.setDaemon(true);
        //------------
        // если прилетает дисконнект по сенсору
        if (TxQueueItemType.DisconnectCloseConnect == type) {
            if (debug) Log.w(TAG, "=========DISCONNEKT!!==");
            //никого нет на очереди ставим в очередь дисконнект
            if (mTxQueueItem == null) queueSetDisconnectCloseConnect(sensor);
            else {
                // в очереди другой сенсор, просто ставим себя в очередь дисконнект
                if (mTxQueueItem.sensor.getAddress().compareTo(sensor.getAddress()) != 0) {
                    queueSetDisconnectCloseConnect(sensor);
                } else {
                    // если текущий в очереди это Этот сенсор, НО с другой командой, ставим в очередь дисконнект
                    if (mTxQueueItem.type != type) queueSetDisconnectCloseConnect(sensor);
                        //если текущая команда и есть сброс, просто ее пропускаем на сброс
                    else {
 //processTxQueue(false);//это обратная связь для сброса переданного значения
 myThread.start();
                    }
                }
            }
        }
        // обычная обработка-----------------
        if (mTxQueueItem == null) return;
        if (status != BluetoothGatt.GATT_SUCCESS) return;
        if (mTxQueueItem.type != type) {
            log("mTxQueueItem.type != type  (input  adress= " + Util.getAddress16Bits(sensor.getAddress())
                    + "  type= " + type.toString() + ")   current= " + mTxQueueItem.toString());
            return;
        }
        if (mTxQueueItem.sensor.getAddress().compareTo(sensor.getAddress()) != 0) {
            log("compareTo(sensor.getAddress()) != 0  (input  adress= " + Util.getAddress16Bits(sensor.getAddress())
                    + "  type= " + type.toString() + ")   current= " + mTxQueueItem.toString());
            return;
        }
        if (mTxQueueItem.characteristic != null) {
            if (mTxQueueItem.characteristic.getUuid().compareTo(uuid) != 0) {
                log("getUuid().compareTo(uuid) != 0)  (input  adress= " + Util.getAddress16Bits(sensor.getAddress())
                        + "  type= " + type.toString() + ")   current= " + mTxQueueItem.toString());
                return;
            }
        }
        if (debug) Log.i(TAG, "--- RESET OK --- " + mTxQueueItem.toString());

//для теста блокировал уровень батареи посмотрет повтор запроса, работает
//        if(uuid.compareTo(PartGatt.UUID_BATTERY_LEVEL) == 0) {
//            log("getUuid().UUID_BATTERY_LEVEL");
//            return;
//        }
        //это то что мы запросили!!
// processTxQueue(false);//это обратная связь для сброса переданного значения
myThread.start();
    }

    /**
     * Add a transaction item to transaction queue
     *
     * @param txQueueItem
     */
    synchronized private void addToTxQueue(TxQueueItem txQueueItem) {
        if (debug) Log.v(TAG, " --- ADD QUEUE --- , " + txQueueItem.toString());
        txQueue.add(txQueueItem);
        // If there is no other transmission processing, go do this one!
        // processTxQueue(true);

        Thread myThread = new Thread( // создаём новый поток
                new Runnable() { // описываем объект Runnable в конструкторе
                    public void run() {
                        try {
                            sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        processTxQueue_(true); // вызываем метод воспроизведения
                    }
                }
        );
        myThread.setDaemon(true);
        myThread.start();
    }
    /**
     * Call when a transaction has been completed.
     * Will process next transaction if queued
     */
    TxQueueItem mTxQueueItem;
    private void processTxQueue_(boolean init){
        blocking();
        processTxQueueWork_(init);
       release();
    }

    private void processTxQueueWork_(boolean init)//false-  если это обратная связь ИЛИ тайм аут по ответу
    {   //если мы стартуем с новым значением, то это ттолько если не заняты работой по передаче
        // нового значения
        if(init && txQueueProcessing) return;//
        //-----------------------------
        // продолжаем работать, убираем колбак и заново запускаем его для новой передачи
        //необходимо всегда УДАЛЯТЬ старые запуски, иначе ОНИ НАЧИНАЮТ ЖИТЬ ВСЕ ВМЕСТЕ!!!
        mHandlerTxQueue.removeCallbacks(runnable);
        //-работаем если есть подключения, те что в отключке-игнорируем!!-------
        while (true){
            if (txQueue.size() <= 0)  {//если очередь пуста выходим С готовность работать
                txQueueProcessing = false;
                return;
            }
            if(debug){
                if(mTxQueueItem != null)Log.w(TAG," --- REMOVE --- , " + mTxQueueItem.toString());
                else Log.w(TAG," --- REMOVE --- , TxQueueItem= null");
            }
            mTxQueueItem = txQueue.remove();
            if(debug){
                if(mTxQueueItem != null)Log.w(TAG," --- EXECUTE --- , " + mTxQueueItem.toString());
                else Log.w(TAG," --- EXECUTE --- , TxQueueItem= null");
            }

            if((mTxQueueItem.sensor != null)
                &&(mTxQueueItem.sensor.mConnectionState  == STATE_DISCONNECTED)
                && (mTxQueueItem.type != TxQueueItemType.DisconnectCloseConnect)//выполнения последовательности закрытия канала
                && (mTxQueueItem.type != TxQueueItemType.Connect)//выполнения коннекта
                && (mTxQueueItem.type != TxQueueItemType.Timer)// ожидания выполнения команды, чтоб она прошла полностью
                    ){
                Log.w(TAG,"--- REMOVE TxQueueItem, STATE_DISCONNECTED, ---"+ mTxQueueItem.toString());
            } else break;
        }
        //----------------
        txQueueProcessing = true;//заблокировались
        //если очередь НЕ пуста, запускаем контроль по времени снова!
        mHandlerTxQueue.postDelayed(runnable,10000);//-- запускаем контроль запроса по времени, устангавливаем 10 секунд
        //
        switch (mTxQueueItem.type) {
            case WriteCharacteristic:
                writeDataToCharacteristic(mTxQueueItem.sensor, mTxQueueItem.characteristic
                        , mTxQueueItem.dataToWrite);
                break;
            case WriteDescriptor:
                setNotificationForCharacteristic(mTxQueueItem.sensor,mTxQueueItem.characteristic,
                        mTxQueueItem.dataToWrite, mTxQueueItem.enabled);
                break;
            case ReadCharacteristic:
                requestCharacteristicValue(mTxQueueItem.sensor,mTxQueueItem.characteristic);
                break;
            case DiscoverServices:
                mTxQueueItem.sensor.mBluetoothGatt.discoverServices();
                break;
            case DisconnectCloseConnect:
                // время ожидания соединения уменьшаем до 2 секунд пока может надо будет оставить 10
                mHandlerTxQueue.removeCallbacks(runnable);
                mHandlerTxQueue.postDelayed(runnable,2000);
                Log.e(TAG," --- DisconnectClose --->> .START ==> postDelayed ==>>  connect ---");
                // сенсора нет на связи, для сброса зависшего состоянияя, переводим в
                // полное отключение КЛОУС и заново порождаем соединение, оно переходит в состояние дисконнект
                // если просто закрыть- поставим на коннект!
                mTxQueueItem.sensor.close();
                //стваим в очередь, поставим на коннект
                queueSetConnect(mTxQueueItem.sensor);
                break;
            case Connect:
                // время ожидания соединения уменьшаем до 5 секунд пока может надо будет оставить 10
                mHandlerTxQueue.removeCallbacks(runnable);
          //      mHandlerTxQueue.postDelayed(runnable,5000);
                mHandlerTxQueue.postDelayed(runnable,1000);
                //запускем на коннект, если он есть в таблице работаем с ним, если нет ГАТТ то создаем его
                connect(mTxQueueItem.sensor.getAddress(),true);
                break;
            case Timer://установили Тайм аут для конкртного сенсора
                mHandlerTxQueue.removeCallbacks(runnable);
                mHandlerTxQueue.postDelayed(runnable,mTxQueueItem.timer);//-- запускаем тайм аут,
                break;
            case ReadRSSI:// ЧТЕНИЕ уровня сгнала СЕНСОРА, время ожидания не более 2 сек
                // установили Тайм аут
                mTxQueueItem.sensor.mBluetoothGatt.readRemoteRssi();
                mHandlerTxQueue.removeCallbacks(runnable);
                mHandlerTxQueue.postDelayed(runnable,mTxQueueItem.timer);//-- запускаем тайм аут,
                break;
            case StartLeScanCallback:// запуск поиска блутуз устройств
                mBluetoothAdapter.startLeScan(mTxQueueItem.leScanCallback);
                mHandlerTxQueue.removeCallbacks(runnable);
                mHandlerTxQueue.postDelayed(runnable,1000);//-- запускаем тайм аут,
                break;
            case StopLeScanCallback:// запуск поиска блутуз устройств
                mBluetoothAdapter.stopLeScan(mTxQueueItem.leScanCallback);
                mHandlerTxQueue.removeCallbacks(runnable);
                mHandlerTxQueue.postDelayed(runnable,3000);//-- запускаем тайм аут,
                break;
        }
    }
    /* set new value for particular characteristic */
    public void writeDataToCharacteristic(final Sensor sensor,final BluetoothGattCharacteristic ch, final byte[] dataToWrite)
    {
        if (Util.isBluetoothAdapterOff() || sensor.mBluetoothGatt == null || ch == null) return;
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
        if (Util.isBluetoothAdapterOff() || sensor.mBluetoothGatt == null) return;
        boolean success = sensor.mBluetoothGatt.setCharacteristicNotification(ch, enabled);
        if(!success) {
            Log.e(TAG, "Seting proper notification status for characteristic failed!");
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
        if (Util.isBluetoothAdapterOff() || sensor.mBluetoothGatt == null) return;

        sensor.mBluetoothGatt.readCharacteristic(ch);
        // new value available will be notified in Callback Object
    }
//===================================================================================================
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
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        boolean rez = false;
        Log.e(TAG,"Service------START --- initialize() --- ");
        //---// Читаем сеттинги из фалов НА флеши------------------
        settingGetFileGoToConnect();
        //myThread.setPriority(10);
        myThread.setDaemon(true);// Указывает на то чтоб убивать когда будет прибито ПРИЛОЖЕНИЕ его породившее
        //   myThread.start();
        if(mBluetoothAdapter != null) rez = true;
        Log.e(TAG,"Service------END --- initialize()--- Status=" + (rez?"OK":"ERROR"));
        return rez;
    }
    public boolean isBluetoothAdapterExist(){
        //если блутуз не доступен в принципе
        if(mBluetoothManager == null) {
            Toast.makeText(context, context.getString(R.string.error_bluetooth_not_supported)
                    , Toast.LENGTH_LONG).show();
            Log.e(TAG, "--" + context.getString(R.string.error_bluetooth_not_supported));
            return false;
        }
        //проверяем потдержку блутуз адаптером ЛЕ блутуза
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, context.getString(R.string.ble_not_supported)
                    , Toast.LENGTH_LONG).show();
            Log.e(TAG, "--" + context.getString(R.string.ble_not_supported));
            return false;
        }
        if(mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(context, context.getString(R.string.unable_to_obtain_a_BluetoothAdapter), Toast.LENGTH_LONG).show();
                Log.e(TAG, context.getString(R.string.unable_to_obtain_a_BluetoothAdapter));
                return false;
            }
        }
        return true;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"Service------START -- onCreate()");
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = null;
        // Checks if Bluetooth is supported on the device.
        //проверяем потдержку блутуз адаптером ЛЕ блутуза
        if(!isBluetoothAdapterExist()) return;
        //---------
        // похоже не успевает все устанавить, а коннект уже налаживает, и обламывается
        //перенесем в инициализацию в приложение!!app
        //     initialize();
        //getActivity().registerReceiver(this.mBluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        //getActivity().registerReceiver(mGpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        // обработка слушателей в самом низу
        //слушаем изменеия РАРЕШЕНИЯ К ДОСТУПУ по блутузу 1)вкл/выкл адвптер БЛУТУЗ, 2) локация GPS тоже нужно для блутуза
        getApplicationContext().registerReceiver(this.mBluetoothStateBroadcastReceiver
                ,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        getApplicationContext().registerReceiver(mGpsReceiver
                ,new IntentFilter("android.location.PROVIDERS_CHANGED"));
        Log.e(TAG,"Service ------ END -- onCreate()------ BluetoothAdapter ON= " + mBluetoothAdapter.isEnabled());
    }
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mySettings";
    private SharedPreferences mSettings;

    public void settingGetFileGoToConnect(){
//        connect("74:DA:EA:9F:54:C9",true);
//        connect("74:DA:EA:9F:54:E6",true);
//        connect("74:DA:EA:9F:44:3A",true);
//        connect("B4:99:4C:30:41:BA",true);
//        if(true) return;
//
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mSettings == null)  mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
      //  if(mSettings == null)  mSettings = getApplicationContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // Читаем данные
        if(mSettings != null){
            int listSizeBluetooth = mSettings.getInt("listSizeBluetooth", 0);
            int i;
            for(i= 0; i < listSizeBluetooth;i++){
                String devAdr = mSettings.getString("item"+i, null);
                Log.i(TAG,"onCreate: -- get sensor from flash= "+i +"   devAdr= " +devAdr + "  size= "+listSizeBluetooth);
                if (devAdr != null) {
                    //читаем УСТРОЙСТВО в файле отдельном
                    SharedPreferences mSettingsDevace =
                            getSharedPreferences(devAdr, Context.MODE_PRIVATE);
                    //getApplication().

                    //RunDataHub app = ((RunDataHub)getApplicationContext())   ;
                    RunDataHub app = ((RunDataHub)getApplication())   ;
                    Sensor sensor = new Sensor(mSettingsDevace, app);
                    arraySensors.add(sensor);
                    Log.i(TAG,"onCreate: -- get sensor from flash= "+i +"   adress= " +sensor.mBluetoothDeviceAddress);
                    //--запускаем на соннект
                    if((sensor.mBluetoothDeviceAddress != null) &&(sensor.mBluetoothDeviceAddress.length() == 17)){
                        // запускаем на соннект
                        //начинаем конект ВСЕГДА с чистого ЛИСТА, тоесть настроек связи (BluetoothGatt)
                        if(sensor.mBluetoothGatt == null) queueSetConnect(sensor);
                        else queueSetDisconnectCloseConnect(sensor);
                        Log.i(TAG,"onCreate: -- connect sensor= "+i+ "  adress= " + sensor.mBluetoothDeviceAddress);
                    }
                }
            }
            //если нет никого то Пишем своего
//            if(i == 0){
//                connect("74:DA:EA:9F:54:C9",true);
//                connect("74:DA:EA:9F:54:E6",true);
//                connect("74:DA:EA:9F:44:3A",true);
//                connect("B4:99:4C:30:41:BA",true);
//            }
        }else{
            Log.e(TAG,"getSharedPreferences= null!");
        }
    }
    //записываем УСТАНОВКИ если есть изменения в них
    public void testChangesAndSettingPutFile(){
        for(int i= 0; i < arraySensors.size(); i++){
            if(arraySensors.get(i).changeConfig){
                settingPutFile();
                return;
            }
        }
        Log.v(TAG,"No SettingPutFile (no change config)");
    }
    public void settingPutFile(){
        // Запоминаем данные

        if(mSettings == null) mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
       // if(mSettings == null) mSettings = getApplicationContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
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
            Log.i(TAG,"SettingPutFile, Write OK, size= " + i);
        }else{
            Log.e(TAG,"SettingPutFile= null!");
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
        //контроль наличия адаптера и его сотояния (ВКЛ/ВЫКЛ)
        if (Util.isBluetoothAdapterOff() || address == null || address.length() != 17) {
            Log.e(TAG, "BluetoothAdapter(" +mBluetoothAdapter +") not initialized("+address
                    +") or unspecified address("+ address.length()+")");
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
        //ИСПОЛЬЗОВАНИЕ наситроек канала старого соединения- обычно приводит к проблемам!
        //лучше ВСЕГДА закрывать соединения, а потом заново все создавать,
        // тогда нет проблем при ВКЛ/ВЫКЛ блутуз Адаптера
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

        Log.i(TAG, "Trying to create a new connection. getState()= "+mBluetoothAdapter.getState()
                + "   isDiscovering()= " +mBluetoothAdapter.isDiscovering());

        sensor.mBluetoothGatt = device.connectGatt(this, avtoConnect, mGattCallback);

        Log.v(TAG, "Trying to create a new connection. getState()= "+mBluetoothAdapter.getState()
        + "   isDiscovering()= " +mBluetoothAdapter.isDiscovering());
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

    // ========================================================
    //слушаем изменеия РАРЕШЕНИЯ К ДОСТУПУ по блутузу 1)вкл/выкл адвптер БЛУТУЗ, 2) локация GPS тоже нужно для блутуза

    private final BroadcastReceiver mBluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

           //     View view = DataFragment.this.getView();

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
//                        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("74:DA:EA:9F:54:C9");
//                        BluetoothGatt bg = device.connectGatt(getApplicationContext(), true, mGattCallback);
                        if(debug) Log.e(TAG, "???------ BluetoothAdapter.STATE_OFF ----???"
                                + mBluetoothAdapter.isEnabled()
                                +"  "+mBluetoothAdapter.getState());
//                                +"  device= "+device
//                                +"  gatt= "+(bg==null?"null":"No null"));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(debug) Log.e(TAG, "???------ BluetoothAdapter.STATE_TURNING_OFF ----???"+ BluetoothAdapter.getDefaultAdapter().isEnabled());
//                        if (view != null) {
//                            Snackbar.make(view, "Bluetooth Выключен", Snackbar.LENGTH_LONG).show();
//                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if(debug) Log.v(TAG, "!!!------ BluetoothAdapter.STATE_TURNING_ON ----!!!"+ BluetoothAdapter.getDefaultAdapter().isEnabled());

                         break;
                    case BluetoothAdapter.STATE_ON:
                        if(debug) Log.v(TAG, "!!!------ BluetoothAdapter.STATE_ON ----!!!"
                                + mBluetoothAdapter.isEnabled()
                        +"  "+mBluetoothAdapter.getState());
                        //перебираем все сеносры и запускаем их
                        for(Sensor sens: arraySensors){
                            // запускаем на соннект
                            //начинаем конект ВСЕГДА с чистого ЛИСТА, тоесть настроек связи (BluetoothGatt)
                            queueSetDisconnectCloseConnect(sens);
                        }
//                        if (view != null) {
//                            Snackbar.make(view, "Bluetooth Включен", Snackbar.LENGTH_LONG).show();
//                        }
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mGpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
    //        View view = DataFragment.this.getView();
            if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")) {
               // LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = lm.getProviders(true);
                if (providers != null && providers.size() > 0) {
                    if(debug) Log.v(TAG, "!!!------ Определение местоположения ВКЛючено ----!!!");
//                    if (view != null) {
//                        Snackbar.make(view, "Определение местоположения включено", Snackbar.LENGTH_LONG).show();
//                    }
                } else {
                    if(debug) Log.e(TAG, "???------ Определение местоположения ВЫКЛючено ----???");
//                    if (view != null) {
//                        Snackbar.make(view, "Определение местоположения выключено", Snackbar.LENGTH_LONG).show();
//                    }
                }
            }
        }
    };
}
