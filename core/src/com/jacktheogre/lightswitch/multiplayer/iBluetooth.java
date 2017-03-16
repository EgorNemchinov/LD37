package com.jacktheogre.lightswitch.multiplayer;

import java.util.LinkedList;

/**
 * Created by luna on 09.02.17.
 */
public interface iBluetooth {
    void enableBluetooth();
    void enableDiscoveribility();
    void discoverDevices();
    void stopDiscovering();
    boolean startServer();
    void connectToServer();
    String getTest();
    void sendMessage(String message);
    String getMessage();
    boolean isConnected();
    boolean canConnect();
    void switchToNextDevice();
    void switchToPrevDevice();
    String getDevice();
    void stop();
    boolean isFirst();
    boolean isLast();
    boolean isDiscovering();
    Object[] getDevices();
}