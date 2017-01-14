package com.portfolio.alexey.connector.command;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.portfolio.alexey.connector.BluetoothLeServiceNew;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import java.util.UUID;

/**
 * Created by lesa on 14.01.2017.
 */

public abstract class Command {
    private final static String TAG = Sensor.class.getSimpleName();
    private final static boolean debug = true;
    public enum TypeCommand {
        ReadCharacteristic//чтение характеристики
        ,WriteCharacteristic//запись характеристики
        ,WriteDescriptor//запись дескриптора
        ,DiscoverServices//запрос у сенсора сервисов и характеристик
        ,Connect//запрос коннекта
        ,DisconnectClose//запрос дисконнекта
        ,Timer //временная пауза - используется для окончания переходных процессов или состояний
    }

    public final  Sensor sensor;
    public final  BluetoothGattCharacteristic characteristic;
    public final  BluetoothGattDescriptor descriptor;
    public final byte[] dataToWrite; // Only used for characteristic write
    public final boolean enabled; // Only used for characteristic notification subscription
    public final  TypeCommand typeCommand;
    public int retry = 0;//количество повторов запроса
    public  final  int timerMsek;//длительность задержки Тайм аута

    //команды Connect, DisconnectClose, DiscoverServices ...
    public Command(Sensor sensor_, TypeCommand typeCommand_,BluetoothGattCharacteristic characteristic_,
                   BluetoothGattDescriptor descriptor_, byte[] dataToWrite_, boolean enabled_, int timerMsek_) {
        typeCommand = typeCommand_;
        sensor = sensor_;
        characteristic = characteristic_;
        dataToWrite = dataToWrite_;
        enabled = enabled_;
        descriptor = descriptor_;
        timerMsek = timerMsek_;
    }
    //выполнить комманду
    public abstract void execute();
    public abstract void ifReset();

    protected boolean filtrInTxQueue(Sensor sensor_, TypeCommand typeCommand_, UUID uuid_, int status_){

        if(status_ != BluetoothGatt.GATT_SUCCESS)  return false;

        if(typeCommand_ != typeCommand) {
            if(debug) Log.e(TAG,"Input typeCommand != typeCommand");
            return false;
        }
        if((sensor_ != null) && (sensor != null)
        && (sensor_.getAddress().compareTo(sensor.getAddress()) != 0)) {
            if(debug) Log.e(TAG,"compareTo(sensor.getAddress()) != 0");
            return false;
        }
        if((characteristic != null) && (uuid_ != null)){
            if(characteristic.getUuid().compareTo(uuid_) != 0) {
                if(debug) Log.e(TAG,"getUuid().compareTo(uuid) != 0)");
                return false;
            }
        }
//для теста блокировал уровень батареи посмотрет повтор запроса, работает
//        if(uuid.compareTo(PartGatt.UUID_BATTERY_LEVEL) == 0) {
//            log("getUuid().UUID_BATTERY_LEVEL");
//            return;
//        }
        //это то что мы запросили!!
       // processTxQueue(false);//это обратная связь для сброса переданного значения
        if(debug) Log.i(TAG,"Ok  " + toString());
        return true;
    }


    @Override
    public String toString() {
        //return super.toString();
        String str= "   adress= "+ sensor.getAddress() + "   type= " + typeCommand + "   retry= "+retry;
        if(characteristic == null) return str;
        else    return str + "   characteristic"+ Util.getUidStringMost16Bits(characteristic);
    }
}
