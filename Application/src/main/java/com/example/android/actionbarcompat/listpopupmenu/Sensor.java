package com.example.android.actionbarcompat.listpopupmenu;
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

import java.util.Map;
import java.util.UUID;

import static android.R.attr.value;
//import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;

/**
 * Created by lesa on 07.12.2016.
 */

public class Sensor {
    private final static String TAG = Sensor.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    //

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

    private Context mContext;
    public BluetoothGatt mBluetoothGatt;
    public BluetoothDevice mBlue;
    //  состояние ОБЪЕКТА КЛАССА BluetoothDeviceAlexey
    public int mConnectionState = 0;//STATE_DISCONNECTING;//специально , для изменеия при инициализации, чтоб распространить НА ИЗМЕНЕНИЕ!
    // Состояние КЛАССА BluetoothDevice ОБЪЕКТ dot- мнеджер указывает на его сосояние
    public int mConnectionStateDOT = BluetoothProfile.STATE_DISCONNECTED;
    //--
    public int rssi = 0;// уровень сигнала db
    public View rssiView = null;

    // Sample Characteristics.
    public int battery_level = -1;//%
    public View battery_levelView = null;
    // "Health Thermometer"
    public float temperatureMeasurement = 0f;//C- у релсиба НЕ потдерживается

    public float intermediateValue = 0f;//C - работаем с этой темпратурой, точность0.1С, каждую секунду
    public View intermediateValueView = null;

    public float minTemperature = -20f;//C для монитора температуры
    public float maxTemperature = +70f;//C для монитора температуры
    public float endTemperature = 0f;//C для градусника
    public float predictedTemperature = 0f;//C для градусника ПРЕСКАЗАНИЯ
    //
    public boolean changeConfig = true;//флаг указывающий на ИМЕНЕНИя и ОБЯЗАЕЛЬНО СОХРАНИТЬ  объект(изменились настройки)!!
    public boolean avtoConnect = true;
    public String mBluetoothDeviceAddress = null;//64 bita
    public String deviceLabel = "Label";
    public View deviceLabelView = null;

    public String deviceName = "deviceNameString";

    private long mBluetoothDeviceAddressLong = 0;//64 bita
    //
    public int deviceItem = 0;
    public int markerColor = 0;
    public View markerColorView = null;

