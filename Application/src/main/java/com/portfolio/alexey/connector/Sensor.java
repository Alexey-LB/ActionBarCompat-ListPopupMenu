package com.portfolio.alexey.connector;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.actionbarcompat.listpopupmenu.Marker;
import com.example.android.actionbarcompat.listpopupmenu.R;
import com.example.android.actionbarcompat.listpopupmenu.RunDataHub;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static android.R.attr.tag;
import static android.R.attr.value;
//import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;

/**
 * Created by lesa on 07.12.2016.
 */

public class Sensor {
    private final static String TAG = Sensor.class.getSimpleName();
 //   public TreeMap <Integer,Object> mData = new TreeMap();
//
//    public static final int STATE_BLUETOOT_ADAPTER_IS_NOT = 0;
//    public static final int STATE_UART_NOT_SUPPORT = 1;
//    public static final int STATE_DISCONNECTED = 2;//СОСТОЯНИЕ дисконнекта БЕЗ ПОПЫТОК ПОВТОРА ВОЗОБНОВЛЕНИЕ КОННЕКТА!!
//    public static final int STATE_DISCONNECTING = 3;//СОСТОЯНИЕ преходного от КОНЕКТА-> ДИСКОНЕКТА, после закрытия канала, переводим в дисконект!
//    public static final int STATE_CONNECTING = 4;//состояние ПРЦЕССА ПОДКЛЮЧЕНИЯ, попыток подключения до 4 и ВОСТАНОВЛЕНИЯ СВВЯЗИ
//    public static final int STATE_CONNECTED = 5;// состояние ПОДКЛЮЧЕНО, вмемте с бондом
//    public static final int STATE_FIND_DEVICE = 6;//advertising

    private static int indexDevace = 0;//для нумерации названий
    private static final String deviceLabelStringDefault = "Термометр";//по умолчанию назначаем имя + номер
    public RunDataHub app;
    private Context mContext;
    public float maxInputDeviceTemperature = 70f;
    public float minInputDeviceTemperature = -20f;

    public BluetoothGatt mBluetoothGatt;
    public BluetoothDevice mBluetoothDevice;
    //  состояние ОБЪЕКТА КЛАССА BluetoothDeviceAlexey
    public int mConnectionState = 0;//STATE_DISCONNECTING;//специально , для изменеия при инициализации, чтоб распространить НА ИЗМЕНЕНИЕ!
    // Состояние КЛАССА BluetoothDevice ОБЪЕКТ dot- мнеджер указывает на его сосояние
    public int mConnectionStateDOT = BluetoothProfile.STATE_DISCONNECTED;
    //--
    public int rssi = 4;// уровень сигнала db

    // Sample Characteristics.
    public int battery_level = 0;//%
    // "Health Thermometer"
    public float temperatureMeasurement = 0f;//C- у релсиба НЕ потдерживается

    public float intermediateValue = Float.NaN;//C - работаем с этой темпратурой, точность0.1С, каждую секунду
    // для сброса к текущей температуре ОТДЕЛЬНАЯ кнопка
    public float minValue = Float.NaN;//минимальное значение текущей измеряемой температуры
    public float maxValue = Float.NaN;//максимальное значение текущей измеряемой температуры
    public float predictedTemperature = Float.NaN;//C для градусника ПРЕСКАЗАНИЯ
//медицинский режим - 1 значение:текущее, второе:МАКСИМАЛЬНОЕ, 3 значение прогнозируемо
//МОНИТОР режим      1 значение:минимальное, второе:текущее, 3 значение макисмальное
    public int measurementMode = 0;//0 режим медецинский Или универсальный
    //
    public boolean onFahrenheit = false;
    //
    public long time = 0;//время НАЧАЛА работы сенсора

    public float minTemperature = -20f;//C ПРЕДЕЛ для АЛЕРТА для монитора температуры
    public float maxTemperature = +70f;//C ПРЕДЕЛ для АЛЕРТА ддля монитора температуры
    public float endTemperature = 0f;//C ПРОГНОЗИРИРОВАНИЕ температуры для медецинского градусника

