package com.portfolio.alexey.connector.command;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.portfolio.alexey.connector.Sensor;

/**
 * Created by lesa on 14.01.2017.
 */

public class CommandTimer extends Command {

    public CommandTimer(Sensor sensor_, int timerMsek_){
        super(sensor_, TypeCommand.Timer,null,null, null, false,timerMsek_);
    }

    @Override
    public void execute() {
        ;
    }

    @Override
    public void ifReset() {
        ;
    }
}
