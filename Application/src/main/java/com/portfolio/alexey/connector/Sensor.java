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

import java.io.UnsupportedEncodingException;
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

    public boolean changeConfig = true;//флаг указывающий на ИМЕНЕНИя и ОБЯЗАЕЛЬНО СОХРАНИТЬ  объект(изменились настройки)!!

    private static int indexDevace = 0;//для нумерации названий
    public  RunDataHub app;
    private Context mContext;
    public float maxInputDeviceTemperature = 125f;// для универсального 70, промышленного +125
    public float minInputDeviceTemperature = -40f;// для универсального -20, промышленного +70

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
    // "Health DrawableThermometer"
    public float temperatureMeasurement = 0f;//C- у релсиба НЕ потдерживается

    public float intermediateValue = Float.NaN;//C - работаем с этой темпратурой, точность0.1С, каждую секунду
    // для сброса к текущей температуре ОТДЕЛЬНАЯ кнопка
    public float minValue = Float.NaN;//минимальное значение текущей измеряемой температуры
    public float maxValue = Float.NaN;//максимальное значение текущей измеряемой температуры
    public float predictedTemperature = Float.NaN;//C для градусника ПРЕСКАЗАНИЯ
//медицинский режим - 1 значение:текущее, второе:МАКСИМАЛЬНОЕ, 3 значение прогнозируемо
//МОНИТОР режим      1 значение:минимальное, второе:текущее, 3 значение макисмальное
    public int measurementMode = 1;//0 режим медецинский Или универсальный
    //
    public boolean onFahrenheit = false;
    //
    public long time = 0;//время НАЧАЛА работы сенсора

    public float endTemperature = 0f;//C ПРОГНОЗИРИРОВАНИЕ температуры для медецинского градусника

    //

    public boolean avtoConnect = true;
    public String mBluetoothDeviceAddress = null;//64 bita
    public String deviceLabel = "";

    public String mDeviceName = "";//имя в широковещятельных пакетах

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
    public NotificationLevel minLevelNotification;
    public NotificationLevel maxLevelNotification;
    public NotificationLevel endMeasurementNotification;//КНОПКА разрешение на оповешение звуком по концу измерения

    public boolean   goToConnect= false;//Устанавливается после того как отправляется на коннект!!чтоб повторно НЕ коннектить