    //
    public boolean changeConfig = true;//флаг указывающий на ИМЕНЕНИя и ОБЯЗАЕЛЬНО СОХРАНИТЬ  объект(изменились настройки)!!
    public boolean avtoConnect = true;
    public String mBluetoothDeviceAddress = null;//64 bita
    public String deviceLabel = "";

    public String deviceName = "";

    private long mBluetoothDeviceAddressLong = 0;//64 bita
    //
    public int deviceItem = 0;
    public int markerColor = 0;

    //"Device Information Service":-character--
    public String softwareRevision ;
    public String firmwareRevision ;
    public String hardwareRevision ;
    public String serialNumber;
    public String modelNumber ;
    public String manufacturerName;

    //
    public int fonColor = 0;
    public int fonImg = 0;
    //
    public boolean onMinVibrationReset = false;
    public boolean onMaxVibrationReset = false;

    public boolean onMinVibration = false;
    public boolean onMaxVibration = false;
    public boolean onEndVibration = false;
    //
    public boolean onMinNotificationReset = false;
    public boolean onMaxNotificationReset = false;

    public boolean onMinNotification = false;//разрешение на оповешение звуком или вибрацией
    public boolean onMaxNotification = false;//разрешение на оповешение звуком или вибрацией
    public boolean onEndNotification = false;
    //
    public String minMelody;
    public String  maxMelody;
    public String  endMelody;