    public int measurementMode = 0;//режим медецинский Или универсальный

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
    public boolean onFahrenheit = false;
    //
    public boolean onMinVibration = false;
    public boolean onMaxVibration = false;
    public boolean onEndVibration = false;
    //
    public boolean onMinmelody = false;
    public boolean onMaxmelody = false;
    public boolean onEndmelody = false;
    public boolean goToConnect = false;//Устанавливается после того как отправляется на коннект!!чтоб повторно НЕ коннектить
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
    //
    private Handler changeValue = new Handler();
    private float getNewValue(float min, float max){
        return (float)( Math.random() * (max-min) + min);
    }
    private  void loop(){
        changeValue.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String val,rs,lb;
                if (mBluetoothDeviceAddress == null) {
                    intermediateValue = getNewValue(20f, 100f);
                    rssi = (int) getNewValue(40f, 100f);
                    battery_level = (int) getNewValue(0f, 100f);
                }
                //если имитация или КОННЕКТ
                if((mBluetoothDeviceAddress == null)
                    || (mConnectionState == BluetoothLeServiceNew.STATE_CONNECTED)){
                    val = getStringIntermediateValue(false,true);
                    rs = String.valueOf(rssi);
                }else{//если коннекта НЕТ
                    val = "-";
                    rs = "0";
                }
                lb = deviceLabel;
                if((deviceLabelView != null)&&(deviceLabelView instanceof TextView)) ((TextView)deviceLabelView).setText(lb);

                if((intermediateValueView != null)&&(intermediateValueView instanceof TextView)) {
                    ((TextView)intermediateValueView).setText(val);
                   // Log.v(TAG,"intermediateValue= " + val);
                }

                if((battery_levelView != null)&&(battery_levelView  instanceof ImageView)){

                    int i = battery_level;
                    if(i < 0) i = i*(-1);
            //        i = i/10;
        //            Log.v(TAG,"rssiView= " + i);
                    ImageView iv = (ImageView)battery_levelView;
                    iv.setImageLevel(i);
//
//                    // iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
//                    // TODO: 12.12.2016 переделать с указанием уровня отображения!! iv.setImageLevel();
//                switch(i){
//                        case 0: iv.setImageResource(R.drawable.ic_battery_alert_black_24dp);break;
//                        case 1: iv.setImageResource(R.drawable.ic_battery_alert_black_24dp);break;
//                        case 2: iv.setImageResource(R.drawable.ic_battery_20_black_24dp);break;
//                        case 3: iv.setImageResource(R.drawable.ic_battery_30_black_24dp);break;
//                        case 4: iv.setImageResource(R.drawable.ic_battery_30_black_24dp);break;
//                        case 5: iv.setImageResource(R.drawable.ic_battery_60_black_24dp);break;
//                        case 6: iv.setImageResource(R.drawable.ic_battery_60_black_24dp);break;
//                        case 7: iv.setImageResource(R.drawable.ic_battery_80_black_24dp);break;
//                        case 8: iv.setImageResource(R.drawable.ic_battery_90_black_24dp);break;
//                        default: iv.setImageResource(R.drawable.ic_battery_full_black_24dp);break;
//                    }
                }

                if((rssiView != null)&&(rssiView  instanceof ImageView)){
                    ImageView iv = (ImageView)rssiView;
                    int i = rssi;
                    //Отрицательное число уровеня- ломает вывод ИЗОБРАЖЕНИЯ по уровням
                    if(i < 0) i = i*(-1);

                    iv.setImageLevel(i);

//                    i = i/10;
//             //       Log.v(TAG,"rssiView= " + i);
//
//                   // iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
//                    switch(i){
//                        case 0: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 1: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 2: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 3: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 4: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 5: iv.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);break;
//                        case 6: iv.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_24dp);break;
//                        case 7: iv.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_24dp);break;
//                        case 8: iv.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_24dp);break;
//                        case 9: iv.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_24dp);break;
//                        default: iv.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_24dp);break;
//                    }
                }
                //
                if((markerColorView != null)&&(markerColorView instanceof TextView)
                        &&(markerColorView.getTag() == null)
                        ){
                    TextView tv = ((TextView)markerColorView);
                    switch(markerColor){
                        case 0: tv.setBackgroundResource(R.drawable.marker_white);break;
                        case 1: tv.setBackgroundResource(R.drawable.marker_red);break;
                        case 2: tv.setBackgroundResource(R.drawable.marker_yellow);break;
                        case 3: tv.setBackgroundResource(R.drawable.marker_green);break;
                        case 4: tv.setBackgroundResource(R.drawable.marker_cyan);break;
                        case 5: tv.setBackgroundResource(R.drawable.marker_blue);break;
                        case 6: tv.setBackgroundResource(R.drawable.marker_purple);break;
                        case 7: tv.setBackgroundResource(R.drawable.marker_black);break;
                    }
                 //   ((TextView)markerColorView).setBackgroundResource(Marker.marker.get(markerColor));
                    markerColorView.setTag((Object) 1);//чтоб не перезаписывать
                }
                loop();
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
    Sensor (){

        deviceLabel = deviceLabelStringDefault + " " + indexDevace++;
        markerColor = 0x7 & indexDevace;
        Log.v(TAG,"markerColor= " + markerColor);
        loop();
    }
    Sensor (final String adress){

        mBluetoothDeviceAddress = adress;
        deviceLabel = deviceLabelStringDefault + " " + indexDevace++;
        markerColor = 0x7 & indexDevace;
        loop();
    }
    Sensor (SharedPreferences mSettings){
        if(mSettings == null){
            deviceLabel = deviceLabelStringDefault + " " + indexDevace++;
            return;
        }
        markerColor = 0x7 & indexDevace;
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
        onFahrenheit = mSettings.getBoolean("onFahrenheit", onFahrenheit);
        onMinVibration = mSettings.getBoolean("onMinVibration", onMinVibration);
        onMaxVibration = mSettings.getBoolean("onMaxVibration", onMaxVibration);
        onEndVibration = mSettings.getBoolean("onEndVibration", onEndVibration);

        onMinmelody = mSettings.getBoolean("onMinmelody", onMinmelody);
        onMaxmelody = mSettings.getBoolean("onMaxmelody", onMaxmelody);
        onEndmelody = mSettings.getBoolean("onEndmelody", onEndmelody);
        loop();
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
        editor.putBoolean("onFahrenheit", onFahrenheit);
        editor.putBoolean("onMinVibration", onMinVibration);
        editor.putBoolean("onMaxVibration", onMaxVibration);
        editor.putBoolean("onEndVibration", onEndVibration);

        editor.putBoolean("onMinmelody", onMinmelody);
        editor.putBoolean("onMaxmelody", onMaxmelody);
        editor.putBoolean("onEndmelody", onEndmelody);
    }
    // TODO: 09.12.2016 МЛАДШИЙ разряд датчика температуры-to 0.0625°C, а выдает 0.1 точность, ГДЕ теряется? надо выдавать все, чтоб НЕ РЫСКАЛО
    // в будующем сделать порог  0.0625°C, чтоб показания не прыгали!
    public String getStringValue( float inp, boolean fahrenheit, boolean addType){
        final String str; float f= inp;
        if(fahrenheit) f = (f *9/5) + 32;// перевод в ФАРЕНГЕЙТА
        if(addType){
            if(fahrenheit) str = String.format("%2.1f °F",f);
            else str = String.format("%2.1f °C",f);
        }else str = String.format("%2.1f",f);
        return str;
    }
    public final String getStringIntermediateValue( boolean fahrenheit, boolean addType){
        return getStringValue( intermediateValue, fahrenheit, addType);
    }
    public String getStringMinTemperature( boolean fahrenheit, boolean addType){
        return getStringValue( minTemperature, fahrenheit, addType);
    }
    public String getStringMaxTemperature( boolean fahrenheit, boolean addType){
        return getStringValue( maxTemperature, fahrenheit, addType);
    }
    public String getStringEndTemperature( boolean fahrenheit, boolean addType){
        return getStringValue( endTemperature, fahrenheit, addType);
    }
    //C для градусника ПРЕСКАЗАНИЯ
    public String getStringPredictedTemperature( boolean fahrenheit, boolean addType){
        return getStringValue( predictedTemperature, fahrenheit, addType);
    }
    // TODO: 09.12.2016 перенести все это во внешний класс PartGatt, он должен все определять со значениями и отдавать в виде объект
    public String setValue(BluetoothGattCharacteristic characteristic){
        // TODO: 09.12.2016         // ПЕРЕДЕЛАТь для 16 рарядов, использовать свитчь -для нас ЗНАЧИМЫ старшие разряды характеристики для определение че это такое
        int charact = PartGatt.getCharacteristicInt(characteristic);//важны только 16 разрядов для определения сервиса и характеристики стандартного
        final String logStr,str;
        int flag = characteristic.getProperties();
        int format = -1;
        if ((PartGatt.UUID_INTERMEDIATE_TEMPERATURE.equals(characteristic.getUuid()))) {
            intermediateValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            str = String.format("%.2f", intermediateValue);
            Log.v(TAG, "TEMPERATURE: " + str + "  Properties= " + flag);
            //
           return str;
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
            Log.v(TAG, String.format("Received heart rate: %d  // ", heartRate,logStr));
            intermediateValue = (float)heartRate;
            // intent.putExtra(EXTRA_DATA, String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
            // sendsendBroadcastPutExtra(action,String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
            //

            return getStringIntermediateValue( false, false);
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
//            return;
//        }
        //-

//        //----ТОЛЬКО ЧТЕНИЕ----------////"Device Information Service":-character----------------------------------------
//        if ((PartGatt.UUID_PNP_ID.equals(characteristic.getUuid()))) {//uint8
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "PNP_ID: " + str + "  Properties= " + flag);
//            return str;
//        }
        //Здесь надо разбиратся -- чето НЕ пОНЯТНО формат вывода// https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list.xml
//        if ((PartGatt.UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST.equals(characteristic.getUuid()))) {//reg-cert-data-list
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            Log.d(TAG, String.format("IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: %d   Properties=%02X", temp,flag));
//            str = String.format("%d", temp);
//            Log.d(TAG, "IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: " + str + "  Properties= " + flag);
//            return str;
//        }
//        if ((PartGatt.UUID_SYSTEM_ID.equals(characteristic.getUuid()))) {////(​0x09 ?​0x0A ?)uint40/(0x07)uint24
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "UUID_SYSTEM_ID: " + str + "  Properties= " + flag);
//            return str;
//        }
        if ((PartGatt.UUID_MANUFACTURER_NAME_STRING.equals(characteristic.getUuid()))) {//string
            manufacturerName = characteristic.getStringValue(0);
            Log.d(TAG, "MANUFACTURER_NAME_STRING: " + manufacturerName + "  Properties= " + flag);
            return manufacturerName;
        }
        if ((PartGatt.UUID_MODEL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            modelNumber = characteristic.getStringValue(0);
            Log.d(TAG, "MODEL_NUMBER_STRING: " + modelNumber + "  Properties= " + flag);
            return modelNumber;
        }
        if ((PartGatt.UUID_SERIAL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            serialNumber = characteristic.getStringValue(0);
            Log.d(TAG, "SERIAL_NUMBER_STRING: " + serialNumber + "  Properties= " + flag);
            return serialNumber;
        }
        if ((PartGatt.UUID_SOFTWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
//            byte[] value = characteristic.getStringValue(0);
//            byte[] value = message.getBytes("UTF-8");
//            String temp = new String.(characteristic.getValue(),0);
//            str = temp.substring(0,temp.length() - 1);
            softwareRevision = characteristic.getStringValue(0);
            Log.d(TAG, "SOFTWARE_REVISION_STRING: " + softwareRevision + "  Properties= " + flag);
            return softwareRevision;
        }
        if ((PartGatt.UUID_FIRMWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            firmwareRevision = characteristic.getStringValue(0);
            Log.d(TAG, "FIRMWARE_REVISION_STRING: " + firmwareRevision + "  Properties= " + flag);
            return firmwareRevision;
        }
        if ((PartGatt.UUID_HARDWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            hardwareRevision = characteristic.getStringValue(0);
            Log.d(TAG, "HARDWARE_REVISION_STRING: " + hardwareRevision  + "  Properties= " + flag);
            return hardwareRevision;
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
            str = String.format("%d", battery_level);
            Log.d(TAG, "BATTERY_LEVEL(%%): " + str + "  Properties= " + logStr);
            return str;
        }
        //------------ Не поняли что это
        logStr = String.format("    Properties=%08X",flag);
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)    stringBuilder.append(String.format("%02X ", byteChar));
            // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            Log.v("service","length= " + data.length+ "  characteristic= " + stringBuilder.toString() + logStr);
            return str = new String(data) + "\n" + stringBuilder.toString();
        }
        return null;
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

    public  int loop_rssi  = 0;
    public void readRSSIandBatteryLevel(){
        if(mBluetoothGatt == null) return;
        // ЭТО для отладки--!!!
        //todo(loop_rssi++ & 0xFFFFFFC0) == 0)- В ТЕЧЕНИИ первой МИНУТЫ запросы будут идти КАЖДУЮ секунду!!!

        //каждые 16 сек смотрим уровень сигнала
        loop_rssi++;
        if(((loop_rssi & 0x0F) == 1) || ((loop_rssi & 0xFFFFFFC0) == 0)) {
            mBluetoothGatt.readRemoteRssi();
            //           Log.w(TAG, "enableRXNotification: loop_rssi -- ");
        }
        //каждые 2 минуты уровень батареи
        if(((loop_rssi & 0x7F) == 2) || ((loop_rssi & 0xFFFFFFC3) == 2)){//Чаше чем 1 раз в 4 секунды НЕ надо, захлебывается
            BluetoothGattCharacteristic characteristic;
            BluetoothGattService service;
            //читать уровень батареи
            service = mBluetoothGatt.getService(PartGatt.UUID_BATTERY_SERVICE);
            if (service == null) {
                Log.e(TAG, "SetRead: BATTERY Service not found!");
                return;//сервиса нет в этом устройстве
            }//читать уровень батареи
            characteristic = service.getCharacteristic(PartGatt.UUID_BATTERY_LEVEL);
            mBluetoothGatt.readCharacteristic(characteristic);
            Log.e(TAG, "R: BATTERY Service -- ");
        }
    }

    //-------------------------------
}