//обнуление гата
    public void close() {
        if (mBluetoothGatt == null) return;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        mConnectionState = 0;
        rssi = 4;
        battery_level = 0;
    }
    public void disconnect(){
        if (mBluetoothGatt == null) return;
        mBluetoothGatt.disconnect();
        rssi = 4;
        battery_level = 0;
    }
    //// для сброса к текущей температуре ОТДЕЛЬНАЯ кнопка
    public void resetMinMaxValueTemperature(){
        intermediateValue = Float.NaN;//C - работаем с этой темпратурой, точность0.1С, каждую секунду
        minValue = Float.NaN;//минимальное значение текущей измеряемой температуры
        maxValue = Float.NaN;//максимальное значение текущей измеряемой температуры
        //сбрасываем счетчик времени сенсора
//2017.01.23 ПРИНЯТО, сбро значений мин/мах, НО время идет,
// чтоб продолжать рисовать график       time = System.currentTimeMillis();
    }
    //
    private boolean mHandlerWork = false;
    private Handler mHandler = new Handler();
    //---------------------------------------------------------------------------------
    private float getValueRandom(float min, float max){
        return (float)( Math.random() * (max-min) + min);
    }
    public void resetNotificationVibrationLevelMinMax(){
        minLevelNotification.resetNotification();
        maxLevelNotification.resetNotification();
        endMeasurementNotification.resetNotification();
        Util.playerRingtoneStop();
       // Log.e(TAG," app= " + app +"  app.mainActivityWork=" +app.mainActivityWork);
    }
    //сработала ли хоть одна сигнализация?
    public boolean isNotificationVibrationLevelMinMax(){
        return minLevelNotification.isNotification()
            || maxLevelNotification.isNotification()
            || endMeasurementNotification.isNotification();
    }
    private void controlLevelMinMax(){
        minLevelNotification.calck(intermediateValue);
        maxLevelNotification.calck(intermediateValue);
        endMeasurementNotification.calck(false);
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
        //       Log.v(TAG,"loop  --");
                if(mHandlerWork)mHandler.postDelayed(this, 1000);// loop();
            }
        }, 1000);
    }
    public String toString(){
        return deviceLabel;
    }
    private String name(){
        return mDeviceName;
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
        //2017.01.23 ПРИНЯТО, название Термомето и Номер индекса по умолчанию ;
        deviceLabel = app.getApplicationContext().getString(R.string.sThermometer);
        deviceLabel = deviceLabel + " " + (app.mBluetoothLeServiceM.arraySensors.size()+1);
        markerColor = 0x7 & indexDevace;
        //----------
        minLevelNotification = new NotificationLevel(NotificationLevel.FLOAT_MIN,app.mainActivityWork);
        minLevelNotification.valueLevel =  -40f;// для универсального -20, промышленного +70
        maxLevelNotification = new NotificationLevel(NotificationLevel.FLOAT_MAX,app.mainActivityWork);
        maxLevelNotification.valueLevel = 125f;// для универсального 70, промышленного +125
        endMeasurementNotification = new NotificationLevel(NotificationLevel.BOOLEAN_TRUE,app.mainActivityWork);
//
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
        long h,m,s;
        s = System.currentTimeMillis();
        if(mConnectionState != BluetoothLeServiceNew.STATE_CONNECTED){
            time = s;
            return "00:00:00";
        }
        s = (s - time)/1000;
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
        return getStringValue( minLevelNotification.valueLevel, onFahrenheit, addType);
    }
    //ПРЕДЕЛ срабатывания оповещения
    public String getStringMaxTemperature( boolean addType){
//return getStringValue( maxTemperature, onFahrenheit, addType);
return getStringValue( maxLevelNotification.valueLevel, onFahrenheit, addType);
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
        if(app ==  null) return"";
        if(measurementMode == 0) return app.getString(R.string.sMedical);
        return app.getString(R.string.sUniversal);
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
        // предсказания темепературы для медецинского градусника
        if ((PartGatt.UUID_RELSIB_TEMP.equals(characteristic.getUuid()))) {
            predictedTemperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            if(logON) {
                str = String.format("%.2f", predictedTemperature);
                Log.v(TAG, "predicted_TEMPERATURE: " + str + "  Properties= " + flag);
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
        //----------Health DrawableThermometer-------------------
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
            changeConfig = true;
            if(logON) Log.d(TAG, "MANUFACTURER_NAME_STRING: " + manufacturerName + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_MODEL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            modelNumber = characteristic.getStringValue(0);
            changeConfig = true;
            if(logON) Log.d(TAG, "MODEL_NUMBER_STRING: " + modelNumber + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_SERIAL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            serialNumber = characteristic.getStringValue(0);
            changeConfig = true;
            if(logON) Log.d(TAG, "SERIAL_NUMBER_STRING: " + serialNumber + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_SOFTWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
//            byte[] value = characteristic.getStringValue(0);
//            byte[] value = message.getBytes("UTF-8");
//            String temp = new String.(characteristic.getValue(),0);
//            str = temp.substring(0,temp.length() - 1);
            softwareRevision = characteristic.getStringValue(0);
            changeConfig = true;
            if(logON) Log.d(TAG, "SOFTWARE_REVISION_STRING: " + softwareRevision + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_FIRMWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            firmwareRevision = characteristic.getStringValue(0);
            changeConfig = true;
            if(logON) Log.d(TAG, "FIRMWARE_REVISION_STRING: " + firmwareRevision + "  Properties= " + flag);
            return true;
        }
        if ((PartGatt.UUID_HARDWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            hardwareRevision = characteristic.getStringValue(0);
            changeConfig = true;
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
            //-- 1.95v=  это 0 процентов, 2.95-100% для обычного блутуза
            // наш диапазон - 2.5в- это 0%, по этому - y=(x - 59)*100/41
            battery_level = (battery_level - 59)*100/41;
            if(battery_level < 0) battery_level = 0;
            if(battery_level > 100) battery_level = 100;
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
    public boolean isAdress(){
        if (mBluetoothDeviceAddress != null) return true;
        return false;
    }
    //=================================================================================
    private boolean queue = true;
    public BluetoothGattCharacteristic getGattCharacteristic(UUID uuidService, UUID uuidCharacteristic, String errorHead) {
        BluetoothGattCharacteristic rez = null;
        if (mBluetoothGatt == null) {
            Log.e(TAG, errorHead + "> mBluetoothGatt == null");
            return rez;
        }
        BluetoothGattService service = mBluetoothGatt.getService(uuidService);
        if (service == null) {
            Log.e(TAG, errorHead + "> Not found Service= " + Util.getUidStringMost16Bits(uuidService));
            return rez;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidCharacteristic);
        if (characteristic == null) {
            Log.e(TAG, errorHead + "> Not found Charateristic= " + Util.getUidStringMost16Bits(uuidCharacteristic));
            return rez;
        }
        return characteristic;
    }
    public  boolean readUuidCharacteristic( UUID uuidService, UUID uuidCharacteristic) {
        boolean rez = false; BluetoothGattCharacteristic characteristic;
        characteristic = getGattCharacteristic(uuidService, uuidCharacteristic, "readUuidCharacteristic");
        if (characteristic == null) return rez;
        //если работаем через очередь
        if(queue){
            Log.i(TAG," characteristic= "+Util.getUidStringMost16Bits(characteristic)
                    +"  servic= " + Util.getUidStringMost16Bits(characteristic.getService())
                    + "  getService().getType()= " + characteristic.getService().getType()
            + "  Permissions()= " + characteristic.getPermissions() + "   getInstanceId()=" +characteristic.getInstanceId());
            app.mBluetoothLeServiceM.queueRequestCharacteristicValue(this,characteristic);
            return true;
        }
        rez = mBluetoothGatt.readCharacteristic(characteristic);
        Log.i(TAG, "Read characteristic= " +Util.getUidStringMost16Bits(uuidCharacteristic)+"   status=" + rez);
        return rez;
    }

    //--- Запись характеристики в символах
    public boolean writeUuidCharacteristic(String  message, UUID uuidService, UUID uuidCharacteristic) {
        try {
            final byte[] value = message.getBytes("UTF-8");
            return  writeUuidCharacteristic(value, uuidService, uuidCharacteristic);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    //--- Запись характеристики в байтах
    public boolean writeUuidCharacteristic(byte[] value, UUID uuidService, UUID uuidCharacteristic) {
        boolean rez = false; BluetoothGattCharacteristic characteristic;
        characteristic = getGattCharacteristic(uuidService, uuidCharacteristic, "writeUuidCharacteristic");
        if (characteristic == null) return rez;
        //если работаем через очередь
        if(queue){
            app.mBluetoothLeServiceM.queueWriteDataToCharacteristic(this,characteristic, value);
            return true;
        }
        characteristic.setValue(value);
        rez = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i(TAG, "Write characteristic= " +Util.getUidStringMost16Bits(uuidCharacteristic)+"   status=" + rez);
        return rez;
    }
    //setNotificationIndication
    public boolean writeUuidDescriptor(byte[] bluetoothGattDescriptorValue, UUID uuidService
            , UUID uuidCharacteristic, UUID uuidDescriptor, boolean enabled) {
        boolean rez = false; BluetoothGattCharacteristic characteristic;byte[] val;
        characteristic = getGattCharacteristic(uuidService, uuidCharacteristic, "writeUuidDescriptor");
        if (characteristic == null) return rez;
        //если работаем через очередь
        if(queue){
            app.mBluetoothLeServiceM.queueSetNotificationForCharacteristic(this, characteristic
                    , bluetoothGattDescriptorValue, enabled);
            return true;
        }
        //
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuidDescriptor);
        if (descriptor == null) {
            Log.e(TAG,"writeUuidDescriptor> Not found descriptor= "+ uuidDescriptor.toString());
            return rez;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        //byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        //если сбросываем, то ЗНАЧЕНИЕ для сброса ОДИНАКОВО ДЛя нотификации и ИДИКАЦИИ ()
        if(enabled) val = bluetoothGattDescriptorValue;
        else val = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        descriptor.setValue(val);
        rez = mBluetoothGatt.writeDescriptor(descriptor);
        //мне кажется лучше так, если запись пройдет, то информирование на с ставим, иначе не надо это нам
        //if(sensor.mBluetoothGatt.writeDescriptor(descriptor)) rez = sensor.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        Log.i(TAG, "writeUuidDescriptor= " +uuidCharacteristic.toString()+"   status=" + rez);
        return rez;
    }
    //set Notification
    public boolean setNotification_WriteUuidDescriptor(UUID uuidService
            , UUID uuidCharacteristic, UUID uuidDescriptor, boolean enabled) {
            return writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, PartGatt.UUID_HEALTH_THERMOMETER
                    , PartGatt.UUID_INTERMEDIATE_TEMPERATURE, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);
    }
    //set Indication
    public boolean setIndication_WriteUuidDescriptor(UUID uuidService
            , UUID uuidCharacteristic, UUID uuidDescriptor, boolean enabled) {
        return writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, PartGatt.UUID_HEALTH_THERMOMETER
                , PartGatt.UUID_INTERMEDIATE_TEMPERATURE, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);
    }
    //================================================================================
    //с//отключение сенсора  команды на сброс в сервис РЭЛСИБА
    public void switchOffSensor(){
        //Запись 1 приводит к отключению термометра.
        writeUuidCharacteristic(new byte[]{1}, PartGatt.UUID_RELSIB_SERVICE, PartGatt.UUID_RELSIB_SHUTDOWN);
       // установка термометра медецинского
      //  uuidCharacteristicWrite(new byte[]{1}, PartGatt.UUID_RELSIB_SERVICE, PartGatt.UUID_RELSIB_MODE);
    }
    //сброс измерения посылка команды на сброс в сервис РЭЛСИБА
    public void resetMeasurement(){
        //Флаг сброса последнего измеренного значения температуры (1 байт чтение/запись)
        //Запись 1 приводит к сбросу последнего измеренного значения температуры,
        writeUuidCharacteristic(new byte[]{1}, PartGatt.UUID_RELSIB_SERVICE, PartGatt.UUID_RELSIB_RESET_MEAS_FLAG);
//        //-------------------------
//        // установка предсказания температуры
        //set Indication// установка предсказания температуры
 //       writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, PartGatt.UUID_RELSIB_SERVICE
 //               , PartGatt.UUID_RELSIB_TEMP, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG);
    }
    //--------------
    public void setNewAdressNameDevice(String address, String deviceName){
        //if(sensor.mBluetoothGatt != null)sensor.close();
        //сбрасываем все строковые значения которые касаются модели и номеров прошивок
        //все считываемпотом заново!
        softwareRevision = null;
        firmwareRevision = null;
        hardwareRevision = null;
        serialNumber = null;
        modelNumber = null;
        manufacturerName = null;
        flagRead = false;
        mBluetoothDeviceAddress = address;
        mDeviceName = deviceName;
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
            if(softwareRevision == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_SOFTWARE_REVISION_STRING);
                if(!queue) return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
            if(firmwareRevision == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_FIRMWARE_REVISION_STRING);
                if(!queue) return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
            if(hardwareRevision == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_HARDWARE_REVISION_STRING);
                if(!queue) return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
            if(serialNumber == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_SERIAL_NUMBER_STRING);
                if(!queue) return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
            if(modelNumber == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_MODEL_NUMBER_STRING);
                if(!queue)  return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
            if(manufacturerName == null) {
                readUuidCharacteristic(PartGatt.UUID_DEVICE_INFORMATION_SERVICE, PartGatt.UUID_MANUFACTURER_NAME_STRING);
                if(!queue) return;// если у нас очередь есть, запрашиваем все подряд быстро и разом
            }
        flagRead = true;
    }
    //ЭТО ПРИЕМ ОТ УДАЛЕННОНГО Блутуз устройства!!( TX - ;) !!)
    //устанавливаем ОПОВЕЩЕНИЕ ЕСЛИ ИЗМЕНЯТСЯ ДАННЫЕ, ДЛЯ ИХ СЧИТЫВАНИЯ
    // ПРИ ЭТОМ! автоматически устанавливается БОНД!!,
    // если ключи кривые то ЗАКОНЧИТ ДИСКОННЕКТОМ в течении 1 сек
    // (кривой ключь - удаленное блутуз УСТРОЙСТВО сбрасывали или отключали)
    // TODO: 10.12.2016 СДЕЛАТЬ метод установить нотификацию для одного УИД, а потом его из метода ниже вызывать
    //
    //http://stackoverflow.com/questions/18011816/has-native-android-ble-gatt-implementation-synchronous-nature
    //One of the most important concepts of the Samsung F/W and s tack is its synchronous nature.
    // That is, if we call for example , writeCharacteristic for a particular characteristic,
    // if it returns true, the next call to any BluetoothGatt or BluetoothGattServer method
    // should be done after the onCharacteristicRead callbac k is received .
    // This is because the stack is designed to support and process only one GATT call at time ,
    // and if , for example , you call writeCharacteristic or readCharacteristic of any c haracteristic
    // soon after the first one, it is ignored.
    //
    // https://code.google.com/p/android/issues/detail?id=58381   - обсуждение проблем по блутузу
    // https://gist.github.com/SoulAuctioneer/ee4cb9bc0b3785bbdd51  -- пример!



    public void setNotification_Indication()
    {
        //set Notification
        writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, PartGatt.UUID_HEALTH_THERMOMETER
                , PartGatt.UUID_INTERMEDIATE_TEMPERATURE, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);

        //set Indication// установка предсказания температуры
        writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, PartGatt.UUID_RELSIB_SERVICE
                , PartGatt.UUID_RELSIB_TEMP, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);

        //set  Notification   СЕРДЦЕБИЕНИЕ!!
        writeUuidDescriptor(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, PartGatt.UUID_HEART_RATE_SERVICE
                , PartGatt.UUID_HEART_RATE_MEASUREMENT, PartGatt.UUID_CLIENT_CHARACTERISTIC_CONFIG, true);
    }

    private   int loop_rssi  = 0;
    //каждый вызов этого метода запрашивает RSSI или BatteryLevel
    // ПРИНИМАЕМ, что вызов идит через каждые 2 секунды!!
    // тогда: батарею будем опрашивать каждые 2 минуты
    public boolean readRSSIandBatteryLevel(){
        //контролтруем в каком мы состоянии, только при конекте работаем
        if((mBluetoothGatt == null)
                || (mConnectionState < BluetoothLeServiceNew.STATE_CONNECTED))return false;
        //только если работаем и очердь НЕ занята!!
        if(app.mBluetoothLeServiceM.getSizeTxQueue() > 0)return false;

        //каждые 2 минуты уровень батареи
         if(((loop_rssi++ & 0x3F) == 1)){
            //читать уровень батареи
            readUuidCharacteristic(PartGatt.UUID_BATTERY_SERVICE, PartGatt.UUID_BATTERY_LEVEL);
            Log.e(TAG, "R: BATTERY Service -- ");
            return true;
        }
        if(queue){
            app.mBluetoothLeServiceM.queueReadRSSI(this);
            return true;
        }
        mBluetoothGatt.readRemoteRssi();
        return true;
    }

    //-------------------------------
    public void getConfig(SharedPreferences mSettings){
        changeConfig = false;//установки считаны из ФЛЕШИ- не изменены!!
        //if (mSettings.contains("COUNTER"))
        mBluetoothDeviceAddress = mSettings.getString("mBluetoothDeviceAddress", mBluetoothDeviceAddress);
        avtoConnect = mSettings.getBoolean("avtoConnect", avtoConnect);
        deviceLabel = mSettings.getString("deviceLabel", deviceLabel);//имя назначаемое пользоватлем
        mDeviceName = mSettings.getString("mDeviceName", mDeviceName);
        deviceItem = mSettings.getInt("deviceItem", deviceItem);
        markerColor = mSettings.getInt("markerColor", markerColor);
        //
        measurementMode = mSettings.getInt("measurementMode", measurementMode);//режим медецинский Или универсальный
        fonColor = mSettings.getInt("fonColor", fonColor);
        fonImg = mSettings.getInt("fonImg", fonImg);
        //"Device Information Service":-character--
//Log.v(TAG," "+softwareRevision+" "+firmwareRevision+" "+hardwareRevision+" " +serialNumber+" "+manufacturerName);
        softwareRevision = mSettings.getString("softwareRevision", softwareRevision);
        firmwareRevision = mSettings.getString("firmwareRevision", firmwareRevision);
        hardwareRevision = mSettings.getString("hardwareRevision", hardwareRevision);
        serialNumber = mSettings.getString("serialNumber", serialNumber);
        modelNumber = mSettings.getString("modelNumber",modelNumber);
        manufacturerName = mSettings.getString("manufacturerName", manufacturerName);
//Log.i(TAG," "+softwareRevision+" "+firmwareRevision+" "+hardwareRevision+" " +serialNumber+" "+manufacturerName);
        //
        minLevelNotification.valueLevel = mSettings.getFloat("minTemperature", minLevelNotification.valueLevel);
        maxLevelNotification.valueLevel = mSettings.getFloat("maxTemperature", maxLevelNotification.valueLevel);
        //
        onFahrenheit = mSettings.getBoolean("onFahrenheit", onFahrenheit);
        //
        minLevelNotification.switchVibration = mSettings.getBoolean("onMinVibration", minLevelNotification.switchVibration);
        maxLevelNotification.switchVibration = mSettings.getBoolean("onMaxVibration", maxLevelNotification.switchVibration);

        endMeasurementNotification.switchVibration = mSettings.getBoolean("onEndVibration", endMeasurementNotification.switchVibration);

        minLevelNotification.switchNotification = mSettings.getBoolean("onMinNotification", minLevelNotification.switchNotification);
        maxLevelNotification.switchNotification = mSettings.getBoolean("onMaxNotification", maxLevelNotification.switchNotification);

        endMeasurementNotification.switchNotification = mSettings.getBoolean("onEndNotification", endMeasurementNotification.switchNotification);

// TODO: 17.12.2016 в случае чтения непонятного типа(например булеан,
// а чтиаем стринг- выбрасывает из программы- обработаь !!прерывания
        minLevelNotification.melody = mSettings.getString("minMelody", minLevelNotification.melody);

        maxLevelNotification.melody = mSettings.getString("maxMelody", maxLevelNotification.melody);
        endMeasurementNotification.melody = mSettings.getString("endMelody", endMeasurementNotification.melody);
    }

    public void putConfig(SharedPreferences.Editor editor){
        if(editor == null) return;
        if(changeConfig == false) return;//ничего не меняли и сохранять НЕ надо!!
        editor.putString("mBluetoothDeviceAddress", mBluetoothDeviceAddress);
        editor.putBoolean("avtoConnect", avtoConnect);
        editor.putString("deviceLabel", deviceLabel);//имя назначаемое пользоватлем
        editor.putString("mDeviceName", mDeviceName);
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
        editor.putFloat("minTemperature", minLevelNotification.valueLevel);
        editor.putFloat("maxTemperature", maxLevelNotification.valueLevel);

        //
        editor.putBoolean("onFahrenheit", onFahrenheit);
        //
        editor.putBoolean("onMinVibration", minLevelNotification.switchVibration);
        editor.putBoolean("onMaxVibration", maxLevelNotification.switchVibration);
        editor.putBoolean("onEndVibration", endMeasurementNotification.switchVibration);
        //
        editor.putBoolean("onMinNotification", minLevelNotification.switchNotification);
        editor.putBoolean("onMaxNotification", maxLevelNotification.switchNotification);
        editor.putBoolean("onEndNotification", endMeasurementNotification.switchNotification);

        editor.putString("minMelody", minLevelNotification.melody);

        editor.putString("maxMelody", maxLevelNotification.melody);
        editor.putString("endMelody", endMeasurementNotification.melody);
        //записать на флеш
        editor.apply();//
    }
}