    public boolean   goToConnect= false;//Устанавливается после того как отправляется на коннект!!чтоб повторно НЕ коннектить
//
    public void close() {
        if (mBluetoothGatt == null) return;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    public void disconnect(){
        if (mBluetoothGatt == null) return;
        mBluetoothGatt.disconnect();
    }
    //// для сброса к текущей температуре ОТДЕЛЬНАЯ кнопка
    public void resetMinMaxValueTemperature(){
        intermediateValue = Float.NaN;//C - работаем с этой темпратурой, точность0.1С, каждую секунду
        minValue = Float.NaN;//минимальное значение текущей измеряемой температуры
        maxValue = Float.NaN;//максимальное значение текущей измеряемой температуры
    }
    //
    private boolean mHandlerWork = false;
    private Handler mHandler = new Handler();
    //---------------------------------------------------------------------------------
    private float getValueRandom(float min, float max){
        return (float)( Math.random() * (max-min) + min);
    }
    public void resetNotificationVibrationLevelMinMax(){
        onMaxNotificationReset = true;
        onMaxVibrationReset = true;
        onMinNotificationReset = true;
        onMinVibrationReset = true;
        Util.playerRingtoneStop();
    }

    private void controlLevelMinMax(){
        if(measurementMode == 0) return;//режим 0 - медецинский Или 1 - универсальный
//        Log.i(TAG,"loop--");
        if(intermediateValue >= maxTemperature){
            Log.e(TAG,"max- " + onMaxNotification + " / " + onMaxNotificationReset);
            if(onMaxNotification && !onMaxNotificationReset && (maxMelody != null)) {
                Log.e(TAG,"maxMelody");
                Util.playerRingtone(0f, maxMelody, app.mainActivityWork,TAG);
            }
            if(onMaxNotification && onMaxVibration && !onMaxVibrationReset) {
                Log.e(TAG,"maxVibrator");
                Util.playerVibrator(300, app.mainActivityWork);
            }
        }else{
            //сбрасываем флаги --
            onMaxNotificationReset = false;
            onMaxVibrationReset = false;
        }
        //---------------
        if(intermediateValue <= minTemperature){
            if(onMinNotification && !onMinNotificationReset && (minMelody != null)){
                Log.e(TAG,"minMelody");
                Util.playerRingtone(0f, minMelody, app.mainActivityWork,TAG);
            }
            if(onMinNotification && onMinVibration && !onMinVibrationReset) {
                Log.e(TAG,"minVibrator");
                Util.playerVibrator(300, app.mainActivityWork);
            }
        } else{
            onMinNotificationReset = false;
            onMinVibrationReset = false;
        }
    }

    //Пока отключил иммитатор -----mHandlerWork = false;
    private  void loop(){
        mHandlerWork = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controlLevelMinMax();
                //Пока отключил иммитатор ----
                if ((false) && (mBluetoothDeviceAddress == null))  {
                    intermediateValue = getValueRandom(20f, 100f);
                    rssi = (int) getValueRandom(40f, 100f);
                    battery_level = (int) getValueRandom(0f, 100f);
            //        Log.e(TAG,"loop Sensor intermediateValue = " + intermediateValue
            //                + "  rssi= "+ rssi+ "  battery_level= " +battery_level);
                }
             //   Log.v(TAG,"loop  --");
                if(mHandlerWork)mHandler.postDelayed(this, 1000);// loop();
            }
        }, 1000);
    }
    public String toString(){
        return deviceLabel;
    }
    private String name(){
        return deviceName;
    }
    // для совместиости с блутуз классом
    public String getName(){
        return manufacturerName;
    }// для совместиости с блутуз классом
    public String getAddress(){
        return mBluetoothDeviceAddress;
    }
    //
    private void initSensor (RunDataHub app_){
        app = app_;
        deviceLabel = deviceLabelStringDefault + " " + indexDevace++;
        markerColor = 0x7 & indexDevace;
        //----------------------
        time = System.currentTimeMillis();
        loop();
    }

    public Sensor (RunDataHub app_){
        initSensor(app_);
    }
    public Sensor (final String adress,RunDataHub app_){
        initSensor(app_);
        mBluetoothDeviceAddress = adress;
    }
    public Sensor (SharedPreferences mSettings, RunDataHub app_){
        initSensor(app_);
        if(mSettings != null)getConfig( mSettings);
    }
    public String  getStringTime(){
        long h,m,s = 0;
        if(time != 0) s = ( System.currentTimeMillis() - time)/1000;
        m = s/60;
        h = m/60;

        return String.format("%02d:%02d:%02d",h,m % 60,s % 60);
    }
    // TODO: 09.12.2016 МЛАДШИЙ разряд датчика температуры-to 0.0625°C, а выдает 0.1 точность, ГДЕ теряется? надо выдавать все, чтоб НЕ РЫСКАЛО
    // в будующем сделать порог  0.0625°C, чтоб показания не прыгали!
    public static  String getStringValue( float inp, boolean fahrenheit, boolean addType){
        final String str; float f= inp;
        if(Float.isNaN(inp)) return "--";
        if(fahrenheit) {
            //f = (f *9/5) + 32;// перевод в ФАРЕНГЕЙТА
            f = Util.getFahrenheit(f);
        }
        if(addType){
            if(fahrenheit) str = "%2.1f °F";
            else str = "%2.1f °C";
        }else str = "%2.1f";
        return String.format(Locale.getDefault(),str,f);
    }

    public float getValue( float inp){
        if(Float.isNaN(inp))  return Float.NaN;
        if(onFahrenheit) {
            //f = (f *9/5) + 32;// перевод в ФАРЕНГЕЙТА
            return Util.getFahrenheit(inp);
        }
        return inp;
    }

    public String getStringValue( float inp, boolean addType){
        final String str; float f= inp;
        if(Float.isNaN(inp))  return "-";
        if(onFahrenheit) {
            //f = (f *9/5) + 32;// перевод в ФАРЕНГЕЙТА
            f = Util.getFahrenheit(f);
        }
        if(addType){
            if(onFahrenheit) str = "%2.1f °F";
            else str = "%2.1f °C";
        }else str = "%2.1f";
        return String.format(Locale.getDefault(),str,f);
    }
    //медицинский режим - 1 значение:текущее, второе:МАКСИМАЛЬНОЕ, 3 значение прогнозируемо
    //МОНИТОР режим      1 значение:минимальное, второе:текущее, 3 значение макисмальное
    public final String getString_1_ValueTemperature(boolean addType){
        if(measurementMode == 0){//режим 0 - медецинский Или 1 - универсальный
            return getStringValue( intermediateValue, addType);
        }
        return getStringValue(minValue , addType);
    }
    public String getString_2_ValueTemperature(boolean addType){
        if(measurementMode == 0){//режим 0 - медецинский Или 1 - универсальный
            return getStringValue( maxValue, addType);
        }
        return getStringValue(intermediateValue , addType);
    }
    public String getString_3_ValueTemperature(boolean addType){
        if(measurementMode == 0){//режим 0 - медецинский Или 1 - универсальный
            return getStringValue( predictedTemperature, addType);//прогнозируемая температура
        }
        return getStringValue(maxValue, addType);
    }
    //текущая температура
    public final String getStringIntermediateValue( boolean addType){
        return getStringValue( intermediateValue, onFahrenheit, addType);
    }
    //текущая минимальная температура
    public String getStringMinValue( boolean addType){
        return getStringValue( minValue, onFahrenheit, addType);
    }
    //текущая МАхсимальная температура
    public String getStringMaxValue( boolean addType){
        return getStringValue( maxValue, onFahrenheit, addType);
    }
    //ПРЕДЕЛ срабатывания оповещения
    public String getStringMinTemperature( boolean addType){
        return getStringValue( minTemperature, onFahrenheit, addType);
    }
    //ПРЕДЕЛ срабатывания оповещения
    public String getStringMaxTemperature( boolean addType){
        return getStringValue( maxTemperature, onFahrenheit, addType);
    }
