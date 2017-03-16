package com.jacktheogre.lightswitch.replay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 08.02.17.
 */

public class Logger {

    //handles all the file operations.
    //creates an object at CommandHandler

    private FileHandle fileHandle;

    public Logger() {
        fileHandle = Assets.getAssetLoader().fileHandle;
        clearLog();

    }

    public void clearLog() {
        fileHandle.writeString("", false);
    }

    public String readFromFile() {
        return fileHandle.readString();
    }

    public void logStringToFile(String s) {
        fileHandle.writeString(s, true);
    }

    public void printLog() {
        Gdx.app.log("Logger", readFromFile());
    }
}
