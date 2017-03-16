package com.jacktheogre.lightswitch.multiplayer;

/**
 * Created by luna on 12.02.17.
 */

public class NullBluetoothManager implements com.jacktheogre.lightswitch.multiplayer.iBluetooth {
    @Override
    public void enableBluetooth() {

    }

    @Override
    public void enableDiscoveribility() {

    }

    @Override
    public void discoverDevices() {

    }

    @Override
    public void stopDiscovering() {

    }

    @Override
    public boolean startServer() {
        return false;
    }

    @Override
    public void connectToServer() {

    }

    @Override
    public String getTest() {
        return null;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean canConnect() {
        return false;
    }

    @Override
    public void switchToNextDevice() {

    }

    @Override
    public void switchToPrevDevice() {

    }

    @Override
    public String getDevice() {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isDiscovering() {
        return false;
    }

    @Override
    public Object[] getDevices() {
        return new Object[0];
    }
}