//
    public String getStringEndTemperature( boolean addType){
        return getStringValue( endTemperature, onFahrenheit, addType);
    }
//      C для градусника ПРЕСКАЗАНИЯ
//    public String getStringPredictedTemperature(boolean addType){
//        return getStringValue( predictedTemperature, onFahrenheit, addType);
//    }
    //режим 0 - медецинский Или 1 - универсальный
    public String getStringMeasurementMode(){
        if(measurementMode == 0) return "Медицинский";
        return "Универсальный";
    }
    //режим медецинский Или универсальный
    public int changeMeasurementMode(){
        measurementMode = (measurementMode + 1)  & 0x1;
        return measurementMode;
    }
    //режим медецинский Или универсальный
    public int getMeasurementMode(){
        return measurementMode & 0x1;
    }

    // TODO: 09.12.2016 перенести все это во внешний класс PartGatt, он должен все определять со значениями и отдавать в виде объект
    public boolean setValue(BluetoothGattCharacteristic characteristic,boolean logON){
        // TODO: 09.12.2016         // ПЕРЕДЕЛАТь для 16 рарядов, использовать свитчь -для нас ЗНАЧИМЫ старшие разряды характеристики для определение че это такое
        int charact = PartGatt.getCharacteristicInt(characteristic);//важны только 16 разрядов для определения сервиса и характеристики стандартного
        final String logStr,str;
        int flag = characteristic.getProperties();
        int format = -1;
        if ((PartGatt.UUID_INTERMEDIATE_TEMPERATURE.equals(characteristic.getUuid()))) {
            intermediateValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            //если сброисили значения, то присваиваем текущие при первом заходе
            if((Float.isNaN(maxValue))
                    || (intermediateValue > maxValue)) maxValue = intermediateValue;
            if((Float.isNaN(minValue))
                    || (intermediateValue < minValue)) minValue = intermediateValue;
            if(logON) {
                str = String.format("%.2f", intermediateValue);
                Log.v(TAG, "TEMPERATURE: " + str + "  Properties= " + flag);
            }
            //
           return true;
        }
//        // carried out as per profile specifications:
//        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if ((PartGatt.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()))) {
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                logStr = "format UINT16.";
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                logStr = "format UINT8.";
            }
            final int heartRate = characteristic.getIntValue(format, 1);
//            final int heartRR1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
//            final int heartRR2 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4);
            if(logON) Log.v(TAG, String.format("Received heart rate: %d  // ", heartRate,logStr));
            intermediateValue = (float)heartRate;
            // intent.putExtra(EXTRA_DATA, String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
            // sendsendBroadcastPutExtra(action,String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
            return true;
        }
        //----------Health Thermometer-------------------
