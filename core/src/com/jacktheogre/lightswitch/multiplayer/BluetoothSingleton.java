package com.jacktheogre.lightswitch.multiplayer;

import com.jacktheogre.lightswitch.tools.NullBluetoothManager;
import com.jacktheogre.lightswitch.tools.iBluetooth;

/**
 * Created by luna on 09.02.17.
 */

public class BluetoothSingleton {

    private static volatile BluetoothSingleton instance = null;
    public iBluetooth bluetoothManager;

    /* METHODS */
    public static BluetoothSingleton getInstance() {
        if (instance == null) {
            synchronized (BluetoothSingleton.class) {
                if (instance == null) {
                    instance = new BluetoothSingleton();
                }
            }
        }
        return instance;
    }

    public iBluetooth getBluetoothManager() {
        if(bluetoothManager == null) {
            bluetoothManager = new NullBluetoothManager();
        }
        return bluetoothManager;
    }

    private BluetoothSingleton() {

    }

}

