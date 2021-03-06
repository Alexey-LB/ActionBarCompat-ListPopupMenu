package com.portfolio.alexey.connector;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.StaticLayout;
import android.util.Log;

import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by lesa on 07.12.2016.
 */

public class PartGatt {
    private final static String TAG = PartGatt.class.getSimpleName();
    private static TreeMap<UUID, String> attributes = new TreeMap();

    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG   =
            UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb");
    //Servise -------------------------------------
    public final static UUID UUID_GENERIC_ACCESS  =
            UUID.fromString( "00001800-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_GENERIC_ATTRIBUTE  =
            UUID.fromString( "00001801-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_HEART_RATE_SERVICE  =
            UUID.fromString( "0000180d-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_HEALTH_THERMOMETER  =
            UUID.fromString( "00001809-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_DEVICE_INFORMATION_SERVICE  =
            UUID.fromString( "0000180a-0000-1000-8000-00805f9b34fb");//--ТОЛЬКО ЧТЕНИЕ---
    public final static UUID UUID_BATTERY_SERVICE  =
            UUID.fromString( "0000180f-0000-1000-8000-00805f9b34fb");

    public final static UUID UUID_RELSIB_SERVICE  =
            UUID.fromString( "0177aaa0-b455-0e17-d0da-14ea33f8de11");
//----------------character---character---character-----------------------------------
    //Прогнозируемая температура (ИНДИКАЦИЯ)
    //    Значение передается после вычисления первого прогнозируемого значения
    // (примерно через 2 мин после начала измерения). При дальнейшем измерении температуры,
    // прогнозируемое значение уточняется и передается снова.
    // Формат передаваемого значения прогнозируемой температуры аналогичен формату измеренного
    // значения температуры, передаваемого по HTP профилю.
    public final static UUID UUID_RELSIB_TEMP  =
            UUID.fromString("0177aaa1-b455-0e17-d0da-14ea33f8de11");
    //Серийный номер Термометра (16 байт запись)
    //Первые 12 байт - серийный номер термометра, отображаемый в профиле Device Information Service.
    public final static UUID     UUID_RELSIB_SERIAL_NUMBER  =
            UUID.fromString("0177aaa2-b455-0e17-d0da-14ea33f8de11");
    //Флаг калибровки (5 байт запись)
    //Первый байт параметра определяет выполняемое действие. Запись 1 приводит к вычислению
    // калибровочного коэффициента и записи его в энергонезависимую память. Запись 0 приводит
    // к сбросу калибровочного коэффициента на значение «по умолчанию».
    public final static UUID     UUID_RELSIB_CALIBR_FLAG  =
            UUID.fromString("0177aaa3-b455-0e17-d0da-14ea33f8de11");
    //Признак калибровки (1 байт чтение)
    //Значение параметра инкрементируется на единицу после каждой калибровки термометра.
    // Параметр необходим для контроля количества проведенных операций калибровки и выявления
    // не санкционированной операции.
    public final static UUID     UUID_RELSIB_CALIBR_NUMBER  =
            UUID.fromString("0177aaa4-b455-0e17-d0da-14ea33f8de11");
    //Дата калибровки (5 байт чтение /запись)
    //В каждом байте параметра  отдельно записывается: Год (14-99) , Месяц (1-12), Число (1-31),
    // Час (0-23), Минута (0-59)
    public final static UUID   UUID_RELSIB_CALIBR_DATE  =
            UUID.fromString("0177aaa5-b455-0e17-d0da-14ea33f8de11");
    //Флаг сброса последнего измеренного значения температуры (1 байт чтение/запись)
    //Запись 1 приводит к сбросу последнего измеренного значения температуры,
    // после сброса флаг устанавливается в 0.
    public final static UUID   UUID_RELSIB_RESET_MEAS_FLAG  =
            UUID.fromString("0177aaa6-b455-0e17-d0da-14ea33f8de11");
    // Флаг очистки списка зарегистрированных ведущих устройств (1 байт- чтение / запись)
    // Параметр не используется, зарезервировано для дальнейших модификаций
    public final static UUID   UUID_RELSIB_WHITE_LIST_FLAG  =
            UUID.fromString("0177aaa7-b455-0e17-d0da-14ea33f8de11");
    //Отключение термометра (1 байт чтение/запись)
    //Запись 1 приводит к отключению термометра.
    public final static UUID  UUID_RELSIB_SHUTDOWN  =
            UUID.fromString("0177aaa8-b455-0e17-d0da-14ea33f8de11");
    //Режим работы Термометр (1 байт чтение/запись)
    // 0 - универсальный режим, 1 - медицинский режим, 2 – логгер.  Параметр не используется, зарезервировано для дальнейших модификаций
    public final static UUID  UUID_RELSIB_MODE  =
            UUID.fromString("0177aaa9-b455-0e17-d0da-14ea33f8de11");
            //============

    public final static UUID UUID_HEART_RATE_MEASUREMENT  =
            UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");//сердцебиение и длительность между ударами в мс
    //
    public final static UUID UUID_INTERMEDIATE_TEMPERATURE  =
            UUID.fromString( "00002a1e-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_TEMPERATURE_MEASUREMENT  =
            UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");

    public final static UUID UUID_BATTERY_LEVEL  =
            UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    ////"Device Information Service":-character--
    public final static UUID UUID_SOFTWARE_REVISION_STRING  =
            UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_FIRMWARE_REVISION_STRING  =
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_HARDWARE_REVISION_STRING  =
            UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERIAL_NUMBER_STRING  =
            UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_MODEL_NUMBER_STRING  =
            UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_MANUFACTURER_NAME_STRING  =
            UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");

    static {
        //Heart Rate
        attributes.put(UUID_HEART_RATE_MEASUREMENT, "Heart Rate Measurement");

        attributes.put(UUID_CLIENT_CHARACTERISTIC_CONFIG, "Client Characteristic Config");
        // Sample Services.
        attributes.put(UUID_GENERIC_ACCESS , "Generic Access");
        attributes.put(UUID_GENERIC_ATTRIBUTE, "Generic Attribute");
        attributes.put(UUID_HEART_RATE_SERVICE, "Heart Rate Service");
        attributes.put(UUID_HEALTH_THERMOMETER, "Health DrawableThermometer");
        attributes.put(UUID_DEVICE_INFORMATION_SERVICE, "Device Information Service");//--ТОЛЬКО ЧТЕНИЕ---
        attributes.put(UUID_BATTERY_SERVICE, "Battery Service");

        // Sample Characteristics.
        attributes.put(UUID_BATTERY_LEVEL, "Battery Level");
        // "Health DrawableThermometer"
        attributes.put(UUID_TEMPERATURE_MEASUREMENT, "Temperature measurement");
        attributes.put(UUID_INTERMEDIATE_TEMPERATURE, "Intermediate temperature");

//"Device Information Service":-character--
        attributes.put(UUID_SOFTWARE_REVISION_STRING, "Software Revision String");
        attributes.put(UUID_FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(UUID_HARDWARE_REVISION_STRING, "Hardware Revision String");
        attributes.put(UUID_SERIAL_NUMBER_STRING, "Serial Number String");
        attributes.put(UUID_MODEL_NUMBER_STRING,"Model Number String");
        attributes.put(UUID_MANUFACTURER_NAME_STRING, "Manufacturer Name String");
        //---
    }
    //важны только 16 разрядов для определения сервиса и характеристики стандартного
    static  public int getCharacteristicInt(BluetoothGattCharacteristic characteristic){
        return (int)(characteristic.getUuid().getMostSignificantBits() >> 32);
    }
    static public String getValue(final BluetoothGattCharacteristic characteristic) {
        //
        final String str;String logStr;
        int flag = characteristic.getProperties();
        int format = -1;
//        // carried out as per profile specifications:
//        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if ((UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()))) {
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
//            final int heartRR1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
//            final int heartRR2 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            // intent.putExtra(EXTRA_DATA, String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
//            sendsendBroadcastPutExtra(action,String.valueOf(heartRate) +" / "+ String.valueOf(heartRR1) +"/"+ String.valueOf(heartRR1));
            str = String.format("%d",heartRate);
            Log.d(TAG, "Heart rate: " + str + "  Properties= " + flag);
            return str;
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
//            return;
//        }
        //-
        if ((UUID_INTERMEDIATE_TEMPERATURE.equals(characteristic.getUuid()))) {
            float temp = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            str = String.format("%.1f", temp);
            Log.d(TAG, "TEMPERATURE: " + str + "  Properties= " + flag);
            return str;
        }
//        //----ТОЛЬКО ЧТЕНИЕ----------////"Device Information Service":-character----------------------------------------
//        if ((UUID_PNP_ID.equals(characteristic.getUuid()))) {//uint8
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "PNP_ID: " + str + "  Properties= " + flag);
//            return str;
//        }
        //Здесь надо разбиратся -- чето НЕ пОНЯТНО формат вывода// https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list.xml
//        if ((UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST.equals(characteristic.getUuid()))) {//reg-cert-data-list
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            Log.d(TAG, String.format("IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: %d   Properties=%02X", temp,flag));
//            str = String.format("%d", temp);
//            Log.d(TAG, "IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST: " + str + "  Properties= " + flag);
//            return str;
//        }
//        if ((UUID_SYSTEM_ID.equals(characteristic.getUuid()))) {////(​0x09 ?​0x0A ?)uint40/(0x07)uint24
//            final int temp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
//            str = String.format("%d", temp);
//            Log.d(TAG, "UUID_SYSTEM_ID: " + str + "  Properties= " + flag);
//            return str;
//        }
        if ((UUID_SOFTWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "SOFTWARE_REVISION_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        if ((UUID_FIRMWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "FIRMWARE_REVISION_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        if ((UUID_HARDWARE_REVISION_STRING.equals(characteristic.getUuid()))) {//string
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "HARDWARE_REVISION_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        if ((UUID_SERIAL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "SERIAL_NUMBER_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        if ((UUID_MODEL_NUMBER_STRING.equals(characteristic.getUuid()))) {//string
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "MODEL_NUMBER_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        if ((UUID_MANUFACTURER_NAME_STRING.equals(characteristic.getUuid()))) {//string
            final byte[] b = characteristic.getValue();
            String temp = new String(characteristic.getValue());
            str = temp.substring(0,temp.length() - 1);
            Log.d(TAG, "MANUFACTURER_NAME_STRING: " + str + "  Properties= " + flag);
            return str;
        }
        //---------------------------------------------------------
        //https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.battery_service.xml
        //-----------------------------это 1 байт от 0 100% батареи, 0%- батарея разряжена
        //дескриптор 12- знаковое 8 разрядное число!означает
        // https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.descriptor.gatt.characteristic_presentation_format.xml

        if ((UUID_BATTERY_LEVEL.equals(characteristic.getUuid()))) {
            if ((flag & 0x08) != 0) {//12-знаковое 8 разрядов
                format = BluetoothGattCharacteristic.FORMAT_SINT8;
                logStr = String.format("format SINT8 (getProperties=%d)",flag);
            } else {//4-без знаковое 8 разрядов
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                logStr = String.format("format UINT8(getProperties=%d)",flag);
            }
            final int temp = characteristic.getIntValue(format, 0);
            str = String.format("%d", temp);
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
            str = new String(data) + "\n" + stringBuilder.toString();
            return str;
        }
        return "";
    }
    public static String lookup(UUID uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    public static String lookup(String  uuid, String defaultName) {
        String name = attributes.get(UUID.fromString(uuid));
        return name == null ? defaultName : name;
    }
}