//
//        if ((UUID_TEMPERATURE_MEASUREMENT.equals(characteristic.getUuid()))) {
//
//            final float temp = characteristic.getFloatValue();
//
//            Log.d(TAG, String.format("TEMPERATURE: %f", temp));
//            // intent.putExtra(EXTRA_DATA, String.valueOf(temp));
//            sendsendBroadcastPutExtra(action,String.valueOf(temp));
//            return true;
//        }
        //-

//        //----ТОЛЬКО ЧТЕНИЕ----------////"Device Information Service":-character----------------------------------------
//        if ((PartGatt.UUID_PNP_ID.equals(characteristic.getUuid()))) {//uint8
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "PNP_ID: " + str + "  Properties= " + flag);
//            return true;
//        }
        //Здесь надо разбиратся -- чето НЕ пОНЯТНО формат вывода// https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list.xml
//        if ((PartGatt.UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST.equals(characteristic.getUuid()))) {//reg-cert-data-list
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            Log.d(TAG, String.format("IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: %d   Properties=%02X", temp,flag));
//            str = String.format("%d", temp);
//            Log.d(TAG, "IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: " + str + "  Properties= " + flag);
//            return true;
//        }
//        if ((PartGatt.UUID_SYSTEM_ID.equals(characteristic.getUuid()))) {////(​0x09 ?​0x0A ?)uint40/(0x07)uint24
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "UUID_SYSTEM_ID: " + str + "  Properties= " + flag);
//            return true;
//        }
        if ((PartGatt.UUID_MANUFACTURER_NAME_STRING.equals(characteristic.getUuid()))) {//string
            manufacturerName = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "MANUFACTURER_NAME_STRING: " + manufacturerName + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_MODEL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            modelNumber = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "MODEL_NUMBER_STRING: " + modelNumber + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_SERIAL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            serialNumber = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "SERIAL_NUMBER_STRING: " + serialNumber + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_SOFTWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
//            byte[] value = characteristic.getStringValue(0);
//            byte[] value = message.getBytes("UTF-8");
//            String temp = new String.(characteristic.getValue(),0);
//            str = temp.substring(0,temp.length() - 1);
            softwareRevision = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "SOFTWARE_REVISION_STRING: " + softwareRevision + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_FIRMWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            firmwareRevision = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "FIRMWARE_REVISION_STRING: " + firmwareRevision + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_HARDWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            hardwareRevision = characteristic.getStringValue(0);
            if(logON) Log.d(TAG, "HARDWARE_REVISION_STRING: " + hardwareRevision  + "  Properties= " + flag);
            return true;
        }
        //---------------------------------------------------------
        //https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.battery_service.xml
        //-----------------------------это 1 байт от 0 100% батареи, 0%- батарея разряжена
        //дескриптор 12- знаковое 8 разрядное число!означает
        // https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.descriptor.gatt.characteristic_presentation_format.xml
//// TODO: 09.12.2016 переделать и уточнить по определению формата получаемых данных
        if ((PartGatt.UUID_BATTERY_LEVEL.equals(characteristic.getUuid()))) {
            if ((flag & 0x08) != 0) {//12-знаковое 8 разрядов
                format = BluetoothGattCharacteristic.FORMAT_SINT8;
                logStr = String.format("format SINT8 (getProperties=%d)",flag);
            } else {//4-без знаковое 8 разрядов
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                logStr = String.format("format UINT8(getProperties=%d)",flag);
            }
            battery_level = characteristic.getIntValue(format, 0);
            if(logON) {
                str = String.format("%d", battery_level);
                Log.d(TAG, "BATTERY_LEVEL(%%): " + str + "  Properties= " + logStr);
            }
            return true;
        }
        //------------ Не поняли что это
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)    stringBuilder.append(String.format("%02X ", byteChar));
            // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            if(logON){
                logStr = String.format("    Properties=%08X",flag);
                Log.v("service","length= " + data.length+ "  characteristic= "
                        + stringBuilder.toString() + logStr);
            }
            return true;
        }
        return false;
    }
    //устройство Зарегестрировано
    public boolean isExistAdress(){
        if (mBluetoothDeviceAddress != null) return true;
        return false;
    }
    //передача строки С ОДНИМ!! ПАРАМЕТРОМ!!! (ключь=значение), с переделкой в байты и т д
    //вроде не более 20 символов за раз!!!
    private   boolean characteristic_Write(String message) {
        boolean status = false;
//
//        try {
//            byte[] value = message.getBytes("UTF-8");
//            if ((Tx_Char != null) && (mBluetoothGatt != null)){
//                Tx_Char.setValue(value);
//                status = mBluetoothGatt.writeCharacteristic(Tx_Char);
//            } else  Log.e(TAG,"characteristic_Write: Tx_Char == null or mBluetoothGatt == null");
//            return status;
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return status;
//        }
        return status;
    }
    private boolean flagRead = false;

    /**
     * todo ДОДЕЛАТЬ, ввести опрос по таймеру, поскольку устройства РАЗНЫЕ могут быть и о них НАДО
     * получить ОПИСАНИЕ в любом случае, даже если нет нотифик значений
     * ВЫЯСНИЛ, запрашивать можно только по одному, иначе не отвечает или пропускает!!
     * сделано так, что Запрашивает по одному (если запрашиваемый парметр отсутствует) и выходит
     */
    public void onCharacteristicRead() {

        if(flagRead) return;// если все прочитали, а читаем по одномУ!!иНАЧЕ не получается-= не буферирует запросы!
        Log.w(TAG, "SetRead: onCharacteristicRead Maker ---");

        BluetoothGattService service;
        BluetoothGattCharacteristic characteristic;
        //СМОТРИМ наличие СЕРВИСА (внутри сервеса характеристики с описателями их)
        service = mBluetoothGatt.getService(PartGatt.UUID_DEVICE_INFORMATION_SERVICE);
        if (service != null) {
            //          Log.i(TAG, "SetRead: DEVICE_INFORMATION");
            if(softwareRevision == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_SOFTWARE_REVISION_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
            if(firmwareRevision == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_FIRMWARE_REVISION_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
            if(hardwareRevision == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_HARDWARE_REVISION_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
            if(serialNumber == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_SERIAL_NUMBER_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
            if(modelNumber == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_MODEL_NUMBER_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
            if(manufacturerName == null) {
                characteristic = service.getCharacteristic(PartGatt.UUID_MANUFACTURER_NAME_STRING);
                mBluetoothGatt.readCharacteristic(characteristic);
                return;
            }
        } else {
            Log.w(TAG, "SetRead: DEVICE_INFORMATION Service not found!");
        }
//        service = mBluetoothGatt.getService(PartGatt.UUID_BATTERY_SERVICE);
//        if (service != null) {
//            Log.i(TAG, "SetRead: BATTERY");
//            if(battery_level < 0) {
//                characteristic = service.getCharacteristic(PartGatt.UUID_BATTERY_LEVEL);
//                mBluetoothGatt.readCharacteristic(characteristic);
//                return;
//            }
//        } else{
//            Log.w(TAG, "SetRead: BATTERY Service not found!");
//        }
        flagRead = true;
    }
    //ЭТО ПРИЕМ ОТ УДАЛЕННОНГО Блутуз устройства!!( TX - ;) !!)
    //устанавливаем ОПОВЕЩЕНИЕ ЕСЛИ ИЗМЕНЯТСЯ ДАННЫЕ, ДЛЯ ИХ СЧИТЫВАНИЯ
    // ПРИ ЭТОМ! автоматически устанавливается БОНД!!,
    // если ключи кривые то ЗАКОНЧИТ ДИСКОННЕКТОМ в течении 1 сек
    // (кривой ключь - удаленное блутуз УСТРОЙСТВО сбрасывали или отключали)
    // TODO: 10.12.2016 СДЕЛАТЬ метод установить нотификацию для одного УИД, а потом его из метода ниже вызывать
    public void enableTXNotification()
    {
        boolean status,status2;
        BluetoothGattCharacteristic characteristic;
        BluetoothGattDescriptor descriptor;
        BluetoothGattService service;
        // должно быть
        //enableTXNotification:  //getState: 12 //isDiscovering: false //getScanMode: 21
        //Log.w(TAG, "enableTXNotification:  "+" //isDiscovering: "+ mBluetoothAdapter.isDiscovering() +" //getScanMode: " + mBluetoothAdapter.getScanMode());
        //СМОТРИМ наличие СЕРВИСА (внутри сервеса характеристики с описателями их)
        service = mBluetoothGatt.getService(PartGatt.UUID_HEALTH_THERMOMETER);
        if (service != null) {
            characteristic = service.getCharacteristic(PartGatt.UUID_INTERMEDIATE_TEMPERATURE);
            if( characteristic != null){
                mBluetoothGatt.setCharacteristicNotification(characteristic,true);
                descriptor =  characteristic.getDescriptor(PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);
                if( descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                    Log.i(TAG, "enableRXNotification: healthThermometerService");
                }
            }
        } else {
            Log.w(TAG, "enableRXNotification: HEALTH_THERMOMETER Service not found!");
        }
        service = mBluetoothGatt.getService(PartGatt.UUID_HEART_RATE_SERVICE);
        if (service != null) {
            characteristic = service.getCharacteristic(PartGatt.UUID_HEART_RATE_MEASUREMENT);
            if( characteristic != null){
                mBluetoothGatt.setCharacteristicNotification(characteristic,true);
                descriptor =  characteristic.getDescriptor(PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);
                if( descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                    Log.i(TAG, "enableRXNotification: heartRateServiceService");
                }
            }
        } else{
            Log.w(TAG, "enableRXNotification: HEART_RATE_SERVICE_Service Service not found!");
        }
    }

    //если совпадает, то ФАЛЬШ, изменения не вносим
    public boolean setText(TextView textView, String txtDevice){
        if(textView.getText().toString().compareTo(txtDevice) == 0) return false;
        textView.setText(txtDevice);
        return true;
    }

    private   int loop_rssi  = 0;
    public boolean readRSSIandBatteryLevel(){
        if(mBluetoothGatt == null) return false;
        // ЭТО для отладки--!!!
        //todo(loop_rssi++ & 0xFFFFFFC0) == 0)- В ТЕЧЕНИИ первой МИНУТЫ запросы будут идти КАЖДУЮ секунду!!!

        //каждые 16 сек смотрим уровень сигнала
        loop_rssi++;
        if(((loop_rssi & 0x0F) == 1) || ((loop_rssi & 0xFFFFFFF3) == 1)) {
            mBluetoothGatt.readRemoteRssi();
            return true;
            //           Log.w(TAG, "enableRXNotification: loop_rssi -- ");
        }
        //каждые 2 минуты уровень батареи
        if(((loop_rssi & 0x7F) == 3) || ((loop_rssi & 0xFFFFFFF3) == 3)){//Чаше чем 1 раз в 4 секунды НЕ надо, захлебывается
       // if(((loop_rssi & 0x3) == 3)){
            BluetoothGattCharacteristic characteristic;
            BluetoothGattService service;
            //читать уровень батареи
            service = mBluetoothGatt.getService(PartGatt.UUID_BATTERY_SERVICE);
            if (service == null) {
                Log.e(TAG, "SetRead: BATTERY Service not found!");
                return false;//сервиса нет в этом устройстве
            }//читать уровень батареи
            characteristic = service.getCharacteristic(PartGatt.UUID_BATTERY_LEVEL);
            mBluetoothGatt.readCharacteristic(characteristic);
            Log.e(TAG, "R: BATTERY Service -- ");
            return true;
        }
        return false;
    }

    //-------------------------------
    public void getConfig(SharedPreferences mSettings){
        changeConfig = false;//установки считаны из ФЛЕШИ- не изменены!!
        //if (mSettings.contains("COUNTER"))
        mBluetoothDeviceAddress = mSettings.getString("mBluetoothDeviceAddress", mBluetoothDeviceAddress);
        avtoConnect = mSettings.getBoolean("avtoConnect", avtoConnect);
        deviceLabel = mSettings.getString("deviceLabel", deviceLabel);//имя назначаемое пользоватлем
        deviceName = mSettings.getString("deviceName", deviceName);
        deviceItem = mSettings.getInt("deviceItem", deviceItem);
        markerColor = mSettings.getInt("markerColor", markerColor);
        //
        measurementMode = mSettings.getInt("measurementMode", measurementMode);//режим медецинский Или универсальный
        fonColor = mSettings.getInt("fonColor", fonColor);
        fonImg = mSettings.getInt("fonImg", fonImg);
        //"Device Information Service":-character--
        softwareRevision = mSettings.getString("softwareRevision", softwareRevision);
        firmwareRevision = mSettings.getString("firmwareRevision", firmwareRevision);
        hardwareRevision = mSettings.getString("hardwareRevision", hardwareRevision);
        serialNumber = mSettings.getString("serialNumber", serialNumber);
        modelNumber = mSettings.getString("modelNumber",modelNumber);
        manufacturerName = mSettings.getString("manufacturerName", manufacturerName);
        //
        minTemperature = mSettings.getFloat("minTemperature", minTemperature);
        maxTemperature = mSettings.getFloat("maxTemperature", maxTemperature);
        //
        onFahrenheit = mSettings.getBoolean("onFahrenheit", onFahrenheit);
        onMinVibration = mSettings.getBoolean("onMinVibration", onMinVibration);
        onMaxVibration = mSettings.getBoolean("onMaxVibration", onMaxVibration);
        onEndVibration = mSettings.getBoolean("onEndVibration", onEndVibration);

        onMinNotification = mSettings.getBoolean("onMinNotification", onMinNotification);
        onMaxNotification = mSettings.getBoolean("onMaxNotification", onMaxNotification);
        onEndNotification = mSettings.getBoolean("onEndNotification", onEndNotification);

// TODO: 17.12.2016 в случае чтения непонятного типа(например булеан,
// а чтиаем стринг- выбрасывает из программы- обработаь !!прерывания
        minMelody = mSettings.getString("minMelody", minMelody);
        maxMelody = mSettings.getString("maxMelody", maxMelody);
        endMelody = mSettings.getString("endMelody", endMelody);
    }

    public void putConfig(SharedPreferences.Editor editor){
        if(editor == null) return;
        if(changeConfig == false) return;//ничего не меняли и сохранять НЕ надо!!
        editor.putString("mBluetoothDeviceAddress", mBluetoothDeviceAddress);
        editor.putBoolean("avtoConnect", avtoConnect);
        editor.putString("deviceLabel", deviceLabel);//имя назначаемое пользоватлем
        editor.putString("deviceName", deviceName);
        editor.putInt("deviceItem", deviceItem);
        editor.putInt("markerColor", markerColor);
        //
        editor.putInt("measurementMode", measurementMode);//режим медецинский Или универсальный
        editor.putInt("fonColor", fonColor);
        editor.putInt("fonImg", fonImg);
        //"Device Information Service":-character--
        editor.putString("softwareRevision", softwareRevision);
        editor.putString("firmwareRevision", firmwareRevision);
        editor.putString("hardwareRevision", hardwareRevision);
        editor.putString("serialNumber", serialNumber);
        editor.putString("modelNumber",modelNumber);
        editor.putString("manufacturerName", manufacturerName);
        //
        editor.putFloat("minTemperature", minTemperature);
        editor.putFloat("maxTemperature", maxTemperature);
        //
        editor.putBoolean("onFahrenheit", onFahrenheit);
        editor.putBoolean("onMinVibration", onMinVibration);
        editor.putBoolean("onMaxVibration", onMaxVibration);
        editor.putBoolean("onEndVibration", onEndVibration);
        //
        editor.putBoolean("onMinNotification", onMinNotification);
        editor.putBoolean("onMaxNotification", onMaxNotification);
        editor.putBoolean("onEndNotification", onEndNotification);

        editor.putString("minMelody", minMelody);
        editor.putString("maxMelody", maxMelody);
        editor.putString("endMelody", endMelody);
        //записать на флеш
        editor.apply();//
    }
}
